# Evaluación Kairos Backend - TV Maze Middleware (Fase 1 Inciso A y B)

API middleware en Java y Spring Boot que se conecta a TV Maze para realizar la búsqueda y consulta detallada de shows, aplicando Arquitectura Hexagonal, resiliencia y manejo global de errores.

## Tecnologias utilizadas

* **Lenguaje:** Java 17 (Uso de *records* e inmutabilidad).
* **Framework:** Spring Boot 3.3.2 (`spring-boot-starter-parent`).
* **IDE:** IntelliJ IDEA 2026.1.4.
* **Resiliencia:** Resilience4j (Circuit Breaker con patrones de tolerancia a fallos para la API externa).
* **Calidad y Pruebas:** JUnit 5, Mockito (Pruebas unitarias y de integración).

## Arquitectura y Lógica de Negocio

* **Diseño Hexagonal:** Separación estricta en capas (`domain`, `ports.in`, `ports.out`, `application`, `infrastructure`).
* **Resiliencia:** Manejo de degradación de servicio mediante `@CircuitBreaker` para proteger la comunicación con la API externa de TV Maze.
* **Control Global de Excepciones:** `@RestControllerAdvice` para interceptar errores no controlados y estandarizar la estructura del `ErrorResponse`.

## Endpoints Desarrollados (Fase 1)

### Inciso A: Búsqueda de Shows (`/shows/search`)
Consulta a la API de TV Maze a partir de un término de búsqueda (`search_query`) y retorna un arreglo de shows formateados con los atributos requeridos.

* **API Externa consumida:** `[http://api.tvmaze.com/search/shows?q=](http://api.tvmaze.com/search/shows?q=){query}`
* **Estructura de Respuesta:** Arreglo de objetos con `id`, `name`, `channel` (`network.name` o `webChannel.name`), `summary` y `genres`.

### Inciso B: Detalle de Show por ID (`/shows/{id}`)
Obtiene la información detallada de un show específico a partir de su ID.

* **API Externa consumida:** `[https://api.tvmaze.com/shows/](https://api.tvmaze.com/shows/){show_id}`
* **Estructura de Respuesta:** Objeto `show` completo mapeado a la entidad de dominio.

## Ejecutar Pruebas Unitarias y de Integración:
./mvnw clean test

Configuracion / Ejecucion Local

![Configuración Proyecto IntelliJ Kairos](https://github.com/user-attachments/assets/383bcb25-34ff-4b5e-b023-e665b53b6276)

Opcional Ejecucion en cURL :
```bash
# Inciso A: Buscar shows por término
curl -X GET "http://localhost:8080/shows/search?q=batman" \
     -H "Accept: application/json"
     
# Inciso B: Caso de éxito por ID
curl -X GET "http://localhost:8080/shows/1" \
     -H "Accept: application/json"
     
# Manejo de error (Show no encontrado)
curl -X GET "http://localhost:8080/shows/99999999" \
     -H "Accept: application/json"

# Analisis con Sonar Qube
./mvnw sonar:sonar \
  -Dsonar.projectKey=kairos \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=TU_TOKEN```