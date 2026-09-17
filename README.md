# Evaluación Kairos Backend - TV Maze Middleware

API middleware en Java y Spring Boot que se conecta a TV Maze, integrando persistencia, caché con TTL, validaciones y manejo global de errores con MongoDB Atlas.

## Tecnologias utilizadas

* **Lenguaje:** Java 17 (Uso de *records* e inmutabilidad).
* **Framework:** Spring Boot 3.3.2 (`spring-boot-starter-parent`).
* **IDE:** IntelliJ IDEA 2026.1.4.
* **Base de Datos:** MongoDB Atlas (Persistencia y caché con índices TTL de 24 horas).
* **Pruebas y Calidad:** JUnit 5, Mockito, AssertJ, Spring Testcontainers y MockMvc.
* **Resiliencia:** Resilience4j (Circuit Breaker y tolerancia a fallos para la API externa).

## Lógica de Negocio

* **Diseño Hexagonal:** Separación en capas (`domain`, `ports.in`, `ports.out`, `application`, `infrastructure`).

Endpoint A: Buscar shows

curl -X GET "http://localhost:8080/shows/search?q=batman" -H "Accept: application/json"

Endpoint B: Obtener show por ID (Cache-Aside en MongoDB)
curl -X GET "http://localhost:8080/shows/1" -H "Accept: application/json"

Endpoint C: Guardar comentario y calificación (0-5)
curl -X POST "http://localhost:8080/shows/1/comments" -H "Content-Type: application/json" -d '{"comment": "Excelente serie de televisión", "rating": 5}'

Prueba Post Inválido (Rating fuera de rango)
curl -X POST "http://localhost:8080/shows/1/comments" -H "Content-Type: application/json" -d '{"comment": "No me gustó", "rating": 10}'

* **Control Global:** Implementación de `@RestControllerAdvice` para estandarizar respuestas y manejar errores.

## Análisis de Calidad con SonarQube
El proyecto cuenta con una suite completa de pruebas unitarias e integración. Para ejecutar el análisis estático local con el servidor de SonarQube en Docker:

## Ejecutar Pruebas Unitarias y de Integración:
./mvnw test

## Opcional : Configurar en IntelliJ las credenciales de MongoDB

1.- Editar Configuration

2.- Agregar Environment Variables

Name = MONGODB_URI

Uri = mongodb+srv://imash1709_db_user:70LtJYtBUzVjC6q2@cluster0.2lokjlx.mongodb.net/tvmaze_db?retryWrites=true&w=majority&appName=Cluster0

![Configuración Proyecto IntelliJ Kairos](https://github.com/user-attachments/assets/8e9ba124-07e2-4ea1-8ba3-91c42044aff0)

## Registros en mongo
![Registros en Mongo](https://github.com/user-attachments/assets/e658d0e2-8bc1-485a-9841-401c5596de78)

```bash
./mvnw sonar:sonar \
  -Dsonar.projectKey=kairos \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=TU_TOKEN

Configuracion y Ejecucion Local

Para ejecutar la aplicación de forma segura sin exponer credenciales en el código fuente, debes configurar la variable de entorno MONGODB_URI.

Desde la Terminal:

export MONGODB_URI="mongodb+srv://imash1709_db_user:70LtJYtBUzVjC6q2@cluster0.2lokjlx.mongodb.net/tvmaze_db?retryWrites=true&w=majority&appName=Cluster0"
./mvnw spring-boot:run