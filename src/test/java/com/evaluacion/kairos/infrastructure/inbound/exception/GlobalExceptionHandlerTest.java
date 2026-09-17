package com.evaluacion.kairos.infrastructure.inbound.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Unit: handleValidationExceptions - Returns 400 with formatted field error message")
    void shouldHandleMethodArgumentNotValidException() throws NoSuchMethodException {
        // Arrange
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "rating", "must be less than or equal to 5"));

        Method method = this.getClass().getDeclaredMethod("dummyMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).isEqualTo("rating: must be less than or equal to 5");
    }

    @Test
    @DisplayName("Unit: handleHttpClientErrorException - Returns external client error status and message")
    void shouldHandleHttpClientErrorException() {
        // Arrange
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                org.springframework.http.HttpHeaders.EMPTY,
                new byte[0],
                null
        );

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpClientErrorException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("External API Client Error");
        assertThat(response.getBody().message()).contains("Error communicating with TV Maze API");
    }

    @Test
    @DisplayName("Unit: handleGlobalException - Returns 500 Internal Server Error for unexpected exceptions")
    void shouldHandleGlobalException() {
        // Arrange
        Exception ex = new RuntimeException("Database connection failure");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().message()).contains("Database connection failure");
    }

    @Test
    @DisplayName("Unit: handleIllegalArgumentException - Returns 400 Bad Request for illegal argument exceptions")
    void shouldHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("El show con ID 1 ya tiene un comentario registrado.");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).contains("El show con ID 1 ya tiene un comentario registrado.");
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String param)
    { /* Método auxiliar requerido para instanciar MethodParameter en la prueba de validación */ }
}