# App_movile
# MVP académico desarrollado para la asignatura DSY1105 – Desarrollo de Aplicaciones Móviles (Duoc UC · CITT, Sede San Joaquín) · Evaluación Parcial 2.

👥 Integrantes
Integrante	Rol en el proyecto
Nombre Apellido Arthur Coby
Nombre Apellido Ignacion Sanchez
Tema 12
Docente: Alvaro
# Contexto del proyecto

CMC360 Ciberseguridad Industrial SpA es una empresa chilena especializada en ciberseguridad de tecnología operacional (OT), assessments y compliance de infraestructura crítica.

Hoy, un assessment de infraestructura exige que un profesional inspeccione físicamente racks y equipos, identifique componentes, registre evidencias y organice la información de forma manual. Esto es lento, poco consistente y dificulta la trazabilidad entre la evidencia y los componentes identificados.

Solución propuesta

Una app Android que permite a un inspector:

Capturar o seleccionar una imagen autorizada de un rack de Data Center.
Registrar los antecedentes del levantamiento (ID del rack, fecha, responsable).
Visualizar los componentes reconocidos de un catálogo acotado.
Revisar, validar o corregir el resultado del reconocimiento.
Guardar un registro estructurado y trazable en una base de datos local.


Funcionalidades implementadas

Módulo	Funcionalidad
Autenticación	Pantalla de login con validación de campos y usuarios persistidos en Room.
Levantamientos	Menú principal con la lista de inspecciones y botón para crear una nueva.
Formulario de rack	Campos con validación en tiempo real (obligatorio, largo mínimo, formato), con íconos y mensajes de error por campo.
Evidencia	Captura de foto con cámara o selección desde galería, con vista previa.
Reconocimiento	Animación de escaneo y listado de componentes reconocidos en tarjetas.
Corrección	Modal para revisar o cambiar la categoría de un componente, con actualización inmediata de la lista.
Persistencia	Almacenamiento local con Room (usuarios, inspecciones y componentes) con trazabilidad imagen ↔ componentes.
Animaciones	Carga durante el escaneo, transiciones entre pantallas y apertura del modal.
Recursos nativos	Cámara/Galería y segundo recurso nativo (almacenamiento de archivos o notificaciones locales).
Flujo principal de la app
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

Switch · Patch panel · Servidor · UPS · PDU · Router · Firewall · Cableado · Rack vacío

Reemplazar por el catálogo definitivo acordado por el equipo.

# Tecnologías utilizadas
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



