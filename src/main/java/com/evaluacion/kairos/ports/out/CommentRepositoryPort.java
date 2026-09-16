package com.evaluacion.kairos.ports.out;

import com.evaluacion.kairos.domain.Comment;

import java.util.List;

public interface CommentRepositoryPort {
    void saveComment(Long showId, String comment, Integer rating);
    List<Comment> findCommentsByShowId(Long showId);
}
