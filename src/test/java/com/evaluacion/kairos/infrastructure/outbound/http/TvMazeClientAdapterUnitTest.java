package com.evaluacion.kairos.infrastructure.outbound.http;

import com.evaluacion.kairos.domain.Show;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TvMazeClientAdapterUnitTest {

    private TvMazeClientAdapter clientAdapter;

    @BeforeEach
    void setUp() {
        clientAdapter = new TvMazeClientAdapter();
    }

    @Test
    @DisplayName("Unit: tvmazeFallback - Returns empty list and logs warning on failure")
    void shouldReturnEmptyListOnFallbackSearch() {
        // Arrange
        RuntimeException ex = new RuntimeException("Service unavailable");

        // Act
        List<Show> result = clientAdapter.tvmazeFallback("batman", ex);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Unit: fallbackGetShowById - Returns empty optional and logs warning on failure")
    void shouldReturnEmptyOptionalOnFallbackGetById() {
        // Arrange
        RuntimeException ex = new RuntimeException("Timeout error");

        // Act
        Optional<Show> result = clientAdapter.fallbackGetShowById(1L, ex);

        // Assert
        assertThat(result).isEmpty();
    }
}