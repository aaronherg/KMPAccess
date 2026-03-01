package com.aarondevs.kmpaccess.shared

data class PermisoRespuesta(
    val estado: Boolean,
    val mensaje: String
)

expect fun getPlataforma(): String

////////////////////////////////////////////////////////
// COMUN (ANDROID + IOS)
////////////////////////////////////////////////////////

// UBICACIÓN
expect suspend fun verificarPermisoUbicacionPrecisa(): PermisoRespuesta
expect suspend fun solicitarPermisoUbicacionPrecisa(): PermisoRespuesta
expect suspend fun verificarPermisoUbicacionAproximada(): PermisoRespuesta
expect suspend fun solicitarPermisoUbicacionAproximada(): PermisoRespuesta
expect suspend fun verificarPermisoUbicacionBackground(): PermisoRespuesta
expect suspend fun solicitarPermisoUbicacionBackground(): PermisoRespuesta
expect suspend fun verificarPermisoUbicacionSiempre(): PermisoRespuesta
expect suspend fun solicitarPermisoUbicacionSiempre(): PermisoRespuesta

// CAMARA
expect suspend fun verificarPermisoCamara(): PermisoRespuesta
expect suspend fun solicitarPermisoCamara(): PermisoRespuesta

// MICROFONO
expect suspend fun verificarPermisoMicrofono(): PermisoRespuesta
expect suspend fun solicitarPermisoMicrofono(): PermisoRespuesta

// BLUETOOTH
expect suspend fun verificarPermisoBluetooth(): PermisoRespuesta
expect suspend fun solicitarPermisoBluetooth(): PermisoRespuesta

expect suspend fun verificarPermisoBluetoothScan(): PermisoRespuesta
expect suspend fun solicitarPermisoBluetoothScan(): PermisoRespuesta

// ARCHIVOS / MEDIA
expect suspend fun verificarPermisoArchivos(): PermisoRespuesta
expect suspend fun solicitarPermisoArchivos(): PermisoRespuesta

expect suspend fun verificarPermisoVideo(): PermisoRespuesta
expect suspend fun solicitarPermisoVideo(): PermisoRespuesta

expect suspend fun verificarPermisoAudio(): PermisoRespuesta
expect suspend fun solicitarPermisoAudio(): PermisoRespuesta

expect suspend fun verificarPermisoFotos(): PermisoRespuesta
expect suspend fun solicitarPermisoFotos(): PermisoRespuesta

// CONTACTOS
expect suspend fun verificarPermisoContactos(): PermisoRespuesta
expect suspend fun solicitarPermisoContactos(): PermisoRespuesta

// NOTIFICACIONES
expect suspend fun verificarPermisoNotificaciones(): PermisoRespuesta
expect suspend fun solicitarPermisoNotificaciones(): PermisoRespuesta

// CALENDARIO
expect suspend fun verificarPermisoCalendario(): PermisoRespuesta
expect suspend fun solicitarPermisoCalendario(): PermisoRespuesta

// INTERNET
expect suspend fun verificarPermisoInternet(): PermisoRespuesta
expect suspend fun solicitarPermisoInternet(): PermisoRespuesta

// RED
expect suspend fun verificarPermisoWifi(): PermisoRespuesta
expect suspend fun solicitarPermisoWifi(): PermisoRespuesta

// NFC
expect suspend fun verificarPermisoNFC(): PermisoRespuesta
expect suspend fun solicitarPermisoNFC(): PermisoRespuesta

// VIBRACION
expect suspend fun verificarPermisoVibracion(): PermisoRespuesta
expect suspend fun solicitarPermisoVibracion(): PermisoRespuesta

// FACE ID
expect suspend fun verificarPermisoFaceID(): PermisoRespuesta
expect suspend fun solicitarPermisoFaceID(): PermisoRespuesta






////////////////////////////////////////////////////////
// SOLO ANDROID
////////////////////////////////////////////////////////

// TELEFONO
expect suspend fun verificarPermisoTelefono(): PermisoRespuesta
expect suspend fun solicitarPermisoTelefono(): PermisoRespuesta

// SMS
expect suspend fun verificarPermisoSMS(): PermisoRespuesta
expect suspend fun solicitarPermisoSMS(): PermisoRespuesta

// LLAMADAS
expect suspend fun verificarPermisoLlamadas(): PermisoRespuesta
expect suspend fun solicitarPermisoLlamadas(): PermisoRespuesta

// REGISTRO DE LLAMADAS
expect suspend fun verificarPermisoHistorialLlamadas(): PermisoRespuesta
expect suspend fun solicitarPermisoHistorialLlamadas(): PermisoRespuesta

// VOICEMAIL
expect suspend fun verificarPermisoVoicemail(): PermisoRespuesta
expect suspend fun solicitarPermisoVoicemail(): PermisoRespuesta

// SENSORES
expect suspend fun verificarPermisoSensores(): PermisoRespuesta
expect suspend fun solicitarPermisoSensores(): PermisoRespuesta

// ACTIVIDAD FISICA
expect suspend fun verificarPermisoActividadFisica(): PermisoRespuesta
expect suspend fun solicitarPermisoActividadFisica(): PermisoRespuesta

// ALARMAS EXACTAS (Android 12+)
expect suspend fun verificarPermisoAlarmasExactas(): PermisoRespuesta
expect suspend fun solicitarPermisoAlarmasExactas(): PermisoRespuesta

// INSTALAR APPS (fuentes desconocidas)
expect suspend fun verificarPermisoInstalarApps(): PermisoRespuesta
expect suspend fun solicitarPermisoInstalarApps(): PermisoRespuesta

// OVERLAY (dibujar sobre otras apps)
expect suspend fun verificarPermisoOverlay(): PermisoRespuesta
expect suspend fun solicitarPermisoOverlay(): PermisoRespuesta

// WRITE SETTINGS
expect suspend fun verificarPermisoModificarAjustes(): PermisoRespuesta
expect suspend fun solicitarPermisoModificarAjustes(): PermisoRespuesta









////////////////////////////////////////////////////////
// SOLO IOS
////////////////////////////////////////////////////////

// UBICACION WHEN IN USE
expect suspend fun verificarPermisoUbicacionWhenInUse(): PermisoRespuesta
expect suspend fun solicitarPermisoUbicacionWhenInUse(): PermisoRespuesta

// TRACKING (AppTrackingTransparency)
expect suspend fun verificarPermisoTracking(): PermisoRespuesta
expect suspend fun solicitarPermisoTracking(): PermisoRespuesta

// RECORDATORIOS
expect suspend fun verificarPermisoRecordatorios(): PermisoRespuesta
expect suspend fun solicitarPermisoRecordatorios(): PermisoRespuesta

// SIRI
expect suspend fun verificarPermisoSiri(): PermisoRespuesta
expect suspend fun solicitarPermisoSiri(): PermisoRespuesta

// MOTION / FITNESS
expect suspend fun verificarPermisoMovimiento(): PermisoRespuesta
expect suspend fun solicitarPermisoMovimiento(): PermisoRespuesta

// HOMEKIT
expect suspend fun verificarPermisoHomeKit(): PermisoRespuesta
expect suspend fun solicitarPermisoHomeKit(): PermisoRespuesta

// MEDIA LIBRARY (Apple Music)
expect suspend fun verificarPermisoMediaLibrary(): PermisoRespuesta
expect suspend fun solicitarPermisoMediaLibrary(): PermisoRespuesta


// LOCAL NETWORK (iOS 14+)
expect suspend fun verificarPermisoRedLocal(): PermisoRespuesta
expect suspend fun solicitarPermisoRedLocal(): PermisoRespuesta