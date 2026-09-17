package com.evaluacion.kairos.infrastructure.outbound.http.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TvMazeShowDtoTest {

    @Test
    @DisplayName("Unit: getChannelName - Returns network name when network is present")
    void shouldReturnNetworkNameWhenPresent() {
        // Arrange
        TvMazeShowDto.NetworkDto network = new TvMazeShowDto.NetworkDto("HBO");
        TvMazeShowDto.NetworkDto webChannel = new TvMazeShowDto.NetworkDto("Netflix");

        TvMazeShowDto dto = new TvMazeShowDto(1L, "Show", "Summary", List.of("Drama"), network, webChannel);

        // Act & Assert
        assertThat(dto.getChannelName()).isEqualTo("HBO");
    }

    @Test
    @DisplayName("Unit: getChannelName - Falls back to webChannel name when network is null")
    void shouldFallbackToWebChannelWhenNetworkIsNull() {
        // Arrange
        TvMazeShowDto.NetworkDto webChannel = new TvMazeShowDto.NetworkDto("Netflix");

        TvMazeShowDto dto = new TvMazeShowDto(1L, "Show", "Summary", List.of("Drama"), null, webChannel);

        // Act & Assert
        assertThat(dto.getChannelName()).isEqualTo("Netflix");
    }

    @Test
    @DisplayName("Unit: getChannelName - Returns null when both network and webChannel are null")
    void shouldReturnNullWhenBothChannelsAreNull() {
        // Arrange
        TvMazeShowDto dto = new TvMazeShowDto(1L, "Show", "Summary", List.of("Drama"), null, null);

        // Act & Assert
        assertThat(dto.getChannelName()).isNull();
    }
}