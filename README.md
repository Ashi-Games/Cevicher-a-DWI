# Rinconcito Marino API

API REST del restaurante Rinconcito Marino: reservas, opiniones y administración con roles.
Hecha con Spring Boot 4, Spring Data JPA, PostgreSQL (Supabase), Spring Security y JWT.

## Requisitos
- JDK 21
- Un proyecto de Supabase (base de datos PostgreSQL)

## Variables de entorno
| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC, ej. `jdbc:postgresql://HOST:5432/postgres?sslmode=require` (Session pooler de Supabase) |
| `DB_USER` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Secreto para firmar tokens (mínimo 32 caracteres) |
| `ADMIN_USER` | Usuario del administrador inicial |
| `ADMIN_PASSWORD` | Contraseña del administrador inicial |

El administrador inicial se crea al arrancar solo si no existe.

## Ejecutar
```powershell
$env:DB_URL="jdbc:postgresql://HOST:5432/postgres?sslmode=require"
$env:DB_USER="..."
$env:DB_PASSWORD="..."
$env:JWT_SECRET="..."
$env:ADMIN_USER="admin"
$env:ADMIN_PASSWORD="..."
.\mvnw spring-boot:run
```
La API queda en `http://localhost:8080`.

## Endpoints
| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/auth/login` | Público |
| POST | `/api/reservas` | Público |
| POST | `/api/opiniones` | Público |
| GET | `/api/reservas`, `/api/reservas/{id}`, `/api/reservas/stats` | ADMIN, EMPLEADO |
| GET | `/api/opiniones` | ADMIN, EMPLEADO |
| PATCH | `/api/reservas/{id}/estado` | ADMIN, EMPLEADO |
| DELETE | `/api/reservas/{id}`, `/api/opiniones/{id}` | ADMIN |

Las rutas protegidas requieren el header `Authorization: Bearer <token>`, con el token obtenido en el login.

## Pruebas
```powershell
.\mvnw test
```
41 pruebas: servicios (JUnit + Mockito), controllers (MockMvc) y reglas de seguridad por rol.

## Estructura
`controller` · `service` · `repository` · `model` · `dto` · `security` · `config` · `exception`

## Autores
[grupo de dwi]