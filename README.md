# Ratcher · Levantamiento móvil de infraestructura (CMC360)

MVP académico para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**
(Duoc UC · CITT, Sede San Joaquín) · **Evaluación Parcial 2**.

## Integrantes

| Integrante | 
|---|---|----|
| Arthur Coby | |
| Ignacio Sanchez ||

- Equipo N°: 12
- Docente: Alvaro

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
| Login | Inicio de sesión con validación por campo. Usuarios guardados en base de datos local. |
| Registro | Crear cuenta (nombre, correo, cargo, contraseña y confirmación). |
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
3. **Vibración** cuando hay errores en un formulario

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
| Repositorio | `repository/` | Intermediario entre los ViewModels y la base de datos. |
| Datos | `data/` | Base de datos local SQLite (`BaseDatos`). |
| Modelo | `model/` | Clases de datos y catálogo de componentes. |
| Utilidades | `util/` | Validaciones, archivos/fotos, vibración, sesión, fechas. |

```
app/src/main/java/com/example/ratcher/
 ├─ IU/                  LoginScreen, RegistroScreen, HomeScreen,
 │   │                   NuevoLevantamientoScreen, DetalleScreen, AppNavegacion
 │   └─ componentes/     CampoTexto, BarraSuperior, TarjetaComponente, EtiquetaEstado, ...
 ├─ viewmodel/           Login, Registro, Home, NuevoLevantamiento, Detalle
 ├─ repository/          UsuarioRepository, LevantamientoRepository, ReconocedorSimulado
 ├─ data/                BaseDatos
 ├─ model/               Usuario, Levantamiento, Componente, Historial, Catalogo
 ├─ util/                Validaciones, Archivos, Vibracion, Sesion, Fechas, Seguridad
 └─ ui/theme/            Color, Theme, Type
```

### Base de datos (SQLite local)
`usuarios` · `levantamientos` · `componentes` · `historial`
(los componentes y el historial se borran solos si se elimina su levantamiento).

### Catálogo de componentes
Switch · Patch panel · Servidor · UPS · PDU · Router · Firewall · Cableado · Rack vacío

## Flujo principal

```
Login → Lista de levantamientos → Nuevo levantamiento (datos + foto)
      → Análisis de imagen → Detalle (confirmar / corregir / rechazar) → Validar
```

## Cómo ejecutar

1. Abrir la carpeta del proyecto en **Android Studio** (`File → Open`).
2. Esperar a que termine la sincronización de Gradle.
3. Elegir un emulador o un teléfono Android (mínimo API 24) y presionar run.
4. Ingresar con el usuario de prueba: `test@test.com` / `123456` (o crear una cuenta nueva).
5. Para probar la cámara en el emulador: *Device Manager → Edit → Show Advanced Settings → Camera*.
   Si no funciona, usar el botón .

Para correr las pruebas unitarias: `./gradlew test`

## Tecnologías

Kotlin · Jetpack Compose (Material 3) · Navigation Compose · ViewModel con estado de Compose ·
SQLite (`SQLiteOpenHelper`) · Activity Result API (cámara y galería) · FileProvider · Git + GitHub · Trello

## Datos de prueba

Solo se deben usar imágenes autorizadas, de laboratorio o públicas, sin rostros, direcciones IP,
credenciales ni datos sensibles
