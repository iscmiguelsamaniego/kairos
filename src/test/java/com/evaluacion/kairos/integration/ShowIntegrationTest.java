package com.evaluacion.kairos.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "tvmaze.api.url=http://localhost:8089")
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8089)
class ShowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Integration: GET /shows/search - Validates complete pipeline mapping")
    void shouldReturnMappedShowsFromExternalApi() throws Exception {

        stubFor(get(urlEqualTo("/search/shows?q=batman"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {
                                    "show": {
                                      "id": 1,
                                      "name": "Batman",
                                      "summary": "<p>Dark knight</p>",
                                      "genres": ["Action"],
                                      "network": { "name": "Fox" }
                                    }
                                  }
                                ]
                                """)));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/shows/search")
                        .param("q", "batman")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Batman"))
                .andExpect(jsonPath("$[0].channel").value("Fox"));
    }

    @Test
    @DisplayName("Integration: GET /shows/{id} - Catches error via GlobalExceptionHandler when not found")
    void shouldHandleShowNotFoundException() throws Exception {
        stubFor(get(urlEqualTo("/shows/9999"))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/shows/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Show not found"));
    }
}