package com.evaluacion.kairos.application;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.out.CommentRepositoryPort;
import com.evaluacion.kairos.ports.out.ShowRepositoryPort;
import com.evaluacion.kairos.ports.out.TvMazeClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ShowUseCaseTest {

    @Mock
    private TvMazeClientPort tvMazeClientPort;

    @Mock
    private ShowRepositoryPort showRepositoryPort;

    @Mock
    private CommentRepositoryPort commentRepositoryPort;

    @InjectMocks
    private ShowUseCase showUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Unit: searchShows - Delegates directly to external client port")
    void shouldSearchShows() {
        // Arrange
        String query = "batman";
        List<Show> expectedShows = List.of(new Show(1L, "Batman", "Fox", "Summary", List.of("Action")));
        when(tvMazeClientPort.searchShows(query)).thenReturn(expectedShows);

        // Act
        List<Show> actualShows = showUseCase.searchShows(query);

        // Assert
        assertThat(actualShows).isEqualTo(expectedShows);
        verify(tvMazeClientPort, times(1)).searchShows(query);
    }

    @Test
    @DisplayName("Unit: getShowById - Returns from repository if cache hit")
    void shouldGetShowFromCacheIfPresent() {
        // Arrange
        Long showId = 1L;
        Show cachedShow = new Show(showId, "Cached Show", "CBS", "Summary", List.of("Drama"));
        when(showRepositoryPort.findById(showId)).thenReturn(Optional.of(cachedShow));

        // Act
        Show result = showUseCase.getShowById(showId);

        // Assert
        assertThat(result).isEqualTo(cachedShow);
        verify(showRepositoryPort, times(1)).findById(showId);
        verify(tvMazeClientPort, never()).getShowById(anyLong());
        verify(showRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Unit: getShowById - Fetches from external API and caches if cache miss")
    void shouldFetchFromApiAndCacheOnCacheMiss() {
        // Arrange
        Long showId = 1L;
        Show externalShow = new Show(showId, "External Show", "NBC", "Summary", List.of("Comedy"));
        when(showRepositoryPort.findById(showId)).thenReturn(Optional.empty());
        when(tvMazeClientPort.getShowById(showId)).thenReturn(Optional.of(externalShow));
        when(showRepositoryPort.save(externalShow)).thenReturn(externalShow);

        // Act
        Show result = showUseCase.getShowById(showId);

        // Assert
        assertThat(result).isEqualTo(externalShow);
        verify(showRepositoryPort, times(1)).findById(showId);
        verify(tvMazeClientPort, times(1)).getShowById(showId);
        verify(showRepositoryPort, times(1)).save(externalShow);
    }

    @Test
    @DisplayName("Unit: getShowById - Throws RuntimeException when show is not found anywhere")
    void shouldThrowExceptionWhenShowNotFound() {
        // Arrange
        Long showId = 99L;
        when(showRepositoryPort.findById(showId)).thenReturn(Optional.empty());
        when(tvMazeClientPort.getShowById(showId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> showUseCase.getShowById(showId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Show not found with ID: 99");

        verify(showRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Unit: addComment - Validates show existence and saves comment successfully")
    void shouldAddCommentSuccessfully() {
        // Arrange
        Long showId = 1L;
        String commentText = "Great show!";
        Integer rating = 5;
        Show show = new Show(showId, "Show", "Network", "Summary", List.of("Drama"));

        when(showRepositoryPort.findById(showId)).thenReturn(Optional.of(show));

        // Act
        showUseCase.addComment(showId, commentText, rating);

        // Assert
        verify(commentRepositoryPort, times(1)).saveComment(showId, commentText, rating);
    }
}