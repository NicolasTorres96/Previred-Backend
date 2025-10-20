# person-crud (Backend) — Desafío Técnico 2025

Backend en **Java 21**, **Spring Boot 3.5.4**, **Maven**, que implementa un CRUD de **Personas** con:
- Validaciones de entrada (incluye validador de **RUT chileno**).
- Cálculo de **edad** en la respuesta.
- Manejo de errores centralizado con **ProblemDetail** (RFC 7807) y excepciones de dominio.
- Mecanismo **"persistir luego"**: si la BD no está disponible al crear/actualizar/eliminar, la solicitud se **encola** en disco y un **scheduler** reintenta aplicarla cuando el servicio vuelva.

> Estructura basada en capas **controller / service / repository / domain / dto / exception / validation**.

## Requisitos
- **Java 21**
- **Maven 3.9+**

## Configuración rápida

La app usa **H2 file** por defecto (archivo en `./data/personas`). Consola en `/h2-console`.

Archivo `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/personas;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE
  jpa:
    hibernate:
      ddl-auto: update
fallback:
  queueDir: ./data/fallback-queue
  retryIntervalMs: 10000
```

## Cómo ejecutar

```bash
# 1) Compilar y ejecutar pruebas
mvn clean verify

# 2) Ejecutar la aplicación
mvn spring-boot:run
```

La API quedará en `http://localhost:8080`.

## Endpoints
- `POST /api/v1/personas` — Crea persona
- `GET /api/v1/personas` — Lista personas
- `GET /api/v1/personas/{id}` — Obtiene persona por ID (UUID)
- `PUT /api/v1/personas/{id}` — Actualiza persona
- `DELETE /api/v1/personas/{id}` — Elimina persona

### EJEMPLO (JSON)
```json
{
  "rut": "123456785",
  "nombre": "Ada",
  "apellido": "Lovelace",
  "fechaNacimiento": "1990-10-01",
  "calle": "Av. Siempre Viva 123",
  "comuna": "Santiago",
  "region": "RM"
}
```

### Respuesta
```json
{
  "id": "UUID",
  "rut": "123456785",
  "nombre": "Ada",
  "apellido": "Lovelace",
  "fechaNacimiento": "1990-10-01",
  "edad": 34,
  "calle": "Av. Siempre Viva 123",
  "comuna": "Santiago",
  "region": "RM"
}
```

## Validaciones
- **RUT**: formato y dígito verificador.
- `nombre`, `apellido`, `calle`, `comuna`, `region`: **@NotBlank**.
- `fechaNacimiento`: **@Past**.

## Manejo de errores

Las respuestas de error usan `ProblemDetail` (RFC 7807).

## "Persistir luego" (cuando la BD está caída)
- En `PersonService`, si ocurre `DataAccessException`, la operación se **encola** como JSON en `./data/fallback-queue`.
- `PersistLaterService` ejecuta un **@Scheduled** que **reintenta** aplicar las operaciones pendientes contra la BD cada `fallback.retryIntervalMs` ms.
- Si el archivo está corrupto o la entidad ya no existe (casos de **update/delete**), se descarta.

> Esto cumple con el requisito de **"asegurarse que el registro quede en la BD, aunque esta no está disponible en ese momento"** encolando operaciones para su posterior aplicación.

## Estructura de paquetes
```
cl.previred.personas
├── controller
├── domain
├── dto
├── exception
├── repository
├── service
├── validation
└── viewmodel
```

## Ejecutarlo en otro equipo
1. Instala **Java 21** y **Maven 3.9+**.
2. Clona/copiar este proyecto.
3. (Opcional) Ajusta la configuración de BD en `application.yml` si quieres usar **PostgreSQL** u otra.
4. Ejecuta:
   ```bash
   mvn clean package
   java -jar target/person-crud-0.0.1-SNAPSHOT.jar
   ```

