package com.evaluacion.kairos.integration;

import com.evaluacion.kairos.infrastructure.outbound.persistence.CommentEntity;
import com.evaluacion.kairos.infrastructure.outbound.persistence.CommentMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentMongoRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CommentMongoRepository commentMongoRepository;

    @BeforeEach
    void setUp() {
        commentMongoRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: CommentMongoRepository - Successfully saves and retrieves CommentEntity")
    void shouldSaveAndRetrieveCommentEntityDirectly() {
        // Arrange
        CommentEntity entity = new CommentEntity();
        entity.setShowId(1L);
        entity.setComment("Excelente trama y desarrollo de personajes.");
        entity.setRating(5);

        // Act
        commentMongoRepository.save(entity);
        List<CommentEntity> entities = commentMongoRepository.findAll();

        // Assert
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getShowId()).isEqualTo(1L);
        assertThat(entities.get(0).getComment()).isEqualTo("Excelente trama y desarrollo de personajes.");
        assertThat(entities.get(0).getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("Integration: CommentMongoRepository - Successfully finds comments by show ID using derived query")
    void shouldFindByShowIdSuccessfully() {
        // Arrange
        CommentEntity entity1 = new CommentEntity(1L, "Muy buena", 4);
        CommentEntity entity2 = new CommentEntity(1L, "Increíble", 5);
        CommentEntity entity3 = new CommentEntity(2L, "De otro show", 3);

        commentMongoRepository.saveAll(List.of(entity1, entity2, entity3));

        // Act
        List<CommentEntity> result = commentMongoRepository.findByShowId(1L);

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2);

        assertThat(result)
                .extracting(CommentEntity::getComment)
                .containsExactlyInAnyOrder("Muy buena", "Increíble");
    }

    @Test
    @DisplayName("Integration: CommentMongoRepository - Deletes comments successfully")
    void shouldDeleteCommentEntity() {
        // Arrange
        CommentEntity entity = new CommentEntity();
        entity.setShowId(2L);
        entity.setComment("Regular");
        entity.setRating(2);
        CommentEntity saved = commentMongoRepository.save(entity);

        // Act
        commentMongoRepository.deleteById(saved.getId());
        List<CommentEntity> entities = commentMongoRepository.findAll();

        // Assert
        assertThat(entities).isEmpty();
    }
}