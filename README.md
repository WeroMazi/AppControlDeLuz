No se espanten por ver un monton de archivos, android studio los genera por si solos, asi que dejo los mas importantes que use para la app y no se pierdan con todo lo que lleva 
Archivos Importantes

MainActivity.kt
app/src/main/java/com/example/appcontroldeluz/MainActivity.kt
Punto de entrada de la aplicacion que gestiona el sistema de navegacion y la verificacion de permisos de ubicacion y Bluetooth.

DashboardScreen.kt
app/src/main/java/com/example/appcontroldeluz/ui/screens/DashboardScreen.kt
Pantalla principal que muestra el estado de las habitaciones y permite el control maestro de todas las luces con persistencia de estado.

CacheManager.kt
app/src/main/java/com/example/appcontroldeluz/data/CacheManager.kt
Gestiona el almacenamiento persistente de la sesion del usuario y el estado de los dispositivos mediante DataStore.

AndroidManifest.xml
app/src/main/AndroidManifest.xml
Configuracion de permisos criticos para el funcionamiento de Bluetooth y servicios de ubicacion aproximada y precisa.

libs.versions.toml
gradle/libs.versions.toml
Archivo de gestion de dependencias que contiene las versiones actualizadas de Kotlin y Jetpack Compose.

 Implementacion del Backend

Para modificar la logica de comunicacion con el hardware o servicios externos, los archivos se encuentran en el paquete:
`app/src/main/java/com/example/appcontroldeluz/backend/`

- BluetoothController.kt: Gestiona la conexion y envio de datos a dispositivos mediante Bluetooth (ej. Arduino).
- WifiController.kt: Implementa la logica de control para dispositivos conectados a la red local.
- VoiceController.kt: Contiene el esqueleto para integrar el reconocimiento de voz y procesamiento de comandos.

Tecnologias Utilizadas
- Kotlin 2.0.21
- Jetpack Compose
- DataStore Preferences
- Material Design 3
- Room Database
