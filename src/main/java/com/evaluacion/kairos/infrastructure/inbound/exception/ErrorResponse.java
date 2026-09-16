package com.evaluacion.kairos.infrastructure.inbound.exception;
import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message
) {}