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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
    @DisplayName("Should return list of shows with enriched comments when search query is valid")
    void shouldReturnShowsWhenQueryIsValid() {
        // Arrange
        Long showId = 1L;
        Show mockShow = new Show(showId, "Batman", "Fox", "Dark knight show", List.of("Action"));
        List<Comment> mockComments = List.of(new Comment("Excelente", 5));

        when(showRepositoryPort.findById(showId)).thenReturn(Optional.of(mockShow));
        when(commentRepositoryPort.findCommentsByShowId(showId)).thenReturn(mockComments);

        // Act
        Show result = showUseCase.getShowById(showId);

        // Assert
        assertEquals(showId, result.id());
        assertEquals(1, result.comments().size());
        assertEquals("Excelente", result.comments().get(0).comment());
    }
}