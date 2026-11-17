# Rinconcito Marino API

API REST del restaurante Rinconcito Marino: reservas, opiniones y administración con roles.
Hecha con Spring Boot 4, Spring Data JPA, MySQL, Spring Security y JWT. Frontend en Angular (carpeta `frontend/`).

## Requisitos
- JDK 21
- MySQL 8 en `localhost:3306` (la base `rinconcito_marino` se crea sola)

## Variables de entorno
| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC de MySQL (opcional, por defecto localhost) |
| `CORS_ORIGINS` | Orígenes permitidos del frontend (opcional) |
| `DB_USER` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Secreto para firmar tokens (mínimo 32 caracteres) |
| `ADMIN_USER` | Usuario del administrador inicial |
| `ADMIN_PASSWORD` | Contraseña del administrador inicial |

El administrador inicial se crea al arrancar solo si no existe.

## Ejecutar
```powershell
.\mvnw spring-boot:run
```
Con MySQL local no hace falta configurar variables; al arrancar se crean el admin inicial y 10 mesas de ejemplo.
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
| GET | `/api/reservas/{id}/detalle` (mesas, pago, atención, historial) | ADMIN, EMPLEADO |
| PUT | `/api/reservas/{id}/mesas` | ADMIN, EMPLEADO |
| POST | `/api/reservas/{id}/pago` | ADMIN, EMPLEADO |
| PATCH | `/api/reservas/{id}/pago/estado` (PAGADO confirma la reserva) | ADMIN, EMPLEADO |
| POST | `/api/reservas/{id}/atencion` · PATCH `/api/reservas/{id}/atencion/salida` | ADMIN, EMPLEADO |
| GET | `/api/mesas` | ADMIN, EMPLEADO |
| POST / PUT / DELETE | `/api/mesas`, `/api/mesas/{id}` | ADMIN |

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