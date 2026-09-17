package com.evaluacion.kairos.integration;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.infrastructure.outbound.persistence.ShowMongoRepository;
import com.evaluacion.kairos.infrastructure.outbound.persistence.ShowRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@Import(ShowRepositoryAdapter.class)
class ShowRepositoryAdapterIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ShowRepositoryAdapter showRepositoryAdapter;

    @Autowired
    private ShowMongoRepository showMongoRepository;

    @BeforeEach
    void setUp() {
        showMongoRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: ShowRepositoryAdapter - Successfully persists domain show and retrieves it by ID")
    void shouldSaveAndFindShowByIdSuccessfully() {
        // Arrange
        Show showToSave = new Show(
                10L,
                "Breaking Bad",
                "AMC",
                "<p>A chemistry teacher turned methamphetamine producer.</p>",
                List.of("Drama", "Crime")
        );

        // Act
        Show savedShow = showRepositoryAdapter.save(showToSave);
        Optional<Show> retrievedShowOpt = showRepositoryAdapter.findById(10L);

        // Assert
        assertThat(savedShow).isNotNull();
        assertThat(savedShow.id()).isEqualTo(10L);

        assertThat(retrievedShowOpt)
                .isPresent();

        Show retrievedShow = retrievedShowOpt.get();
        assertThat(retrievedShow.id()).isEqualTo(10L);
        assertThat(retrievedShow.name()).isEqualTo("Breaking Bad");
        assertThat(retrievedShow.channel()).isEqualTo("AMC");
        assertThat(retrievedShow.summary()).contains("chemistry teacher");
        assertThat(retrievedShow.genres()).containsExactly("Drama", "Crime");
    }

    @Test
    @DisplayName("Integration: ShowRepositoryAdapter - Returns empty Optional when show ID does not exist")
    void shouldReturnEmptyOptionalWhenShowNotFound() {
        // Act
        Optional<Show> retrievedShowOpt = showRepositoryAdapter.findById(9999L);

        // Assert
        assertThat(retrievedShowOpt).isEmpty();
    }
}