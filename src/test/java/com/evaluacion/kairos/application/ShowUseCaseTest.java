package com.evaluacion.kairos.application;

import com.evaluacion.kairos.domain.Comment;
import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.out.CommentRepositoryPort;
import com.evaluacion.kairos.ports.out.ShowRepositoryPort;
import com.evaluacion.kairos.ports.out.TvMazeClientPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowUseCaseTest {

    @Mock
    private TvMazeClientPort tvMazeClientPort;

    @Mock
    private ShowRepositoryPort showRepositoryPort;

    @Mock
    private CommentRepositoryPort commentRepositoryPort;

    @InjectMocks
    private ShowUseCase showUseCase;

    @Test
    @DisplayName("Should return list of shows with enriched comments when searching by query")
    void shouldReturnEnrichedShowsWhenSearchQueryIsValid() {
        // Arrange
        Long showId = 1L;
        String query = "Batman";
        Show mockShow = new Show(showId, "Batman", "Fox", "Dark knight show", List.of("Action"));
        List<Comment> mockComments = List.of(new Comment("Muy buena serie", 5));

        when(tvMazeClientPort.searchShows(query)).thenReturn(List.of(mockShow));
        when(commentRepositoryPort.findCommentsByShowId(showId)).thenReturn(mockComments);

        // Act
        List<Show> results = showUseCase.searchShows(query);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(showId, results.get(0).id());
        assertEquals(1, results.get(0).comments().size());
        assertEquals("Muy buena serie", results.get(0).comments().get(0).comment());

        verify(tvMazeClientPort).searchShows(query);
        verify(commentRepositoryPort).findCommentsByShowId(showId);
    }

    @Test
    @DisplayName("Should return show from repository (Cache Hit) with enriched comments")
    void shouldReturnShowFromCacheWhenExists() {
        // Arrange
        Long showId = 1L;
        Show mockShow = new Show(showId, "Batman", "Fox", "Dark knight show", List.of("Action"));
        List<Comment> mockComments = List.of(new Comment("Excelente", 5));

        when(showRepositoryPort.findById(showId)).thenReturn(Optional.of(mockShow));
        when(commentRepositoryPort.findCommentsByShowId(showId)).thenReturn(mockComments);

        // Act
        Show result = showUseCase.getShowById(showId);

        // Assert
        assertNotNull(result);
        assertEquals(showId, result.id());
        assertEquals(1, result.comments().size());
        assertEquals("Excelente", result.comments().get(0).comment());

        verify(showRepositoryPort).findById(showId);
        verify(tvMazeClientPort, never()).getShowById(any());
        verify(showRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should fetch from external API, save to repository (Cache Miss), and return enriched show")
    void shouldFetchFromExternalApiWhenNotInCache() {
        // Arrange
        Long showId = 2L;
        Show externalShow = new Show(showId, "Flash", "CW", "Fast hero", List.of("Sci-Fi"));
        List<Comment> mockComments = List.of();

        when(showRepositoryPort.findById(showId)).thenReturn(Optional.empty());
        when(tvMazeClientPort.getShowById(showId)).thenReturn(Optional.of(externalShow));
        when(showRepositoryPort.save(externalShow)).thenReturn(externalShow);
        when(commentRepositoryPort.findCommentsByShowId(showId)).thenReturn(mockComments);

        // Act
        Show result = showUseCase.getShowById(showId);

        // Assert
        assertNotNull(result);
        assertEquals(showId, result.id());
        assertTrue(result.comments().isEmpty());

        verify(showRepositoryPort).findById(showId);
        verify(tvMazeClientPort).getShowById(showId);
        verify(showRepositoryPort).save(externalShow);
        verify(commentRepositoryPort).findCommentsByShowId(showId);
    }

    @Test
    @DisplayName("Should throw RuntimeException when show is not found in cache nor external API")
    void shouldThrowExceptionWhenShowNotFoundAnywhere() {
        // Arrange
        Long showId = 99L;
        when(showRepositoryPort.findById(showId)).thenReturn(Optional.empty());
        when(tvMazeClientPort.getShowById(showId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            showUseCase.getShowById(showId);
        });

        assertEquals("Show not found", exception.getMessage());
        verify(showRepositoryPort).findById(showId);
        verify(tvMazeClientPort).getShowById(showId);
        verify(showRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should validate show exists and save comment successfully")
    void shouldAddCommentSuccessfully() {
        // Arrange
        Long showId = 1L;
        String commentText = "Me encanta";
        Integer rating = 4;
        Show mockShow = new Show(showId, "Batman", "Fox", "Dark knight show", List.of("Action"));

        when(showRepositoryPort.findById(showId)).thenReturn(Optional.of(mockShow));
        when(commentRepositoryPort.findCommentsByShowId(showId)).thenReturn(List.of());

        // Act
        showUseCase.addComment(showId, commentText, rating);

        // Assert
        verify(showRepositoryPort).findById(showId);
        verify(commentRepositoryPort).saveComment(showId, commentText, rating);
    }
}