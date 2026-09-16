package com.evaluacion.kairos.infrastructure.outbound.http;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.infrastructure.outbound.http.dto.TvMazeSearchItemDto;
import com.evaluacion.kairos.infrastructure.outbound.http.dto.TvMazeShowDto;
import com.evaluacion.kairos.ports.out.TvMazeClientPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TvMazeClientAdapter implements TvMazeClientPort {

    private static final Logger log = LoggerFactory.getLogger(TvMazeClientAdapter.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public TvMazeClientAdapter(
            RestTemplate restTemplate,
            @Value("${tvmaze.api.url:https://api.tvmaze.com}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<Show> tvmazeFallback(String query, Throwable t) {
        log.warn("Circuito abierto o fallo al buscar shows con la query '{}'. Motivo: {}", query, t.getMessage());
        return Collections.emptyList();
    }

    public Optional<Show> fallbackGetShowById(Long id, Throwable t) {
        log.warn("Circuito abierto o fallo al obtener el show con ID {}. Motivo: {}", id, t.getMessage());
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "tvmazeService", fallbackMethod = "tvmazeFallback")
    public List<Show> searchShows(String query) {
        String url = baseUrl + "/search/shows?q=" + query;

        ResponseEntity<List<TvMazeSearchItemDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        List<TvMazeSearchItemDto> body = response.getBody();
        if (body == null) return Collections.emptyList();

        return body.stream()
                .filter(item -> item.show() != null)
                .map(item -> mapToDomain(item.show()))
                .toList();
    }

    @Override
    @CircuitBreaker(name = "tvmazeService", fallbackMethod = "fallbackGetShowById")
    public Optional<Show> getShowById(Long id) {
        String url = baseUrl + "/shows/" + id;
        try {
            TvMazeShowDto showDto = restTemplate.getForObject(url, TvMazeShowDto.class);
            return Optional.ofNullable(showDto).map(this::mapToDomain);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    private Show mapToDomain(TvMazeShowDto dto) {
        return new Show(
                dto.id(),
                dto.name(),
                dto.getChannelName(),
                dto.summary(),
                dto.genres()
        );
    }
}