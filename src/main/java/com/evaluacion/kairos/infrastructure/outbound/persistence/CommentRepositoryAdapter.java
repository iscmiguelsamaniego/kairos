package com.evaluacion.kairos.infrastructure.outbound.persistence;
import com.evaluacion.kairos.ports.out.CommentRepositoryPort;
import org.springframework.stereotype.Component;

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
}