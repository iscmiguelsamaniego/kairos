package com.evaluacion.kairos.domain;

import java.util.Collections;
import java.util.List;

public record Show(
        Long id,
        String name,
        String channel,
        String summary,
        List<String> genres,
        List<Comment> comments
) {
    public Show(Long id, String name, String channel, String summary, List<String> genres) {
        this(id, name, channel, summary, genres, Collections.emptyList());
    }
}