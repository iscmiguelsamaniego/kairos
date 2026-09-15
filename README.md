# Evaluación Kairos Backend - TV Maze Middleware

API middleware en Java y Spring Boot que se conecta a TV Maze, integrando persistencia, caché con TTL, validaciones y manejo global de errores con MongoDB Atlas.

## Tecnologias utilizadas

* **Java 17**

* **Spring Boot** (Web, Data MongoDB, Validation)


* **MongoDB Atlas** (Caché con índices TTL y base de datos NoSQL)


* **JUnit & Mockito** (Pruebas unitarias y de integración)

## Lógica de Negocio

* **Búsqueda (`/search`):** Consume TV Maze y retorna los shows requeridos (`id`, `name`, `channel`, `summary`, `genres`) enriquecidos con sus comentarios de MongoDB.


* **Show por ID y Caché (`/shows/{id}`):** Valida la caché en MongoDB antes de llamar a la API externa; si no existe, lo consume, lo guarda y lo devuelve con sus comentarios.


* **Comentarios (`/comments`):** Registra calificaciones y comentarios aplicando validaciones estrictas de rango (0 a 5) mediante Bean Validation.


* **Control Global:** Implementación de `@RestControllerAdvice` para estandarizar respuestas y manejar errores.