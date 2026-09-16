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

curl -X GET "http://localhost:8080/shows/search?q=batman" -H "Accept: application/json"


* **Endpoint B (`/shows/{id}`):** Validación previa de caché en MongoDB (patrón *cache-aside*); si no existe, consume la API externa, persiste y responde junto con sus comentarios.



* **Comentarios (`/comments`):** Registro de opiniones y calificaciones con validación estricta de rango (0 a 5) mediante Bean Validation.

curl -X POST "http://localhost:8080/shows/1/comments" \
-H "Content-Type: application/json" \
-d '{"comment": "Excelente serie de televisión", "rating": 5}'

Prueba Post Inválido
curl -X POST "http://localhost:8080/shows/1/comments" \
-H "Content-Type: application/json" \
-d '{"comment": "No me gustó", "rating": 10}'

* **Control Global:** Implementación de `@RestControllerAdvice` para estandarizar respuestas y manejar errores.

## Análisis de Calidad con SonarQube
El proyecto cuenta con una suite completa de pruebas unitarias e integración. Para ejecutar el análisis estático local con el servidor de SonarQube en Docker:

```bash
./mvnw sonar:sonar \
  -Dsonar.projectKey=kairos \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=TU_TOKEN

Configuracion y Ejecucion Local

Para ejecutar la aplicación de forma segura sin exponer credenciales en el código fuente, debes configurar la variable de entorno MONGODB_URI.

Desde la Terminal:
```bash
export MONGODB_URI="mongodb+srv://imash1709_db_user:70LtJYtBUzVjC6q2@cluster0.2lokjlx.mongodb.net/tvmaze_db?retryWrites=true&w=majority&appName=Cluster0"
./mvnw spring-boot:run

<img width="778" height="697" alt="Image" src="https://github.com/user-attachments/assets/383bcb25-34ff-4b5e-b023-e665b53b6276" />

 