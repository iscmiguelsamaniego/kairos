package com.evaluacion.kairos.application;
import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.out.TvMazeClientPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowUseCaseTest {
    @Mock
    private TvMazeClientPort tvMazeClientPort;

    @InjectMocks
    private ShowUseCase showUseCase;

    @Test
    @DisplayName("Should return list of shows when search query is valid")
    void shouldReturnShowsWhenQueryIsValid() {
        // Arrange
        String query = "batman";
        List<Show> mockShows = List.of(
                new Show(1L, "Batman", "Fox", "Dark knight show", List.of("Action", "Drama"))
        );
        when(tvMazeClientPort.searchShows(query)).thenReturn(mockShows);

        // Act
        List<Show> result = showUseCase.searchShows(query);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Batman", result.get(0).name());
        assertEquals("Fox", result.get(0).channel());
    }
}