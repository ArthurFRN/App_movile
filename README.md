# App_movile
MVP académico desarrollado para la asignatura DSY1105 – Desarrollo de Aplicaciones Móviles (Duoc UC · CITT, Sede San Joaquín) · Evaluación Parcial 2.

👥 Integrantes
Integrante	Rol en el proyecto
Nombre Apellido (Integrante A)	Ej.: UI, navegación y cámara
Nombre Apellido (Integrante B)	Ej.: Room, ViewModels y validaciones
Equipo N°: ___
Docente: ___
Tablero Trello: Ver planificación <!-- reemplazar por el enlace real -->
Video demo: Ver demostración <!-- reemplazar por el enlace real -->
📌 Contexto del proyecto

CMC360 Ciberseguridad Industrial SpA es una empresa chilena especializada en ciberseguridad de tecnología operacional (OT), assessments y compliance de infraestructura crítica.

Hoy, un assessment de infraestructura exige que un profesional inspeccione físicamente racks y equipos, identifique componentes, registre evidencias y organice la información de forma manual. Esto es lento, poco consistente y dificulta la trazabilidad entre la evidencia y los componentes identificados.

🎯 Solución propuesta

Una app Android que permite a un inspector:

Capturar o seleccionar una imagen autorizada de un rack de Data Center.
Registrar los antecedentes del levantamiento (ID del rack, fecha, responsable).
Visualizar los componentes reconocidos de un catálogo acotado.
Revisar, validar o corregir el resultado del reconocimiento.
Guardar un registro estructurado y trazable en una base de datos local.

⚠️ Alcance: es un MVP académico. No emite certificaciones, no reemplaza una inspección profesional ni realiza auditorías automáticas SEC-RIC (evolución futura).

✨ Funcionalidades implementadas
Módulo	Funcionalidad
🔐 Autenticación	Pantalla de login con validación de campos y usuarios persistidos en Room.
📋 Levantamientos	Menú principal con la lista de inspecciones y botón para crear una nueva.
📝 Formulario de rack	Campos con validación en tiempo real (obligatorio, largo mínimo, formato), con íconos y mensajes de error por campo.
📷 Evidencia	Captura de foto con cámara o selección desde galería, con vista previa.
🔍 Reconocimiento	Animación de escaneo y listado de componentes reconocidos en tarjetas.
✏️ Corrección	Modal para revisar o cambiar la categoría de un componente, con actualización inmediata de la lista.
💾 Persistencia	Almacenamiento local con Room (usuarios, inspecciones y componentes) con trazabilidad imagen ↔ componentes.
🎞️ Animaciones	Carga durante el escaneo, transiciones entre pantallas y apertura del modal.
📱 Recursos nativos	Cámara/Galería y segundo recurso nativo (almacenamiento de archivos o notificaciones locales).
🧭 Flujo principal de la app
Login  →  Menú de levantamientos  →  Formulario de rack  →  Captura de evidencia
                                                                      │
        Guardado en Room  ←  Detalle y corrección (modal)  ←  Reconocimiento
🏗️ Arquitectura

El proyecto sigue el patrón MVVM con Repositorio y persistencia local con Room.

Capa	Responsabilidad
UI (View)	Pantallas, componentes visuales, animaciones y modal. No contiene lógica de validación.
ViewModel	Estado de cada pantalla y validaciones de formularios, desacopladas de la interfaz.
Repository	Intermediario entre los ViewModels y los DAOs.
Data (Room)	Entities, DAOs y AppDatabase (singleton).
📁 Estructura de carpetas
app/src/main/java/cl/duoc/cmc360/
 ├─ ui/
 │   ├─ login/           LoginScreen, LoginViewModel
 │   ├─ home/            HomeScreen (lista de levantamientos)
 │   ├─ form/            RackFormScreen, RackFormViewModel
 │   ├─ capture/         CaptureScreen (cámara / galería)
 │   ├─ detail/          DetailScreen, ComponentCard, EditDialog
 │   ├─ components/      componentes reutilizables
 │   └─ theme/           colores, tipografía y estilos
 ├─ navigation/          NavGraph y rutas
 ├─ data/
 │   ├─ local/           entities/, dao/, AppDatabase
 │   └─ repository/      UserRepository, InspectionRepository
 └─ util/                permisos, manejo de archivos, notificaciones

Ajustar nombres de paquetes y carpetas a la estructura real del proyecto.

🗃️ Modelo de datos
Entidad	Campos principales
User	id, nombre, correo, contraseña (hash), rol
RackInspection	id, rackId, fecha, rutaImagen, técnico, estado
Component	id, inspectionId (FK), categoría, descripción, resultado, validado/corregido
🧩 Catálogo inicial de componentes

Switch · Patch panel · Servidor · UPS · PDU · Router · Firewall · Cableado · Rack vacío

Reemplazar por el catálogo definitivo acordado por el equipo.

🛠️ Tecnologías utilizadas
Android Studio (IDE)
Kotlin
Jetpack Compose (interfaz)
Navigation Compose (navegación)
ViewModel + StateFlow/State (gestión de estado)
Room (persistencia local)
CameraX / Activity Result API (cámara y galería)
Git + GitHub (control de versiones)
Trello (planificación)

Ajustar la lista según las librerías realmente usadas en el proyecto.

▶️ Cómo ejecutar el proyecto
Requisitos previos
Android Studio actualizado (Hedgehog o superior recomendado)
JDK 17
Emulador Android o dispositivo físico (API 26+)
Pasos
Clonar el repositorio:
bash
   git clone https://github.com/USUARIO/NOMBRE-REPOSITORIO.git
Abrir la carpeta del proyecto con Android Studio.
Esperar la sincronización de Gradle (Sync Project with Gradle Files).
Seleccionar un emulador o dispositivo Android.
Presionar Run ▶️ (Shift + F10).
Aceptar los permisos de cámara y almacenamiento cuando la app los solicite.
🔑 Credenciales de prueba
Usuario	Contraseña
usuario@demo.cl	Demo1234

Datos ficticios, solo para pruebas académicas.

📸 Capturas de pantalla
Login	Menú	Formulario
agregar imagen	agregar imagen	agregar imagen
Captura	Reconocimiento	Modal de corrección
agregar imagen	agregar imagen	agregar imagen

Guardar las imágenes en una carpeta docs/screenshots/ y enlazarlas aquí, por ejemplo: ![Login](docs/screenshots/login.png).

🔒 Seguridad y manejo de datos
Se usan solo imágenes autorizadas, de laboratorio, públicas o sintéticas.
No se capturan ni almacenan rostros, voces, datos personales, direcciones IP, credenciales, configuraciones sensibles, vulnerabilidades ni topologías reales de producción.
Los permisos (cámara y lectura de imágenes) se solicitan en tiempo de ejecución y solo se usan para el flujo de levantamiento.
No existe conexión con sistemas productivos de CMC360 ni se incluyen llaves o tokens.
🤝 Gestión del proyecto
Planificación: tablero de Trello con listas Por Hacer · En Proceso · En Revisión · Completado y tareas repartidas entre ambos integrantes.
Control de versiones: commits frecuentes y distribuidos, con mensajes técnicos siguiendo esta convención:
Prefijo	Uso	Ejemplo
feat	Nueva funcionalidad	feat(login): agrega validación de correo en LoginViewModel
fix	Corrección de error	fix(form): corrige mensaje de error en ID Rack vacío
ui	Cambios visuales	ui(detail): agrega tarjetas de componentes reconocidos
refactor	Reorganización de código	refactor(repo): mueve acceso a DAO a InspectionRepository
docs	Documentación	docs(readme): documenta pasos de ejecución
🚀 Posibles evoluciones futuras
Reconocimiento de componentes mediante un servicio de visión por computador real.
Relación con criterios normativos SEC-RIC.
Generación de reportes del levantamiento.
Integración con una base histórica de assessments.
