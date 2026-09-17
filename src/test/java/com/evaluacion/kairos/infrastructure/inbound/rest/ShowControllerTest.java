package com.evaluacion.kairos.infrastructure.inbound.rest;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.in.ShowServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShowController.class)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShowServicePort showServicePort;

    @Test
    @DisplayName("GET /shows/search should return 200 OK and json array of shows")
    void shouldSearchShowsSuccessfully() throws Exception {
        // Arrange
        String query = "friends";
        List<Show> mockShows = List.of(
                new Show(
                        2L,
                        "Friends",
                        "NBC",
                        "Comedy about six friends",
                        List.of("Comedy")
                )
        );

        when(showServicePort.searchShows(query)).thenReturn(mockShows);

        // Act & Assert
        mockMvc.perform(get("/shows/search")
                        .param("q", query)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Friends"))
                .andExpect(jsonPath("$[0].channel").value("NBC"))
                .andExpect(jsonPath("$[0].summary").value("Comedy about six friends"))
                .andExpect(jsonPath("$[0].genres[0]").value("Comedy"));
    }

    @Test
    @DisplayName("GET /shows/{id} should return 200 OK and show details when found")
    void shouldGetShowByIdSuccessfully() throws Exception {
        // Arrange
        Long showId = 1L;
        Show mockShow = new Show(
                showId,
                "Breaking Bad",
                "AMC",
                "Chemistry teacher turns into drug kingpin",
                List.of("Drama", "Crime")
        );

        when(showServicePort.getShowById(showId)).thenReturn(mockShow);

        // Act & Assert
        mockMvc.perform(get("/shows/{id}", showId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Breaking Bad"))
                .andExpect(jsonPath("$.channel").value("AMC"));
    }

    @Test
    @DisplayName("POST /shows/{id}/comments should return 200 OK when comment request is valid")
    void shouldAddCommentSuccessfully() throws Exception {
        // Arrange
        Long showId = 1L;
        CommentRequest request = new CommentRequest();
        request.setComment("Excelente serie");
        request.setRating(5);

        doNothing().when(showServicePort).addComment(eq(showId), eq("Excelente serie"), eq(5));

        // Act & Assert
        mockMvc.perform(post("/shows/{id}/comments", showId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comentario agregado exitosamente"));
    }

    @Test
    @DisplayName("POST /shows/{id}/comments should return 400 Bad Request when validation fails")
    void shouldFailWhenCommentRequestIsInvalid() throws Exception {
        // Arrange
        Long showId = 1L;
        CommentRequest request = new CommentRequest();
        request.setComment(""); // Vacío (inválido según anotaciones de Bean Validation)
        request.setRating(6);   // Fuera de rango (máximo 5)

        // Act & Assert
        mockMvc.perform(post("/shows/{id}/comments", showId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(showServicePort, never()).addComment(anyLong(), anyString(), anyInt());
    }
}