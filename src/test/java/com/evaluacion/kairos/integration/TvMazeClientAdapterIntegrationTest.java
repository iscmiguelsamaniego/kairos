package com.evaluacion.kairos.integration;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.infrastructure.outbound.http.TvMazeClientAdapter;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "tvmaze.api.url=http://localhost:8089"
)
@WireMockTest(httpPort = 8089)
class TvMazeClientAdapterIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TvMazeClientAdapter tvMazeClientAdapter;

    @Test
    @DisplayName("Integration: TvMazeClientAdapter - Successfully searches and maps shows from external API")
    void shouldSearchShowsFromExternalApi() {
        // Arrange
        stubFor(get(urlEqualTo("/search/shows?q=friends"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {
                                    "show": {
                                      "id": 431,
                                      "name": "Friends",
                                      "summary": "<p>Central Perk group.</p>",
                                      "genres": ["Comedy", "Romance"],
                                      "network": { "name": "NBC" }
                                    }
                                  }
                                ]
                                """)));

        // Act
        List<Show> shows = tvMazeClientAdapter.searchShows("friends");

        // Assert
        assertThat(shows).isNotEmpty();
        assertThat(shows.get(0).id()).isEqualTo(431L);
        assertThat(shows.get(0).name()).isEqualTo("Friends");
        assertThat(shows.get(0).channel()).isEqualTo("NBC");
        assertThat(shows.get(0).genres()).contains("Comedy", "Romance");
    }

    @Test
    @DisplayName("Integration: TvMazeClientAdapter - Successfully fetches show by ID from external API")
    void shouldGetShowByIdFromExternalApi() {
        // Arrange
        stubFor(get(urlEqualTo("/shows/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "id": 1,
                                  "name": "Under the Dome",
                                  "summary": "<p>Small town gets trapped.</p>",
                                  "genres": ["Drama", "Sci-Fi"],
                                  "network": { "name": "CBS" }
                                }
                                """)));

        // Act
        Optional<Show> showOpt = tvMazeClientAdapter.getShowById(1L);

        // Assert
        assertThat(showOpt).isPresent();
        assertThat(showOpt.get().id()).isEqualTo(1L);
        assertThat(showOpt.get().name()).isEqualTo("Under the Dome");
        assertThat(showOpt.get().channel()).isEqualTo("CBS");
        assertThat(showOpt.get().genres()).contains("Drama");
    }

    @Test
    @DisplayName("Integration: TvMazeClientAdapter - Returns empty when show is not found (404)")
    void shouldReturnEmptyWhenShowNotFound() {
        // Arrange
        stubFor(get(urlEqualTo("/shows/99999999"))
                .willReturn(aResponse().withStatus(404)));

        // Act
        Optional<Show> showOpt = tvMazeClientAdapter.getShowById(99999999L);

        // Assert
        assertThat(showOpt).isEmpty();
    }

    @Test
    @DisplayName("Integration: TvMazeClientAdapter - Triggers Circuit Breaker fallback returning empty list on server error (500)")
    void shouldTriggerFallbackWhenExternalApiFails() {
        // Arrange: Simulamos una caída del servicio externo (500 Internal Server Error)
        stubFor(get(urlEqualTo("/search/shows?q=error-test"))
                .willReturn(aResponse().withStatus(500)));

        // Act: El cliente debe invocar el fallback definido en Resilience4j sin lanzar excepción al usuario
        List<Show> shows = tvMazeClientAdapter.searchShows("error-test");

        // Assert: Valida que el fallback responde de forma segura con una lista vacía
        assertThat(shows).isNotNull().isEmpty();
    }
}