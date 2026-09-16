package com.evaluacion.kairos.ports.out;

public interface CommentRepositoryPort {
    void saveComment(Long showId, String comment, Integer rating);
}
