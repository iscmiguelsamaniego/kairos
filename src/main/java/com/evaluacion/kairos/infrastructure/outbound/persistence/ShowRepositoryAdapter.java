package com.evaluacion.kairos.infrastructure.outbound.persistence;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.out.ShowRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ShowRepositoryAdapter implements ShowRepositoryPort {

    private final ShowMongoRepository mongoRepository;

    public ShowRepositoryAdapter(ShowMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Optional<Show> findById(Long id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Show save(Show show) {
        ShowEntity entity = toEntity(show);
        ShowEntity saved = mongoRepository.save(entity);
        return toDomain(saved);
    }

    private Show toDomain(ShowEntity entity) {
        return new Show(
                entity.getId(),
                entity.getName(),
                entity.getChannel(),
                entity.getSummary(),
                entity.getGenres()
        );
    }

    private ShowEntity toEntity(Show show) {
        ShowEntity entity = new ShowEntity();
        entity.setId(show.id());
        entity.setName(show.name());
        entity.setChannel(show.channel());
        entity.setSummary(show.summary());
        entity.setGenres(show.genres());
        return entity;
    }
}