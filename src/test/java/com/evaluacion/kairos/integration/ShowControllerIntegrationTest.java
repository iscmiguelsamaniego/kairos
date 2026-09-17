package com.evaluacion.kairos.integration;

import com.evaluacion.kairos.infrastructure.inbound.rest.CommentRequest;
import com.evaluacion.kairos.infrastructure.outbound.persistence.ShowMongoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "tvmaze.api.url=http://localhost:8089"
)
@AutoConfigureMockMvc
class ShowControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShowMongoRepository showMongoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    static WireMockServer wireMockServer = new WireMockServer(8089);

    @BeforeEach
    void setUp() {
        showMongoRepository.deleteAll();
    }

    @BeforeAll
    static void startWireMock() {
        wireMockServer.start();
        configureFor("localhost", 8089);
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Integration: GET /shows/search - Returns mapped shows from external API")
    void shouldSearchShowsSuccessfully() throws Exception {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get
                        (com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/search/shows?q=batman"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                 {
                                    "show": {
                                        "id": 975,
                                        "name": "Batman",
                                        "summary": "<p>Dark Knight</p>",
                                        "genres": ["Action"],
                                        "network": { "name": "ABC" }
                                        }
                                    }
                                ]
                                """)));

        mockMvc.perform(get("/shows/search")
                        .param("q", "batman")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(975))
                .andExpect(jsonPath("$[0].name").value("Batman"))
                .andExpect(jsonPath("$[0].channel").value("ABC"));
    }

    @Test
    @DisplayName("Integration: GET /shows/{id} - Fetches from API on cache miss and saves to MongoDB")
    void shouldFetchByIdAndCacheInMongo() throws Exception {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get
                        (com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/shows/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "id": 1,
                                  "name": "Under the Dome",
                                  "summary": "<p>Small town gets trapped.</p>",
                                  "genres": ["Drama"],
                                  "network": { "name": "CBS" }
                                }
                                """)));

        // Primera llamada: No está en Mongo, va a la API externa y guarda en caché
        mockMvc.perform(get("/shows/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Under the Dome"));

        // Validar que se guardó físicamente en MongoDB
        assertThat(showMongoRepository.findById(1L)).isPresent();
    }

    @Test
    @DisplayName("Integration: POST /shows/{id}/comments - Persists comment successfully")
    void shouldAddCommentSuccessfully() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setComment("Excelente serie, muy recomendada.");
        request.setRating(5);

        mockMvc.perform(post("/shows/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comentario agregado exitosamente"));
    }

    @Test
    @DisplayName("Integration: POST /shows/{id}/comments - Fails validation when rating is out of bounds")
    void shouldFailValidationWhenRatingIsInvalid() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setComment("Buen show");
        request.setRating(6); // Inválido (debe ser máximo 5)

        mockMvc.perform(post("/shows/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}