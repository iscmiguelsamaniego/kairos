package com.evaluacion.kairos.infrastructure.outbound.http;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.infrastructure.outbound.http.dto.TvMazeSearchItemDto;
import com.evaluacion.kairos.infrastructure.outbound.http.dto.TvMazeShowDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TvMazeClientAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TvMazeClientAdapter tvMazeClientAdapter;

    @Nested
    @DisplayName("searchShows Unit Tests")
    class SearchShowsTests {

        @Test
        @DisplayName("Should map search results successfully to domain list when network is present")
        void shouldMapSearchResultsWithNetworkToDomain() {
            // Arrange
            String query = "batman";
            TvMazeShowDto.NetworkDto network = new TvMazeShowDto.NetworkDto("Fox");
            TvMazeShowDto showDto = new TvMazeShowDto(1L, "Batman", "<p>Dark knight</p>", List.of("Action"), network, null);
            TvMazeSearchItemDto itemDto = new TvMazeSearchItemDto(showDto);

            List<TvMazeSearchItemDto> mockResponse = List.of(itemDto);

            when(restTemplate.exchange(
                    anyString(),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenReturn(ResponseEntity.ok(mockResponse));

            // Act
            List<Show> result = tvMazeClientAdapter.searchShows(query);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).id());
            assertEquals("Batman", result.get(0).name());
            assertEquals("Fox", result.get(0).channel());
            assertEquals("<p>Dark knight</p>", result.get(0).summary());
        }

        @Test
        @DisplayName("Should return empty list when API response body is null")
        void shouldReturnEmptyListWhenResponseBodyIsNull() {
            // Arrange
            when(restTemplate.exchange(
                    anyString(),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenReturn(ResponseEntity.ok(null));

            // Act
            List<Show> result = tvMazeClientAdapter.searchShows("batman");

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getShowById Unit Tests")
    class GetShowByIdTests {

        @Test
        @DisplayName("Should map webChannel when network is null")
        void shouldMapWebChannelWhenNetworkIsNull() {
            // Arrange
            Long showId = 100L;
            TvMazeShowDto.NetworkDto webChannel = new TvMazeShowDto.NetworkDto("Netflix");
            TvMazeShowDto showDto = new TvMazeShowDto(showId, "Stranger Things", "Sci-Fi show", List.of("Sci-Fi"), null, webChannel);

            when(restTemplate.getForObject(anyString(), eq(TvMazeShowDto.class))).thenReturn(showDto);

            // Act
            Optional<Show> result = tvMazeClientAdapter.getShowById(showId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(showId, result.get().id());
            assertEquals("Stranger Things", result.get().name());
            assertEquals("Netflix", result.get().channel());
        }

        @Test
        @DisplayName("Should return Optional.empty when RestTemplate throws HttpClientErrorException.NotFound")
        void shouldReturnEmptyOptionalWhenNotFoundExceptionOccurs() {
            // Arrange
            Long showId = 999L;
            when(restTemplate.getForObject(anyString(), eq(TvMazeShowDto.class)))
                    .thenThrow(HttpClientErrorException.NotFound.class);

            // Act
            Optional<Show> result = tvMazeClientAdapter.getShowById(showId);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Fallback Methods Tests")
    class FallbackTests {

        @Test
        @DisplayName("Should return empty list when searchShows fallback is triggered")
        void tvmazeFallbackShouldReturnEmptyList() {
            List<Show> result = tvMazeClientAdapter.tvmazeFallback("batman", new RuntimeException("Circuit open"));
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty Optional when getShowById fallback is triggered")
        void fallbackGetShowByIdShouldReturnEmptyOptional() {
            Optional<Show> result = tvMazeClientAdapter.fallbackGetShowById(1L, new RuntimeException("Circuit open"));
            assertTrue(result.isEmpty());
        }
    }
}