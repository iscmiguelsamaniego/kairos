package com.evaluacion.kairos.application;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.out.TvMazeClientPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowUseCaseTest {

    @Mock
    private TvMazeClientPort tvMazeClientPort;

    @InjectMocks
    private ShowUseCase showUseCase;

    @Nested
    @DisplayName("searchShows Tests")
    class SearchShowsTests {

        @Test
        @DisplayName("Should return list of shows when search query is valid")
        void shouldReturnShowsWhenQueryIsValid() {
            // Arrange
            String query = "Batman";
            Show mockShow = new Show(1L, "Batman", "Fox", "Dark knight show", List.of("Action"));
            when(tvMazeClientPort.searchShows(query)).thenReturn(List.of(mockShow));

            // Act
            List<Show> result = showUseCase.searchShows(query);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Batman", result.get(0).name());
            verify(tvMazeClientPort, times(1)).searchShows(query);
        }

        @Test
        @DisplayName("Should return empty list when no shows match the search query")
        void shouldReturnEmptyListWhenNoShowsFound() {
            // Arrange
            String query = "UnknownShowQuery123";
            when(tvMazeClientPort.searchShows(query)).thenReturn(Collections.emptyList());

            // Act
            List<Show> result = showUseCase.searchShows(query);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(tvMazeClientPort, times(1)).searchShows(query);
        }
    }

    @Nested
    @DisplayName("getShowById Tests")
    class GetShowByIdTests {

        @Test
        @DisplayName("Should return show details when valid ID exists")
        void shouldReturnShowWhenIdExists() {
            // Arrange
            Long showId = 1L;
            Show mockShow = new Show(showId, "Under the Dome", "CBS", "Drama show", List.of("Drama"));
            when(tvMazeClientPort.getShowById(showId)).thenReturn(Optional.of(mockShow));

            // Act
            Show result = showUseCase.getShowById(showId);

            // Assert
            assertNotNull(result);
            assertEquals(showId, result.id());
            assertEquals("Under the Dome", result.name());
            verify(tvMazeClientPort, times(1)).getShowById(showId);
        }

        @Test
        @DisplayName("Should throw RuntimeException when show ID is not found")
        void shouldThrowExceptionWhenShowNotFound() {
            // Arrange
            Long showId = 999L;
            when(tvMazeClientPort.getShowById(showId)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> showUseCase.getShowById(showId));

            assertEquals("Show not found", exception.getMessage());
            verify(tvMazeClientPort, times(1)).getShowById(showId);
        }
    }
}