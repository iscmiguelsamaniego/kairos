package com.evaluacion.kairos.infrastructure.inbound;

import com.evaluacion.kairos.infrastructure.inbound.exception.GlobalExceptionHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and return 400 Bad Request")
    void shouldHandleValidationException() throws Exception {
        // Payload vacío para forzar fallo en @NotBlank
        String invalidPayload = "{\"name\":\"\"}";

        mockMvc.perform(post("/test/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should handle HttpClientErrorException and return external status code")
    void shouldHandleHttpClientErrorException() throws Exception {
        mockMvc.perform(get("/test/http-client-error"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("External API Client Error"))
                .andExpect(jsonPath("$.message").value("Error communicating with TV Maze API: Not Found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500 Internal Server Error")
    void shouldHandleGlobalException() throws Exception {
        mockMvc.perform(get("/test/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Database connection failed"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // --- Controlador auxiliar para disparar las excepciones en las pruebas ---
    @RestController
    @RequestMapping("/test")
    static class TestController {

        @PostMapping("/valid")
        public ResponseEntity<String> testValidation(@Valid @RequestBody TestDto dto) {
            return ResponseEntity.ok("OK");
        }

        @GetMapping("/http-client-error")
        public void testHttpClientError() {
            throw HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        }

        @GetMapping("/generic-error")
        public void testGenericError() {
            throw new RuntimeException("Database connection failed");
        }
    }

    // --- DTO auxiliar para validaciones ---
    static class TestDto {
        @NotBlank(message = "El nombre no puede estar vacío")
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}