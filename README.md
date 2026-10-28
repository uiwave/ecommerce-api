# E-commerce API

API REST de e-commerce construida con Spring Boot 4, Java 21 y PostgreSQL.
Incluye autenticación stateless con JWT, roles (`USER`/`ADMIN`) y refresh tokens con rotación.

## Requisitos

- Java 21+
- PostgreSQL en ejecución
- Maven Wrapper incluido (`mvnw`)

## Configuración

1. Copia el archivo de ejemplo y completa los valores:

   ```bash
   cp .env.example .env
   ```

2. Genera un `JWT_SECRET` de al menos 32 caracteres. Por ejemplo:

   ```bash
   openssl rand -base64 48
   ```

3. Crea la base de datos si no existe:

   ```sql
   CREATE DATABASE ecommerce_db;
   ```

Las migraciones de Flyway (`src/main/resources/db/migration`) crean las tablas automáticamente al arrancar.

## Ejecutar

```bash
./mvnw spring-boot:run
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

En la primera ejecución se crea el administrador definido en `ADMIN_EMAIL` / `ADMIN_PASSWORD`
(solo si no existe todavía).

## Endpoints de autenticación

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/auth/register` | público | Registro (rol `USER`) |
| POST | `/api/auth/login` | público | Devuelve `accessToken` y `refreshToken` |
| POST | `/api/auth/refresh` | público | Rota el refresh token |
| POST | `/api/auth/logout` | autenticado | Revoca el refresh token |
| GET | `/api/auth/me` | autenticado | Usuario actual |
| GET | `/api/admin/hello` | `ADMIN` | Endpoint de prueba de roles |

### Ejemplo rápido

```bash
# Registro
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"cliente@test.com","password":"Cliente123!","fullName":"Cliente"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"cliente@test.com","password":"Cliente123!"}'

# Recurso protegido
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <accessToken>"
```

El access token expira en 15 minutos por defecto; usa `/api/auth/refresh` para renovarlo.

## Arquitectura

Organización por módulo/feature:

- `auth/` — registro, login, refresh y logout
- `user/` — entidad `User`, enum `Role`, `RefreshToken` y repositorios
- `security/` — JWT, filtro de autenticación y respuestas 401/403
- `config/` — seguridad, CORS y OpenAPI
- `admin/` — endpoints de administración
- `common/exception/` — manejo global de errores

## Tests

```bash
./mvnw test
```
