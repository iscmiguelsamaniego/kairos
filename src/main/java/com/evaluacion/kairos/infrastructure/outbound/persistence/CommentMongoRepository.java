package com.evaluacion.kairos.infrastructure.outbound.persistence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMongoRepository extends MongoRepository<CommentEntity, String> {
}