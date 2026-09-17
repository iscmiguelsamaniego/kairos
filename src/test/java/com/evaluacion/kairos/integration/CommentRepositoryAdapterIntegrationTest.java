package com.evaluacion.kairos.integration;

import com.evaluacion.kairos.domain.Comment;
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
    @DisplayName("Integration: CommentRepositoryAdapter - Successfully finds and maps comments to domain by show ID")
    void shouldFindAndMapCommentsByShowIdSuccessfully() {
        // Arrange: Insertamos directamente entidades de prueba en la base de datos de MongoDB
        Long showId = 2L;
        commentMongoRepository.save(new CommentEntity(showId, "Excelente temporada", 5));
        commentMongoRepository.save(new CommentEntity(showId, "Me esperaba más", 3));

        // Comentario de otro show para asegurar aislamiento de datos
        commentMongoRepository.save(new CommentEntity(99L, "Show diferente", 4));

        // Act
        List<Comment> domainComments = commentRepositoryAdapter.findCommentsByShowId(showId);

        // Assert
        assertThat(domainComments)
                .isNotNull()
                .hasSize(2);

        assertThat(domainComments.get(0).comment()).isEqualTo("Excelente temporada");
        assertThat(domainComments.get(0).rating()).isEqualTo(5);

        assertThat(domainComments.get(1).comment()).isEqualTo("Me esperaba más");
        assertThat(domainComments.get(1).rating()).isEqualTo(3);
    }
}