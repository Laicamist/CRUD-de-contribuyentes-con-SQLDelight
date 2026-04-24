# CRUD-de-contribuyentes-con-SQLDelight

Este proyecto es una aplicación multiplataforma desarrollada con Kotlin Multiplatform (KMP) que simula el proceso de registro de datos 
personales ante el SAT (Servicio de Administración Tributaria). La aplicación permite gestionar información de contribuyentes de manera local, 
utilizando un enfoque de persistencia reactiva.

## Caracteristicas principales:
* Gestión de Contribuyentes: CRUD completo (Crear, Leer, Actualizar, Eliminar) para Personas Físicas y Morales.
* Persistencia Reactiva: Implementación de SQLDelight para el manejo de bases de datos con flujos de datos en tiempo real mediante Coroutines.
* Arquitectura Multiplataforma: Esta construido pensando en su uso para los sistemas android y JVM.
* Interfaz Dinámica: UI construida con Jetpack Compose / Compose Multiplatform.

## TECH STACK
* Lenguaje: Kotlin
* Framework: Kotlin Multiplatform (KMP)
* Base de Datos: SQLDelight (Generación de código Type-safe a partir de SQL).
* Concurrencia: Kotlin Coroutines & Flow.

## Requisitos previos
* Android Studio Jellyfish o superior.
* SDK de Android 34+.
* JAVA SDK 21.0.10.

## Instrucciones de uso
Para poder utilizar esta build es necesario realizar el siguiente comando de git en el directorio donde almacenaras la carpeta del proyecto
git clone --branch RELEASE https://github.com/Laicamist/CRUD-de-contribuyentes-con-SQLDelight.git
Una vez cargues el proyecto y lo buildees podrias sufrir un error de compilacion, se debe a que las fuentes que usa el proyecto cambiaron su primer caracter
pasando de ser inter a Inter, solo tienes que corregir el referenciado en el archivo type.kt para que todo funcione con normalidad
(Este caso puede ser aislado no está sujeto a que suceda en todas las ocaciones).

## Descripción de cada pantala
## Home Screen
Esta es la bienvenida a nuestro proyecto CRUD se ofrecen las opciones de seleccion de persona moral y persona fisica

<img src="mediosDigitales/Captura de pantalla 2026-04-23 043427.png" width="80%" alt="Home Screen">

## Lista de personas fisicas
Dentro de esta pantalla se cargan todas personas fisicas que fueron agregadas previamente a la base de datos, de lo contrario aparece vació, desde esta pantalla
tenemos acceso a ver detalles de una persona fisica y a agregar personas fisicas

<img src="mediosDigitales/Captura de pantalla 2026-04-23 043523.png" width="80%" alt="ListaFisicas">

## Detalles de persona fisica
En esta pantalla se despliega toda la información técnica y personal del contribuyente. Es la capa de consulta profunda donde el usuario puede verificar que el 
RFC y el Régimen Fiscal sean correctos antes de realizar cualquier movimiento.

<img src="mediosDigitales/Captura de pantalla 2026-04-23 043553.png" width="80%" alt="DetalleFisicas">

## Editar persona fisica
Esta interfaz reutiliza el formulario de registro pero carga los datos existentes desde SQLDelight. Permite corregir errores de captura 
manteniendo la integridad del ID único en la base de datos.

<img src="mediosDigitales/Captura de pantalla 2026-04-23 043637.png" width="80%" alt="EditarFisicas">

## Registro persona fisica 
Formulario de alta donde se aplican las validaciones de negocio.

<img src="mediosDigitales/Captura de pantalla 2026-04-23 043702.png" width="80%" alt="RegistroFisicas">

## Lista de personas morales
Un listado optimizado que muestra las entidades legales registradas. Utiliza LazyColumn para manejar grandes volúmenes de datos 
y se actualiza automáticamente gracias a los Flows de SQLDelight cuando se añade una nueva empresa.

<img src="mediosDigitales/Captura de pantalla 2026-04-23 043730.png" width="80%" alt="ListaMorales">

## Detalles de persona moral
Presenta la información corporativa, diferenciando claramente los campos únicos de las morales, 
como la razón social y la estructura de su RFC (12 caracteres).

<img src="mediosDigitales/Captura de pantalla 2026-04-23 043751.png" width="80%" alt="DetallesMorales">

## Editar persona moral
Pantalla dedicada a la actualización de datos empresariales. Garantiza que cualquier cambio en el régimen moral se 
refleje de inmediato en todos los módulos del sistema.

<img src="mediosDigitales/Captura de pantalla 2026-04-23 043814.png" width="80%" alt="EditarMoral">

## Registro persona moral
El punto de entrada para nuevas entidades legales. Incluye validaciones específicas para asegurar que la información 
cumpla con los estándares requeridos para la simulación fiscal.

<img src="mediosDigitales/Captura de pantalla 2026-04-23 043835.png" width="80%" alt="RegistroMoral">

# Conclusión
Este proyecto representó un desafío técnico significativo que me permitió profundizar en el ecosistema de Kotlin Multiplatform (KMP). A través de su desarrollo, logré consolidar conocimientos críticos en:
* Arquitectura de Datos: Implementación de persistencia sólida y "type-safe" con SQLDelight.
* Programación Reactiva: Gestión de estados y flujos de datos en tiempo real mediante Kotlin Coroutines y Flow.
* Navegación Multiplataforma: Control de flujo de pantallas en un entorno compartido.
* Lógica de Negocio Compartida: Optimización de código para ser ejecutado tanto en Android como en entornos JVM.
Como todo gran camino, este comienza por cosas pequeñas, el día de mañana se vendrán proyectos más robustos de los cuales
será un placer compartir con la comunidad ya sea escolar o de internet. 
###### ISAAC PEREZ ALBOR (Laicamist).
# Autor:
## ISAAC PEREZ ALBOR
## ALIAS: "LAICAMIST"
