package com.evaluacion.kairos.infrastructure.inbound.rest;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.in.ShowServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShowController.class)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShowServicePort showServicePort;

    @Test
    @DisplayName("GET /shows/search - Should return 200 OK and json array without comments for phase 1")
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
                .andExpect(jsonPath("$[0].genres[0]").value("Comedy"))
                .andExpect(jsonPath("$[0].comments").doesNotExist());
    }

    @Test
    @DisplayName("GET /shows/{id} - Should return 200 OK and show object")
    void shouldGetShowByIdSuccessfully() throws Exception {
        // Arrange
        Long showId = 2L;
        Show mockShow = new Show(
                showId,
                "Friends",
                "NBC",
                "Comedy about six friends",
                List.of("Comedy")
        );

        when(showServicePort.getShowById(showId)).thenReturn(mockShow);

        // Act & Assert
        mockMvc.perform(get("/shows/{id}", showId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Friends"))
                .andExpect(jsonPath("$.channel").value("NBC"))
                .andExpect(jsonPath("$.comments").doesNotExist());
    }
}