package com.evaluacion.kairos.infrastructure.outbound.persistence;
import com.evaluacion.kairos.domain.Comment;
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
        CommentEntity entity = new CommentEntity(showId, comment, rating);
        commentMongoRepository.save(entity);
    }

    @Override
    public List<Comment> findCommentsByShowId(Long showId) {
        return commentMongoRepository.findByShowId(showId).stream()
                .map(entity -> new Comment(entity.getComment(), entity.getRating()))
                .toList();
    }
}