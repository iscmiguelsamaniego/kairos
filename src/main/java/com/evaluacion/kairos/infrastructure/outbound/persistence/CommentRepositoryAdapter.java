package com.evaluacion.kairos.infrastructure.outbound.persistence;

import com.evaluacion.kairos.ports.out.CommentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentRepositoryAdapter implements CommentRepositoryPort {

    private final CommentMongoRepository commentMongoRepository;

    public CommentRepositoryAdapter(CommentMongoRepository commentMongoRepository) {
        this.commentMongoRepository = commentMongoRepository;
    }

    @Override
    public void saveComment(Long showId, String comment, Integer rating) {

        List<CommentEntity> existingComments = commentMongoRepository.findByShowId(showId);

        for (CommentEntity existing : existingComments) {
            if (isTextSimilar(existing.getComment(), comment)) {
                throw new IllegalArgumentException("Ya existe un comentario idéntico o muy similar registrado para este show.");
            }
        }

        CommentEntity entity = new CommentEntity(showId, comment, rating);
        commentMongoRepository.save(entity);
    }

    private boolean isTextSimilar(String existingComment, String newComment) {
        if (existingComment == null || newComment == null) {
            return false;
        }

        String normalizedExisting = existingComment.trim().toLowerCase();
        String normalizedNew = newComment.trim().toLowerCase();

        return normalizedExisting.equals(normalizedNew);
    }

}