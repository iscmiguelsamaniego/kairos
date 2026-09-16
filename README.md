# Evaluación Kairos Backend - TV Maze Middleware

API middleware en Java y Spring Boot que se conecta a TV Maze, integrando persistencia, caché con TTL, validaciones y manejo global de errores con MongoDB Atlas.

## Tecnologias utilizadas

* **Lenguaje:** Java 17 (Uso de *records* e inmutabilidad).
* **Framework:** Spring Boot 3.3.2 (`spring-boot-starter-parent`).
* **IDE:** IntelliJ IDEA 2026.1.4.
* **Base de Datos:** MongoDB Atlas (Persistencia y caché con índices TTL de 24 horas).
* **Calidad y Pruebas:** JUnit 5, Mockito (Pruebas unitarias y de integración).

## Lógica de Negocio

* **Diseño Hexagonal:** Separación estricta en capas (`domain`, `ports.in`, `ports.out`, `application`, `infrastructure`).
* **Endpoint A (`/search`):** Consulta a TV Maze y retorno estructurado de shows (`id`, `name`, `channel`, `summary`, `genres`) enriquecidos con comentarios de MongoDB.
* **Endpoint B (`/shows/{id}`):** Validación previa de caché en MongoDB (patrón *cache-aside*); si no existe, consume la API externa, persiste y responde junto con sus comentarios.
* **Comentarios (`/comments`):** Registro de opiniones y calificaciones con validación estricta de rango (0 a 5) mediante Bean Validation.
* **Control Global:** Implementación de `@RestControllerAdvice` para estandarizar respuestas y manejar errores.

## Análisis de Calidad con SonarQube
El proyecto cuenta con una suite completa de pruebas unitarias e integración. Para ejecutar el análisis estático local con el servidor de SonarQube en Docker:

```bash
./mvnw sonar:sonar \
  -Dsonar.projectKey=kairos \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=TU_TOKEN