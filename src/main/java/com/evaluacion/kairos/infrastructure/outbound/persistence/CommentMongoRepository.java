package com.evaluacion.kairos.infrastructure.outbound.persistence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMongoRepository extends MongoRepository<CommentEntity, String> {
    List<CommentEntity> findByShowId(Long showId);
}