package com.evaluacion.kairos.infrastructure.outbound.http;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.infrastructure.outbound.http.dto.TvMazeSearchItemDto;
import com.evaluacion.kairos.infrastructure.outbound.http.dto.TvMazeShowDto;
import com.evaluacion.kairos.ports.out.TvMazeClientPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TvMazeClientAdapter implements TvMazeClientPort {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Show> searchShows(String query) {
        String url = "http://api.tvmaze.com/search/shows?q=" + query;
        try {
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

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Show> getShowById(Long id) {
        String url = "https://api.tvmaze.com/shows/" + id;
        try {
            TvMazeShowDto showDto = restTemplate.getForObject(url, TvMazeShowDto.class);
            if (showDto == null) return Optional.empty();

            return Optional.of(mapToDomain(showDto));
        } catch (Exception e) {
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