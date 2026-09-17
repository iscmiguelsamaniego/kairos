package com.evaluacion.kairos.integration;

import com.evaluacion.kairos.infrastructure.outbound.persistence.CommentEntity;
import com.evaluacion.kairos.infrastructure.outbound.persistence.CommentMongoRepository;
import com.evaluacion.kairos.infrastructure.outbound.persistence.CommentRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@Import(CommentRepositoryAdapter.class)
class CommentRepositoryAdapterIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CommentRepositoryAdapter commentRepositoryAdapter;

    @Autowired
    private CommentMongoRepository commentMongoRepository;

    @BeforeEach
    void setUp() {
        commentMongoRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: CommentRepositoryAdapter - Successfully saves comment and rating linked to show ID")
    void shouldSaveCommentSuccessfully() {
        // Arrange
        Long showId = 1L;
        String commentText = "Una obra maestra de la televisión.";
        Integer rating = 5;

        // Act
        commentRepositoryAdapter.saveComment(showId, commentText, rating);

        // Assert
        List<CommentEntity> savedComments = commentMongoRepository.findAll();
        assertThat(savedComments).hasSize(1);

        CommentEntity saved = savedComments.get(0);
        assertThat(saved.getShowId()).isEqualTo(showId);
        assertThat(saved.getComment()).isEqualTo(commentText);
        assertThat(saved.getRating()).isEqualTo(rating);
    }

    @Test
    @DisplayName("Integration: CommentRepositoryAdapter - Throws exception when saving a duplicate or similar comment")
    void shouldThrowExceptionWhenCommentIsSimilar() {
        // Arrange
        Long showId = 1L;
        String commentText = "Muy buena trama.";
        Integer rating = 4;

        commentRepositoryAdapter.saveComment(showId, commentText, rating);

        assertThatThrownBy(() ->
                commentRepositoryAdapter.saveComment(showId, "   MUY buena trama.   ", 5)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un comentario idéntico o muy similar registrado para este show.");

        List<CommentEntity> allComments = commentMongoRepository.findAll();
        assertThat(allComments).hasSize(1);
    }
}