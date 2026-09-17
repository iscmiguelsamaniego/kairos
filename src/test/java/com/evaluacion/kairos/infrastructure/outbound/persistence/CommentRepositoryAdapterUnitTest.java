package com.evaluacion.kairos.infrastructure.outbound.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CommentRepositoryAdapterUnitTest {

    private CommentMongoRepository commentMongoRepository;
    private CommentRepositoryAdapter commentRepositoryAdapter;

    @BeforeEach
    void setUp() {
        commentMongoRepository = mock(CommentMongoRepository.class);
        commentRepositoryAdapter = new CommentRepositoryAdapter(commentMongoRepository);
    }

    @Test
    @DisplayName("Unit: CommentRepositoryAdapter - Maps parameters to entity and saves via repository when no duplicate exists")
    void shouldMapAndSaveCommentEntity() {
        // Arrange
        Long showId = 1L;
        String commentText = "Excelente serie.";
        Integer rating = 5;

        when(commentMongoRepository.findByShowId(showId)).thenReturn(Collections.emptyList());

        // Act
        commentRepositoryAdapter.saveComment(showId, commentText, rating);

        // Assert
        verify(commentMongoRepository, times(1)).findByShowId(showId);

        ArgumentCaptor<CommentEntity> entityCaptor = ArgumentCaptor.forClass(CommentEntity.class);
        verify(commentMongoRepository, times(1)).save(entityCaptor.capture());

        CommentEntity capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getShowId()).isEqualTo(showId);
        assertThat(capturedEntity.getComment()).isEqualTo(commentText);
        assertThat(capturedEntity.getRating()).isEqualTo(rating);
    }

    @Test
    @DisplayName("Unit: CommentRepositoryAdapter - Throws exception when a similar comment already exists")
    void shouldThrowExceptionWhenSimilarCommentExists() {
        // Arrange
        Long showId = 1L;
        String existingCommentText = "Gran trama y personajes.";

        CommentEntity existingEntity = new CommentEntity();
        existingEntity.setShowId(showId);
        existingEntity.setComment(existingCommentText);
        existingEntity.setRating(5);

        when(commentMongoRepository.findByShowId(showId)).thenReturn(List.of(existingEntity));

        assertThatThrownBy(() ->
                commentRepositoryAdapter.saveComment(showId, "   gran trama y personajes.   ", 4)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un comentario idéntico o muy similar registrado para este show.");

        verify(commentMongoRepository, times(1)).findByShowId(showId);
        verify(commentMongoRepository, never()).save(any(CommentEntity.class));
    }
}