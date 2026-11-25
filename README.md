# Ratcher · Levantamiento móvil de infraestructura (CMC360)

MVP académico para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**
<<<<<<< HEAD
(Duoc UC · CITT, Sede San Joaquín) · **Evaluación Parcial 3 (consumo de APIs, pruebas y APK)**.
=======
(Duoc UC · CITT, Sede San Joaquín) · **Evaluación Parcial 2**.
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f

## Integrantes
| Integrante | 
|---|
| Arthur Coby | 
| Ignacio Sanchez |

- Equipo N°: 12
- Docente: Alvaro
- nombre del proyecto: Ratcher

## Contexto

CMC360 Ciberseguridad Industrial SpA necesita ordenar el levantamiento inicial de infraestructura
en Data Centers. Hoy un profesional revisa los racks, identifica los equipos y registra todo a mano,
lo que es lento y poco consistente.

**Solución:** una app Android que permite a un inspector tomar (o elegir) una foto autorizada de un rack,
registrar los datos del levantamiento, ver los componentes reconocidos de un catálogo acotado y
revisarlos (confirmar, corregir o rechazar), dejando todo guardado con trazabilidad.

> La app **no** emite auditorías ni certificaciones, y no valida normativa SEC-RIC (queda como evolución futura).

## Funcionalidades implementadas

| Módulo | Qué hace |
|---|---|
<<<<<<< HEAD
| Login | Inicio de sesión con validación por campo. Autenticación contra Xano (token). |
| Registro | Crear cuenta en Xano (nombre, correo, cargo, contraseña y confirmación). |
=======
| Login | Inicio de sesión con validación por campo. Usuarios guardados en base de datos local. |
| Registro | Crear cuenta (nombre, correo, cargo, contraseña y confirmación). |
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
| Lista de levantamientos | Muestra todos los levantamientos con foto, estado y cantidad de componentes por revisar. |
| Nuevo levantamiento | Formulario: ID del rack, área, fecha, observaciones y evidencia fotográfica. |
| Evidencia | Foto con la **cámara** (pide permiso) o imagen desde la **galería**, con vista previa. |
| Reconocimiento | Pantalla animada de análisis y lista de componentes reconocidos con su % de confianza. |
| Revisión | Confirmar, rechazar o **corregir** (formulario en ventana emergente) cada componente. |
| Validación | El levantamiento se valida cuando todos los componentes fueron revisados. |
| Trazabilidad | Historial de acciones: quién hizo qué y cuándo, ligado a la imagen. |

### Recursos nativos usados
1. **Cámara** (con permiso en tiempo de ejecución)
2. **Galería de imágenes** (selector del sistema)
<<<<<<< HEAD
3. **Notificaciones** del sistema: aviso al validar un levantamiento (pide `POST_NOTIFICATIONS` en Android 13+)
4. **Vibración** cuando hay errores en un formulario
=======
3. **Vibración** cuando hay errores en un formulario
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f

### Animaciones
- Transiciones entre pantallas (deslizar + desvanecer)
- Mensajes de error que aparecen y desaparecen
- Botón que se sacude cuando el formulario tiene errores
- Barra de confianza que se llena al mostrar cada componente
- Cambio animado de color en las etiquetas de estado
- Pulso del texto mientras se analiza la imagen

### Validaciones
Todas están en `util/Validaciones.kt` y tienen pruebas unitarias en
`app/src/test/.../ValidacionesTest.kt`. Cada campo muestra ícono de error o de correcto y un mensaje.

## Importante: el reconocimiento es simulado

El caso permite usar servicios de prueba. La clase ReconocedorSimulado
elige componentes del catálogo a partir del tamaño del archivo, por eso la misma foto siempre da el mismo resultado.
Para una versión real solo habría que reemplazar esa clase por un servicio de reconocimiento de imágenes.

## Arquitectura (MVVM)

| Capa | Carpeta | Responsabilidad |
|---|---|---|
| Vista (UI) | `IU/`, `IU/componentes/` | Pantallas y componentes visuales. No contienen lógica de validación. |
| ViewModel | `viewmodel/` | Estado de cada pantalla; llaman a las validaciones y a los repositorios. |
<<<<<<< HEAD
| Repositorio | `repository/` | Intermediario entre los ViewModels y la API de Xano. |
| Datos | `data/` | Cliente Retrofit, interfaces de la API de Xano, DTOs y conversión a modelos. |
=======
| Repositorio | `repository/` | Intermediario entre los ViewModels y la base de datos. |
| Datos | `data/` | Base de datos local SQLite (`BaseDatos`). |
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
| Modelo | `model/` | Clases de datos y catálogo de componentes. |
| Utilidades | `util/` | Validaciones, archivos/fotos, vibración, sesión, fechas. |

```
app/src/main/java/com/example/ratcher/
 ├─ IU/                  LoginScreen, RegistroScreen, HomeScreen,
 │   │                   NuevoLevantamientoScreen, DetalleScreen, AppNavegacion
 │   └─ componentes/     CampoTexto, BarraSuperior, TarjetaComponente, EtiquetaEstado, ...
 ├─ viewmodel/           Login, Registro, Home, NuevoLevantamiento, Detalle
 ├─ repository/          UsuarioRepository, LevantamientoRepository, ReconocedorSimulado
<<<<<<< HEAD
 ├─ data/                ApiConfig, ApiClient, AuthApi, RatcherApi, Dtos, Mapeos
 ├─ model/               Usuario, Levantamiento, Componente, Historial, Catalogo
 ├─ util/                Validaciones, Archivos, Vibracion, Notificaciones, Sesion, Fechas
 └─ ui/theme/            Color, Theme, Type
```

### Base de datos y API (Xano)
Los datos viven en Xano (tablas `user`, `levantamiento`, `componente`, `historial`) y la app los consume con
**Retrofit + Gson + OkHttp**. La foto de evidencia queda guardada en el teléfono y Xano guarda su ruta.
La guía de creación del backend está en [`docs/XANO_SETUP.md`](docs/XANO_SETUP.md).

| # | Método | Endpoint | Para qué se usa |
|---|---|---|---|
| 1 | POST | `/auth/signup` | Registro |
| 2 | POST | `/auth/login` | Login (devuelve token) |
| 3 | GET | `/auth/me` | Datos del usuario logueado |
| 4 | GET | `/levantamiento` | Lista de levantamientos (Home) |
| 5 | GET | `/levantamiento/{id}` | Detalle |
| 6 | POST | `/levantamiento` | Crear levantamiento |
| 7 | PATCH | `/levantamiento/{id}` | Validar (cambiar estado) |
| 8 | DELETE | `/levantamiento/{id}` | Eliminar levantamiento |
| 9 | GET | `/componente?levantamiento_id=` | Componentes de un levantamiento |
| 10 | POST | `/componente` | Guardar componentes reconocidos |
| 11 | PATCH | `/componente/{id}` | Confirmar, corregir o rechazar |
| 12 | GET | `/historial?levantamiento_id=` | Trazabilidad |
| 13 | POST | `/historial` | Registrar acción en el historial |

Todas las peticiones (menos login y registro) llevan el token en `Authorization: Bearer ...`
(`ApiClient` lo agrega solo). Si no hay conexión la app muestra un aviso y no se cierra.
=======
 ├─ data/                BaseDatos
 ├─ model/               Usuario, Levantamiento, Componente, Historial, Catalogo
 ├─ util/                Validaciones, Archivos, Vibracion, Sesion, Fechas, Seguridad
 └─ ui/theme/            Color, Theme, Type
```

### Base de datos (SQLite local)
`usuarios` · `levantamientos` · `componentes` · `historial`
(los componentes y el historial se borran solos si se elimina su levantamiento).
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f

### Catálogo de componentes
Switch · Patch panel · Servidor · UPS · PDU · Router · Firewall · Cableado · Rack vacío

## Flujo principal

```
<<<<<<< HEAD
Login (Xano) → Lista de levantamientos → Nuevo levantamiento (datos + foto)
=======
Login → Lista de levantamientos → Nuevo levantamiento (datos + foto)
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
      → Análisis de imagen → Detalle (confirmar / corregir / rechazar) → Validar
```

## Cómo ejecutar

<<<<<<< HEAD
1. Crear el backend en Xano siguiendo [`docs/XANO_SETUP.md`](docs/XANO_SETUP.md).
2. Pegar las URLs de Xano en `data/ApiConfig.kt`.
3. Abrir la carpeta del proyecto en **Android Studio** (`File → Open`) y esperar la sincronización de Gradle.
4. Elegir un emulador o teléfono Android (mínimo API 24) y presionar run.
5. Crear una cuenta desde "Registro" e iniciar sesión.
6. Para probar la cámara en el emulador: *Device Manager → Edit → Show Advanced Settings → Camera*.

## Pruebas unitarias

`./gradlew test` (no requieren emulador ni internet):

| Archivo | Qué prueba |
|---|---|
| `ValidacionesTest` | Reglas de validación de los formularios |
| `ReconocedorSimuladoTest` | Resultado determinista, cantidad de componentes y rango de confianza |
| `MapeosTest` | Conversión de las respuestas de Xano a los modelos de la app |
| `RatcherApiTest` | Consumo de la API con un servidor falso (`MockWebServer`): lectura del JSON, token en la cabecera y cuerpo del POST |

## Generar el APK firmado (release)

- Terminal: `./gradlew assembleRelease` → `app/build/outputs/apk/release/app-release.apk`
- Android Studio: *Build → Generate Signed App Bundle / APK → APK*, usando `app/ratcher-release.jks`
  (alias `ratcher`, clave `ratcher2026`).

La llave está en el repositorio solo por ser un proyecto académico; en un proyecto real no se sube a Git.
El ícono personalizado (rack de servidores) está en `res/drawable/ic_launcher_*.xml` y `res/mipmap-*`.
=======
1. Abrir la carpeta del proyecto en **Android Studio** (`File → Open`).
2. Esperar a que termine la sincronización de Gradle.
3. Elegir un emulador o un teléfono Android (mínimo API 24) y presionar run.
4. Ingresar con el usuario de prueba: `test@test.com` / `123456` (o crear una cuenta nueva).
5. Para probar la cámara en el emulador: *Device Manager → Edit → Show Advanced Settings → Camera*.
   Si no funciona, usar el botón .

Para correr las pruebas unitarias: `./gradlew test`
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f

## Tecnologías

Kotlin · Jetpack Compose (Material 3) · Navigation Compose · ViewModel con estado de Compose ·
<<<<<<< HEAD
Retrofit · OkHttp · Gson · Xano (API REST) · Notificaciones · Activity Result API (cámara y galería) · FileProvider · Git + GitHub · Trello
=======
SQLite (`SQLiteOpenHelper`) · Activity Result API (cámara y galería) · FileProvider · Git + GitHub · Trello
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f

## Datos de prueba

Solo se deben usar imágenes autorizadas, de laboratorio o públicas, sin rostros, direcciones IP,
credenciales ni datos sensibles
