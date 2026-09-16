package com.evaluacion.kairos.infrastructure.outbound.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TvMazeShowDto(
        Long id,
        String name,
        String summary,
        List<String> genres,
        NetworkDto network,
        @JsonProperty("webChannel") NetworkDto webChannel
) {
    public record NetworkDto(String name) {}

    public String getChannelName() {
        if (network != null && network.name() != null) {
            return network.name();
        }
        if (webChannel != null && webChannel.name() != null) {
            return webChannel.name();
        }
        return null;
    }
}
