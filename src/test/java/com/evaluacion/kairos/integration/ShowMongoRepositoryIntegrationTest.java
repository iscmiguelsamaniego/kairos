package com.evaluacion.kairos.integration;

import com.evaluacion.kairos.infrastructure.outbound.persistence.ShowEntity;
import com.evaluacion.kairos.infrastructure.outbound.persistence.ShowMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class ShowMongoRepositoryIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private ShowMongoRepository showMongoRepository;

    @BeforeEach
    void setUp() {
        showMongoRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: ShowMongoRepository - Successfully saves and finds ShowEntity by ID")
    void shouldSaveAndFindShowEntityDirectly() {
        // Arrange
        ShowEntity entity = new ShowEntity();
        entity.setId(100L);
        entity.setName("The Wire");
        entity.setChannel("HBO");
        entity.setSummary("<p>Baltimore drug scene.</p>");
        entity.setGenres(List.of("Crime", "Drama"));

        // Act
        showMongoRepository.save(entity);
        Optional<ShowEntity> foundEntity = showMongoRepository.findById(100L);

        // Assert
        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getId()).isEqualTo(100L);
        assertThat(foundEntity.get().getName()).isEqualTo("The Wire");
        assertThat(foundEntity.get().getChannel()).isEqualTo("HBO");
        assertThat(foundEntity.get().getGenres()).containsExactly("Crime", "Drama");
    }

    @Test
    @DisplayName("Integration: ShowMongoRepository - Deletes entity correctly")
    void shouldDeleteShowEntity() {
        // Arrange
        ShowEntity entity = new ShowEntity();
        entity.setId(200L);
        entity.setName("Temporary Show");
        showMongoRepository.save(entity);

        // Act
        showMongoRepository.deleteById(200L);
        Optional<ShowEntity> foundEntity = showMongoRepository.findById(200L);

        // Assert
        assertThat(foundEntity).isEmpty();
    }
}