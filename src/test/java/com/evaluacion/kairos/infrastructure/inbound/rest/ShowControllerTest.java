package com.evaluacion.kairos.infrastructure.inbound.rest;

import com.evaluacion.kairos.domain.Comment;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @DisplayName("GET /shows/search should return 200 OK and json array with comments")
    void shouldSearchShowsSuccessfully() throws Exception {
        // Arrange
        String query = "friends";

        List<Show> mockShows = List.of(
                new Show(
                        2L,
                        "Friends",
                        "NBC",
                        "Comedy about six friends",
                        List.of("Comedy"),
                        List.of(new Comment("Muy buena comedia", 5))
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
                .andExpect(jsonPath("$[0].comments[0].comment").value("Muy buena comedia"))
                .andExpect(jsonPath("$[0].comments[0].rating").value(5));

        verify(showServicePort).searchShows(query);
    }

    @Test
    @DisplayName("GET /shows/{id} should return 200 OK and show detail with comments")
    void shouldGetShowByIdSuccessfully() throws Exception {
        // Arrange
        Long showId = 2L;
        Show mockShow = new Show(
                showId,
                "Friends",
                "NBC",
                "Comedy about six friends",
                List.of("Comedy"),
                List.of(new Comment("Excelente serie", 5))
        );

        when(showServicePort.getShowById(showId)).thenReturn(mockShow);

        // Act & Assert
        mockMvc.perform(get("/shows/{id}", showId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(showId))
                .andExpect(jsonPath("$.name").value("Friends"))
                .andExpect(jsonPath("$.channel").value("NBC"))
                .andExpect(jsonPath("$.comments[0].comment").value("Excelente serie"))
                .andExpect(jsonPath("$.comments[0].rating").value(5));

        verify(showServicePort).getShowById(showId);
    }

    @Test
    @DisplayName("POST /shows/{id}/comments should return 200 OK when request payload is valid")
    void shouldAddCommentSuccessfully() throws Exception {
        // Arrange
        Long showId = 2L;
        CommentRequest request = new CommentRequest();
        request.setComment("Muy entretenida");
        request.setRating(4);

        doNothing().when(showServicePort).addComment(eq(showId), eq("Muy entretenida"), eq(4));

        // Act & Assert
        mockMvc.perform(post("/shows/{id}/comments", showId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comentario agregado exitosamente"));

        verify(showServicePort).addComment(showId, "Muy entretenida", 4);
    }

    @Test
    @DisplayName("POST /shows/{id}/comments should return 400 Bad Request when validation fails")
    void shouldFailWhenCommentRequestIsInvalid() throws Exception {
        // Arrange
        Long showId = 2L;
        CommentRequest request = new CommentRequest();
        request.setComment(""); // Invalid: @NotBlank fallará
        request.setRating(10);  // Invalid: @Max(5) fallará

        // Act & Assert
        mockMvc.perform(post("/shows/{id}/comments", showId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(showServicePort, never()).addComment(any(), any(), any());
    }
}