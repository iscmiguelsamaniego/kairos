package com.evaluacion.kairos.integration;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.infrastructure.outbound.persistence.ShowMongoRepository;
import com.evaluacion.kairos.infrastructure.outbound.persistence.ShowRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@Import(ShowRepositoryAdapter.class)
class ShowRepositoryAdapterIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private ShowRepositoryAdapter showRepositoryAdapter;

    @Autowired
    private ShowMongoRepository showMongoRepository;

    @BeforeEach
    void setUp() {
        showMongoRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: Save and FindById - Persists domain show and retrieves it successfully")
    void shouldSaveAndFindShowById() {
        // Arrange
        Show show = new Show(
                1L,
                "Breaking Bad",
                "AMC",
                "<p>A chemistry teacher turned methamphetamine producer.</p>",
                List.of("Drama", "Crime")
        );

        // Act
        showRepositoryAdapter.save(show);
        Optional<Show> retrievedShow = showRepositoryAdapter.findById(1L);

        // Assert
        assertThat(retrievedShow).isPresent();
        assertThat(retrievedShow.get().id()).isEqualTo(1L);
        assertThat(retrievedShow.get().name()).isEqualTo("Breaking Bad");
        assertThat(retrievedShow.get().channel()).isEqualTo("AMC");
        assertThat(retrievedShow.get().genres()).containsExactly("Drama", "Crime");
    }

    @Test
    @DisplayName("Integration: FindById - Returns empty optional when show does not exist in MongoDB")
    void shouldReturnEmptyWhenShowNotFound() {
        // Act
        Optional<Show> retrievedShow = showRepositoryAdapter.findById(999L);

        // Assert
        assertThat(retrievedShow).isEmpty();
    }
}