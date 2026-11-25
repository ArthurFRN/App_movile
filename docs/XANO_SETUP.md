# Configuración del backend en Xano

La app consume una API REST creada en Xano. Esta guía deja el backend listo en pocos minutos.

## 1. Tablas

`user` (ya existe) → agregar el campo **`cargo`** (text, por defecto vacío).

| Tabla | Campos (además de `id` y `created_at`) |
|---|---|
| `levantamiento` | `rack_id` text · `area` text · `fecha` text · `observaciones` text (permitir vacío) · `ruta_imagen` text · `responsable` text · `estado` text (default `En revisión`) |
| `componente` | `levantamiento_id` integer · `categoria` text · `descripcion` text · `confianza` integer · `estado` text (default `Reconocido`) · `categoria_original` text |
| `historial` | `levantamiento_id` integer · `accion` text · `detalle` text · `fecha` text · `usuario` text |

> `event_log` no se usa; puede quedar sin tocar.

## 2. Endpoints (13)

Crear todos en **un mismo grupo de API** (ej. `ratcher`). Todos, excepto `signup` y `login`, requieren autenticación con la tabla `user`.

| # | Método | Ruta | Entradas | Respuesta |
|---|---|---|---|---|
| 1 | POST | `/auth/signup` | name, email, password, **cargo** | `{ "authToken": "..." }` |
| 2 | POST | `/auth/login` | email, password | `{ "authToken": "..." }` |
| 3 | GET | `/auth/me` | — | usuario (id, name, email, cargo) |
| 4 | GET | `/levantamiento` | — | arreglo de levantamientos |
| 5 | GET | `/levantamiento/{id}` | id | un levantamiento (404 si no existe) |
| 6 | POST | `/levantamiento` | rack_id, area, fecha, observaciones, ruta_imagen, responsable, estado | el registro creado (con `id`) |
| 7 | PATCH | `/levantamiento/{id}` | estado | registro actualizado |
| 8 | DELETE | `/levantamiento/{id}` | id | borra el levantamiento **y** sus `componente` e `historial` (bulk delete por `levantamiento_id`) |
| 9 | GET | `/componente` | `levantamiento_id` (opcional: si viene, filtra) | arreglo de componentes |
| 10 | POST | `/componente` | levantamiento_id, categoria, descripcion, confianza, estado, categoria_original | registro creado |
| 11 | PATCH | `/componente/{id}` | categoria, descripcion, estado (actualización parcial) | registro actualizado |
| 12 | GET | `/historial` | `levantamiento_id` (opcional: si viene, filtra) | arreglo de historial |
| 13 | POST | `/historial` | levantamiento_id, accion, detalle, fecha, usuario | registro creado |

Las respuestas de los GET de lista deben ser un **arreglo JSON simple** (sin paginación).

## 3. Prompt para el Xano Agent (botón "Xano Agent")

```
Create these database tables and one API group called "ratcher".

Tables:
- Add a text field "cargo" to the existing table "user".
- levantamiento: rack_id (text), area (text), fecha (text), observaciones (text, optional), ruta_imagen (text), responsable (text), estado (text, default "En revisión").
- componente: levantamiento_id (integer), categoria (text), descripcion (text), confianza (integer), estado (text, default "Reconocido"), categoria_original (text).
- historial: levantamiento_id (integer), accion (text), detalle (text), fecha (text), usuario (text).

API group "ratcher" with these endpoints (JSON in/out, snake_case field names exactly as above):
1. POST /auth/signup: inputs name, email, password, cargo. Creates a user (hashed password) and returns {"authToken": ...}.
2. POST /auth/login: inputs email, password. Returns {"authToken": ...}.
3. GET /auth/me: authenticated, returns the current user without password (id, name, email, cargo).
4. GET /levantamiento: authenticated, returns a plain array of all records (no pagination).
5. GET /levantamiento/{id}: authenticated, returns one record or 404.
6. POST /levantamiento: authenticated, inputs rack_id, area, fecha, observaciones (optional), ruta_imagen, responsable, estado. Returns the created record including id.
7. PATCH /levantamiento/{id}: authenticated, updates only the supplied fields (estado).
8. DELETE /levantamiento/{id}: authenticated, deletes the record AND all componente and historial rows whose levantamiento_id equals the id.
9. GET /componente: authenticated, optional query input levantamiento_id; when provided, filter by it, otherwise return all. Plain array.
10. POST /componente: authenticated, inputs levantamiento_id, categoria, descripcion, confianza, estado, categoria_original. Returns created record.
11. PATCH /componente/{id}: authenticated, partial update of categoria, descripcion, estado.
12. GET /historial: authenticated, optional query input levantamiento_id used as a filter. Plain array.
13. POST /historial: authenticated, inputs levantamiento_id, accion, detalle, fecha, usuario. Returns created record.
```

## 4. Conectar la app

1. En Xano abre el grupo de API → copia **Base Request URL** (ej. `https://x8ki-letl-twmt.n7.xano.io/api:AbCdEf12`).
2. Pega la URL en `app/src/main/java/com/example/ratcher/data/ApiConfig.kt` (`URL_AUTH` y `URL_DATOS`; si usas el grupo "Authentication" que trae Xano para el login, ponle a `URL_AUTH` la URL de ese grupo).
3. Ejecuta la app, crea una cuenta desde "Registro" e inicia sesión.

## 5. Probar rápido

En Xano → grupo → endpoint → **Run & Debug**. Verifica que `login` devuelva `authToken`, y que `POST /levantamiento` devuelva el `id`.

Nota: en el plan gratuito Xano puede limitar las peticiones por segundo; la app reintenta automáticamente si recibe un 429.
