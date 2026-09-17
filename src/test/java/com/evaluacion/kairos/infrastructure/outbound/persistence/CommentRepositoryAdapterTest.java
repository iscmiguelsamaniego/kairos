package com.evaluacion.kairos.infrastructure.outbound.persistence;

import com.evaluacion.kairos.domain.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentRepositoryAdapterTest {

    @Mock
    private CommentMongoRepository commentMongoRepository;

    @InjectMocks
    private CommentRepositoryAdapter commentRepositoryAdapter;

    @Test
    @DisplayName("Should map and save comment entity successfully")
    void shouldSaveCommentSuccessfully() {
        // Arrange
        Long showId = 1L;
        String commentText = "Excelente serie de televisión";
        Integer rating = 5;

        // Act
        commentRepositoryAdapter.saveComment(showId, commentText, rating);

        // Assert
        ArgumentCaptor<CommentEntity> entityCaptor = ArgumentCaptor.forClass(CommentEntity.class);
        verify(commentMongoRepository).save(entityCaptor.capture());

        CommentEntity capturedEntity = entityCaptor.getValue();
        assertNotNull(capturedEntity);
        assertEquals(showId, capturedEntity.getShowId());
        assertEquals(commentText, capturedEntity.getComment());
        assertEquals(rating, capturedEntity.getRating());
    }

    @Test
    @DisplayName("Should find comments by show ID and map them to domain objects")
    void shouldFindCommentsByShowIdSuccessfully() {
        // Arrange
        Long showId = 1L;
        CommentEntity entity1 = new CommentEntity(showId, "Muy buena", 4);
        CommentEntity entity2 = new CommentEntity(showId, "Me encantó", 5);

        when(commentMongoRepository.findByShowId(showId)).thenReturn(List.of(entity1, entity2));

        // Act
        List<Comment> domainComments = commentRepositoryAdapter.findCommentsByShowId(showId);

        // Assert
        assertNotNull(domainComments);
        assertEquals(2, domainComments.size());

        assertEquals("Muy buena", domainComments.get(0).comment());
        assertEquals(4, domainComments.get(0).rating());

        assertEquals("Me encantó", domainComments.get(1).comment());
        assertEquals(5, domainComments.get(1).rating());

        verify(commentMongoRepository).findByShowId(showId);
    }
}