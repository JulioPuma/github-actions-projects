# Backend Project — CRUD Reactivo con WebFlux

Aplicación demo en Spring Boot (WebFlux) que expone un CRUD reactivo para la entidad Usuario. Usa una capa de datos en memoria mediante la clase `BaseDatos` (patrón Singleton), registra logs con SLF4J y publica la documentación OpenAPI con Swagger UI.

## Tecnologías
- Java 21, Spring Boot 3.5.x
- Spring WebFlux (reactivo) y Reactor (Mono/Flux)
- SLF4J (logback por defecto con Spring Boot)
- springdoc-openapi (Swagger UI para WebFlux)
- JUnit 5 + WebTestClient para pruebas

## Ejecutar la aplicación
```pwsh
# Empaquetar (opcional)
mvn -f "c:\Users\julio\Documents\GIT Projects\github-projects\github-actions-projects\backend-project\pom.xml" -DskipTests=true package

# Iniciar la aplicación
yarn --version > $null 2>&1; # no-op, solo ejemplo de powershell
mvn -f "c:\Users\julio\Documents\GIT Projects\github-projects\github-actions-projects\backend-project\pom.xml" spring-boot:run
```

Por defecto la app escucha en `http://localhost:8080`.

## Swagger / OpenAPI
- Swagger UI: `http://localhost:8080/swagger`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Desde Swagger UI puedes inspeccionar el contrato y ejecutar las peticiones a los endpoints.

## Endpoints (CRUD Usuario)
Base path: `/api/usuarios`

- POST `/api/usuarios`
  - Crea un usuario
  - Respuestas: `201 Created`, `409 Conflict` si ya existe el correo
- GET `/api/usuarios`
  - Lista todos los usuarios
  - Respuesta: `200 OK`
- GET `/api/usuarios/{correo}`
  - Obtiene un usuario por correo
  - Respuestas: `200 OK`, `404 Not Found` si no existe
- PUT `/api/usuarios/{correo}`
  - Actualiza campos de un usuario existente
  - Respuestas: `200 OK`, `404 Not Found` si no existe
- DELETE `/api/usuarios/{correo}`
  - Elimina un usuario por correo
  - Respuesta: `204 No Content`

### Modelo Usuario
Campos:
- `nombre` (string)
- `apellido` (string)
- `edad` (number)
- `fecha_registro` (string ISO-8601, se setea automáticamente si no viene)
- `correo` (string, actúa como identificador único)
- `contrasena` (string, solo escritura; no aparece en respuestas)

Ejemplo de creación (POST `/api/usuarios`):
```json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "edad": 30,
  "correo": "juan@example.com",
  "contrasena": "secreta"
}
```

Ejemplo de actualización (PUT `/api/usuarios/juan@example.com`):
```json
{
  "nombre": "Juan Carlos"
}
```

## Logs
La aplicación usa SLF4J para registrar eventos en la capa de servicio y en la “BaseDatos” en memoria.

## Pruebas
```pwsh
mvn -f "c:\Users\julio\Documents\GIT Projects\github-projects\github-actions-projects\backend-project\pom.xml" -DskipTests=false test
```
Las pruebas usan `WebTestClient` y cubren las rutas principales del CRUD.

## Notas
- No hay base de datos externa; los datos viven en memoria durante la ejecución.
- El campo `contrasena` es de solo escritura (write-only) en JSON y no se devuelve en las respuestas.