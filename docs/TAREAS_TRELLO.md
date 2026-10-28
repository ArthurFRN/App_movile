# Tareas del proyecto Ratcher (para Trello)

Leyenda: ✅ = ya está hecho en el código · ⬜ = lo tienen que hacer ustedes

**Cómo usarlo en Trello:** creen 3 listas (**Por hacer**, **En progreso**, **Hecho**) y una tarjeta por cada línea.
Pongan las ✅ en "Hecho" **a medida que las suban a GitHub** y asignen cada tarjeta a Arthur o a Ignacio.
Los commits y las tarjetas deben tener fechas reales y repartidas: es lo que más pesa en la pauta (20 %).

---

## 1. Planificación y diseño
- ✅ Definir el caso (CMC360, levantamiento de racks) y el alcance del MVP — *Arthur*
- ✅ Definir catálogo de componentes (Switch, Patch panel, Servidor, UPS, PDU, Router, Firewall, Cableado, Rack vacío) — *Ignacio*
- ✅ Definir las 6 pantallas y el flujo de navegación — *Arthur*
- ✅ Definir paleta de colores y tema de la app — *Ignacio*

## 2. Base del proyecto
- ✅ Login con validación (ya existía, se mejoró) — *Arthur*
- ✅ Agregar dependencias: Navigation Compose, ViewModel Compose, íconos — *Ignacio*
- ✅ Crear modelos: Usuario, Levantamiento, Componente, Historial, Catalogo — *Ignacio*

## 3. Persistencia local
- ✅ Crear base de datos SQLite con 4 tablas (BaseDatos.kt) — *Ignacio*
- ✅ Repositorio de usuarios (login y registro) — *Arthur*
- ✅ Repositorio de levantamientos (crear, listar, componentes, historial) — *Ignacio*
- ✅ Guardar contraseñas con hash (Seguridad.kt) — *Arthur*

## 4. Lógica y validaciones
- ✅ Validaciones centralizadas (Validaciones.kt) — *Arthur*
- ✅ Pruebas unitarias de las validaciones (ValidacionesTest.kt) — *Arthur*
- ✅ Reconocedor simulado de componentes — *Ignacio*
- ✅ Sesión del usuario (Sesion.kt) y fechas (Fechas.kt) — *Arthur*

## 5. ViewModels (MVVM)
- ✅ LoginViewModel — *Arthur*
- ✅ RegistroViewModel — *Arthur*
- ✅ HomeViewModel — *Ignacio*
- ✅ NuevoLevantamientoViewModel — *Ignacio*
- ✅ DetalleViewModel (confirmar, corregir, rechazar, validar, eliminar) — *Ignacio*

## 6. Interfaz y formularios
- ✅ Componente reutilizable CampoTexto (ícono, error, correcto, mensaje) — *Arthur*
- ✅ Componentes: BarraSuperior, EtiquetaEstado, GrupoOpciones, ImagenEvidencia — *Arthur*
- ✅ Pantalla de Login — *Arthur*
- ✅ Pantalla de Registro — *Arthur*
- ✅ Pantalla de lista de levantamientos (Home) — *Ignacio*
- ✅ Formulario Nuevo levantamiento — *Ignacio*
- ✅ Pantalla de Detalle con tarjetas de componentes — *Ignacio*
- ✅ Formulario "Corregir componente" (ventana emergente) — *Ignacio*
- ✅ Navegación entre pantallas (AppNavegacion.kt) — *Ignacio*

## 7. Recursos nativos
- ✅ Cámara con permiso en tiempo de ejecución + FileProvider — *Ignacio*
- ✅ Galería de imágenes — *Ignacio*
- ✅ Vibración ante errores — *Arthur*

## 8. Animaciones
- ✅ Transiciones entre pantallas — *Ignacio*
- ✅ Mensajes de error animados y botón que se sacude — *Arthur*
- ✅ Barra de confianza y cambio de color de estados — *Arthur*
- ✅ Pantalla de análisis con pulso — *Ignacio*

## 9. Documentación
- ✅ README.md con descripción, funcionalidades, arquitectura y pasos para ejecutar — *Arthur*
- ⬜ Completar en el README: **número de equipo** y **rol de cada integrante** — *Arthur*
- ⬜ Agregar capturas de pantalla de la app al README (carpeta `docs/capturas/`) — *Ignacio*

## 10. Pruebas (lo tienen que hacer ustedes en Android Studio)
- ⬜ Abrir el proyecto, esperar la sincronización de Gradle y corregir errores si aparecen — *Arthur*
- ⬜ Ejecutar en emulador y probar todo el flujo: registro → login → nuevo levantamiento → corregir → validar — *Ignacio*
- ⬜ Probar la cámara (emulador o teléfono real) y la galería — *Ignacio*
- ⬜ Ejecutar `./gradlew test` y verificar que las pruebas pasan — *Arthur*
- ⬜ Conseguir 2 o 3 imágenes de racks **autorizadas** (sin rostros, IPs ni datos sensibles) — *Ambos*

## 11. GitHub y Trello (indicador más pesado: 20 %)
- ⬜ Crear el tablero de Trello y cargar estas tarjetas — *Arthur*
- ⬜ Dar acceso al docente al tablero de Trello — *Arthur*
- ⬜ Hacer el repositorio **público** y dar acceso al docente y al compañero — *Ignacio*
- ⬜ Subir el proyecto **por partes**, cada uno con su propio usuario de Git (ver commits sugeridos abajo) — *Ambos*
- ⬜ Mover las tarjetas en Trello a medida que se hacen los commits — *Ambos*

## 12. Entrega y defensa
- ⬜ Confirmar con el docente la **fecha límite real** de entrega — *Ambos*
- ⬜ **Una sola persona** sube el código al enlace grupal de AVA — *Arthur*
- ⬜ **Cada uno** activa su enlace individual en AVA (nombre y apellido, número de equipo y nombre de la app) — *Ambos*
- ⬜ **No hacer ningún commit después de la fecha de entrega** (ni mínimo: da 1.0 automático) — *Ambos*
- ⬜ Leer el código y poder explicar cada parte (se defiende) — *Ambos*

---

## Commits sugeridos (en este orden, repartidos entre los dos)

Hagan commits chicos, uno por tarea, y no todo el mismo día.

| # | Quién | Mensaje del commit |
|---|---|---|
| 1 | Arthur | `chore: configurar proyecto base y login inicial` |
| 2 | Ignacio | `build: agregar dependencias de navegación, ViewModel e íconos` |
| 3 | Ignacio | `feat: agregar modelos Usuario, Levantamiento, Componente, Historial y Catalogo` |
| 4 | Arthur | `feat: centralizar validaciones de formularios en Validaciones.kt` |
| 5 | Arthur | `test: agregar pruebas unitarias para las validaciones` |
| 6 | Ignacio | `feat: crear base de datos SQLite con tablas de usuarios, levantamientos, componentes e historial` |
| 7 | Arthur | `feat: agregar UsuarioRepository con login y registro` |
| 8 | Arthur | `feat: guardar contraseñas con hash SHA-256 y manejar sesión de usuario` |
| 9 | Ignacio | `feat: agregar LevantamientoRepository con CRUD de componentes e historial` |
| 10 | Ignacio | `feat: agregar reconocedor simulado de componentes del catálogo` |
| 11 | Arthur | `feat: crear componente CampoTexto con íconos y mensajes de error animados` |
| 12 | Arthur | `feat: agregar LoginViewModel y pantalla de login con validaciones` |
| 13 | Arthur | `feat: agregar pantalla y ViewModel de registro de usuarios` |
| 14 | Ignacio | `feat: agregar navegación con NavHost y transiciones animadas` |
| 15 | Ignacio | `feat: agregar pantalla principal con lista de levantamientos` |
| 16 | Ignacio | `feat: crear formulario de nuevo levantamiento con captura de cámara y galería` |
| 17 | Arthur | `feat: agregar vibración como recurso nativo ante errores de formulario` |
| 18 | Ignacio | `feat: agregar pantalla de detalle con tarjetas de componentes y barra de confianza` |
| 19 | Ignacio | `feat: agregar formulario de corrección de componentes y trazabilidad` |
| 20 | Arthur | `docs: actualizar README con arquitectura, funcionalidades y pasos de ejecución` |

> Tip: cada integrante debe configurar su usuario antes de hacer commits:
> `git config user.name "Tu Nombre"` y `git config user.email "tu@correo.com"`.
