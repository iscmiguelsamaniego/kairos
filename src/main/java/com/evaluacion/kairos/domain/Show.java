package com.evaluacion.kairos.domain;

import java.util.List;

public record Show(
        Long id,
        String name,
        String channel,
        String summary,
        List<String> genres) {}