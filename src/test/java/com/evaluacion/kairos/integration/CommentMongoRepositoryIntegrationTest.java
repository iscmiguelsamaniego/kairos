package com.evaluacion.kairos.integration;

import com.evaluacion.kairos.infrastructure.outbound.persistence.CommentEntity;
import com.evaluacion.kairos.infrastructure.outbound.persistence.CommentMongoRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CommentMongoRepositoryIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CommentMongoRepositoryIntegrationTest.class);

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private CommentMongoRepository commentMongoRepository;

    @BeforeEach
    void setUp() {
        log.info(">>> [TEST LOG] INICIANDO setUp() en CommentMongoRepositoryIntegrationTest...");
        commentMongoRepository.deleteAll();
        log.info(">>> [TEST LOG] deleteAll() ejecutado con éxito.");
    }

    @Test
    @DisplayName("Integration: CommentMongoRepository - Successfully saves and retrieves CommentEntity")
    void shouldSaveAndRetrieveCommentEntityDirectly() {
        CommentEntity entity = new CommentEntity();
        entity.setShowId(1L);
        entity.setComment("Excelente trama y desarrollo de personajes.");
        entity.setRating(5);

        commentMongoRepository.save(entity);
        List<CommentEntity> entities = commentMongoRepository.findAll();

        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getShowId()).isEqualTo(1L);
        assertThat(entities.get(0).getComment()).isEqualTo("Excelente trama y desarrollo de personajes.");
        assertThat(entities.get(0).getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("Integration: CommentMongoRepository - Deletes comments successfully")
    void shouldDeleteCommentEntity() {
        CommentEntity entity = new CommentEntity();
        entity.setShowId(2L);
        entity.setComment("Regular");
        entity.setRating(2);
        CommentEntity saved = commentMongoRepository.save(entity);

        commentMongoRepository.deleteById(saved.getId());
        List<CommentEntity> entities = commentMongoRepository.findAll();

        assertThat(entities).isEmpty();
    }

    @AfterAll
    static void afterAll() {
        mongo.stop();
    }
}