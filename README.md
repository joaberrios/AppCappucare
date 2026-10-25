# AppCappucare

Aplicación móvil Android para gestionar el inventario de insumos médicos.

## Descripción

AppCappucare permite registrar productos, controlar existencias y recibir alertas cuando el stock es bajo.

Proyecto académico desarrollado con Kotlin y Android Studio.

## Funcionalidades

- Registrar productos con nombre, categoría, precio y stock.
- Consultar el inventario.
- Editar y eliminar productos.
- Registrar ventas y entradas de stock.
- Validar formularios y mostrar mensajes de error.
- Tomar fotografías de productos con la cámara.
- Guardar fotografías de los productos.
- Mostrar notificaciones cuando el stock es bajo.
- Consultar un resumen del inventario.
- Navegar entre pantallas con animaciones.
- Mantener los datos guardados al cerrar la aplicación.

## Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Room Database
- Arquitectura MVVM
- Android Studio
- Git y GitHub

## Arquitectura

El proyecto utiliza MVVM para separar la interfaz de usuario, la lógica de negocio y el acceso a los datos.

- Model: representa los productos.
- View: muestra las pantallas y formularios.
- ViewModel: administra las operaciones y validaciones.
- Repository: conecta el ViewModel con Room.
- Room: almacena los productos localmente.

## Cómo ejecutar

1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Esperar la sincronización de Gradle.
4. Seleccionar un emulador Android compatible.
5. Ejecutar la aplicación.

## Recursos del dispositivo

- Cámara para fotografiar productos.
- Notificaciones para alertar sobre stock bajo.

## Repositorio

https://github.com/joaberrios/AppCappucare

## Desarrollador
- Joaquin Berrios

