package com.aarondevs.kmpaccess.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.aarondevs.kmpaccess.shared.*
import kmpaccess.composeapp.generated.resources.Res
import kmpaccess.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

data class PermisoUI(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val icono: DrawableResource,
    val plataforma: String,
    var respuesta: MutableState<PermisoRespuesta> = mutableStateOf(PermisoRespuesta(false, "")),
    val funcion: suspend () -> PermisoRespuesta
)

fun listaPermisos() = mutableStateListOf(

    ////////////////////////////////////////////////////////
    // AMBOS (ANDROID + IOS)
    ////////////////////////////////////////////////////////

    // UBICACIÓN
    PermisoUI(
        "ubicacion_siempre",
        "KMPAccess requiere acceso a tu ubicación en todo momento",
        "Esto permite que la app funcione correctamente incluso cuando no la estás usando.",
        Res.drawable.ico_ubicacion,
        "AMBOS"
    ) { solicitarPermisoUbicacionSiempre() },

    PermisoUI(
        "ubicacion_precisa",
        "KMPAccess requiere ubicación precisa",
        "Permite acceder a tu ubicación exacta para funciones críticas.",
        Res.drawable.ico_ubicacion,
        "AMBOS"
    ) { solicitarPermisoUbicacionPrecisa() },

    PermisoUI(
        "ubicacion_aproximada",
        "KMPAccess requiere ubicación aproximada",
        "Permite acceder a tu ubicación aproximada para funciones generales.",
        Res.drawable.ico_ubicacion,
        "AMBOS"
    ) { solicitarPermisoUbicacionAproximada() },

    PermisoUI(
        "ubicacion_background",
        "KMPAccess requiere ubicación en segundo plano",
        "Permite seguimiento continuo incluso cuando la app no está activa.",
        Res.drawable.ico_ubicacion,
        "AMBOS"
    ) { solicitarPermisoUbicacionBackground() },

    // CAMARA
    PermisoUI(
        "camara",
        "KMPAccess requiere acceso a la cámara",
        "La cámara es necesaria para capturar fotos y escanear códigos.",
        Res.drawable.ico_camara,
        "AMBOS"
    ) { solicitarPermisoCamara() },

    // MICROFONO
    PermisoUI(
        "microfono",
        "KMPAccess requiere acceso al micrófono",
        "El micrófono se utiliza para grabar audio o mensajes de voz.",
        Res.drawable.ico_microfono,
        "AMBOS"
    ) { solicitarPermisoMicrofono() },

    // BLUETOOTH
    PermisoUI(
        "bluetooth",
        "KMPAccess requiere acceso a Bluetooth",
        "Permite conectar con dispositivos cercanos como wearables o sensores.",
        Res.drawable.ico_bluetooth,
        "AMBOS"
    ) { solicitarPermisoBluetooth() },

    PermisoUI(
        "bluetooth_scan",
        "KMPAccess requiere escanear dispositivos Bluetooth",
        "Permite detectar dispositivos cercanos para conectividad.",
        Res.drawable.ico_bluetooth,
        "AMBOS"
    ) { solicitarPermisoBluetoothScan() },

    // ARCHIVOS / MEDIA
    PermisoUI(
        "archivos",
        "KMPAccess requiere acceso a tus archivos",
        "Permite seleccionar, guardar o compartir documentos e imágenes.",
        Res.drawable.ico_archivos,
        "AMBOS"
    ) { solicitarPermisoArchivos() },

    PermisoUI(
        "fotos",
        "KMPAccess requiere acceso a tus fotos",
        "Permite seleccionar y usar fotos dentro de la app.",
        Res.drawable.ico_fotos,
        "AMBOS"
    ) { solicitarPermisoFotos() },

    PermisoUI(
        "video",
        "KMPAccess requiere acceso a videos",
        "Permite grabar o reproducir videos dentro de la app.",
        Res.drawable.ico_video,
        "AMBOS"
    ) { solicitarPermisoVideo() },

    PermisoUI(
        "audio",
        "KMPAccess requiere acceso a archivos de audio",
        "Permite grabar, reproducir o compartir audios.",
        Res.drawable.ico_audio,
        "AMBOS"
    ) { solicitarPermisoAudio() },

    // CONTACTOS
    PermisoUI(
        "contactos",
        "KMPAccess requiere acceso a tus contactos",
        "Permite invitar o compartir información con tus contactos.",
        Res.drawable.ico_contactos,
        "AMBOS"
    ) { solicitarPermisoContactos() },

    // CALENDARIO
    PermisoUI(
        "calendario",
        "KMPAccess requiere acceso a tu calendario",
        "Permite crear, ver o actualizar eventos dentro de tu calendario.",
        Res.drawable.ico_calendario,
        "AMBOS"
    ) { solicitarPermisoCalendario() },

    // NOTIFICACIONES
    PermisoUI(
        "notificaciones",
        "KMPAccess quiere enviarte notificaciones",
        "Te mantiene informado sobre alertas y eventos importantes.",
        Res.drawable.ico_campana,
        "AMBOS"
    ) { solicitarPermisoNotificaciones() },

    // INTERNET
    PermisoUI(
        "internet",
        "KMPAccess requiere acceso a Internet",
        "Permite que la app funcione correctamente con conexión a la red.",
        Res.drawable.ico_internet,
        "AMBOS"
    ) { solicitarPermisoInternet() },

    // WIFI
    PermisoUI(
        "wifi",
        "KMPAccess requiere acceso a la red Wi-Fi",
        "Permite obtener información de las redes disponibles y mejorar la conectividad.",
        Res.drawable.ico_wifi,
        "AMBOS"
    ) { solicitarPermisoWifi() },

    // NFC
    PermisoUI(
        "nfc",
        "KMPAccess requiere acceso a NFC",
        "Permite leer etiquetas NFC y realizar pagos o identificaciones sin contacto.",
        Res.drawable.ico_nfc,
        "AMBOS"
    ) { solicitarPermisoNFC() },

    // VIBRACION
    PermisoUI(
        "vibracion",
        "KMPAccess requiere acceso a la vibración",
        "Permite alertas hápticas y notificaciones mediante vibración.",
        Res.drawable.ico_vibracion,
        "AMBOS"
    ) { solicitarPermisoVibracion() },

    // FACE ID / BIOMETRIA
    PermisoUI(
        "faceid",
        "KMPAccess requiere acceso a tu biometría",
        "Permite iniciar sesión usando tu huella o reconocimiento facial.",
        Res.drawable.ico_facial,
        "AMBOS"
    ) { solicitarPermisoFaceID() },

    ////////////////////////////////////////////////////////
    // SOLO IOS
    ////////////////////////////////////////////////////////

    PermisoUI(
        "ubicacion_when_in_use",
        "KMPAccess requiere acceso a tu ubicación mientras usas la app",
        "Permite funciones basadas en ubicación solo cuando la app está activa.",
        Res.drawable.ico_ubicacion,
        "IOS"
    ) { solicitarPermisoUbicacionWhenInUse() },

    PermisoUI(
        "tracking",
        "KMPAccess requiere permiso de tracking",
        "Permite recibir información para publicidad personalizada y análisis.",
        Res.drawable.ico_tracking,
        "IOS"
    ) { solicitarPermisoTracking() },

    PermisoUI(
        "recordatorios",
        "KMPAccess requiere acceso a tus recordatorios",
        "Permite crear y gestionar recordatorios dentro de la app.",
        Res.drawable.ico_recordatorio,
        "IOS"
    ) { solicitarPermisoRecordatorios() },

    PermisoUI(
        "siri",
        "KMPAccess requiere acceso a Siri",
        "Permite interactuar con la app usando comandos de voz mediante Siri.",
        Res.drawable.ico_siri,
        "IOS"
    ) { solicitarPermisoSiri() },

    PermisoUI(
        "homekit",
        "KMPAccess requiere acceso a HomeKit",
        "Permite controlar dispositivos inteligentes del hogar desde la app.",
        Res.drawable.ico_casa,
        "IOS"
    ) { solicitarPermisoHomeKit() },

    PermisoUI(
        "media_library",
        "KMPAccess requiere acceso a tu biblioteca de medios",
        "Permite acceder a música y contenido multimedia para funciones dentro de la app.",
        Res.drawable.ico_audio,
        "IOS"
    ) { solicitarPermisoMediaLibrary() },

    PermisoUI(
        "red_local",
        "KMPAccess requiere acceso a la red local",
        "Permite detectar dispositivos en la red local y ofrecer funcionalidades de conexión local.",
        Res.drawable.ico_red_local,
        "IOS"
    ) { solicitarPermisoRedLocal() },

    PermisoUI(
        "actividad_fisica",
        "KMPAccess requiere acceso a tu actividad física",
        "Permite monitorear pasos, movimiento y actividades deportivas.",
        Res.drawable.ico_fitness,
        "IOS"
    ) { solicitarPermisoMovimiento() },

    ////////////////////////////////////////////////////////
    // SOLO ANDROID
    ////////////////////////////////////////////////////////

    PermisoUI(
        "telefono",
        "KMPAccess requiere acceso a tu teléfono",
        "Permite hacer llamadas o verificar el estado del teléfono.",
        Res.drawable.ico_telefono,
        "ANDROID"
    ) { solicitarPermisoTelefono() },

    PermisoUI(
        "sms",
        "KMPAccess requiere acceso a tus SMS",
        "Permite enviar y leer mensajes de texto.",
        Res.drawable.ico_sms,
        "ANDROID"
    ) { solicitarPermisoSMS() },

    PermisoUI(
        "llamadas",
        "KMPAccess requiere acceso a llamadas",
        "Permite iniciar llamadas o acceder a registros.",
        Res.drawable.ico_telefono,
        "ANDROID"
    ) { solicitarPermisoLlamadas() },

    PermisoUI(
        "historial_llamadas",
        "KMPAccess requiere acceder al historial de llamadas",
        "Permite consultar tu registro de llamadas.",
        Res.drawable.ico_telefono,
        "ANDROID"
    ) { solicitarPermisoHistorialLlamadas() },

    PermisoUI(
        "voicemail",
        "KMPAccess requiere acceso al buzón de voz",
        "Permite leer mensajes de voz.",
        Res.drawable.ico_voicemail,
        "ANDROID"
    ) { solicitarPermisoVoicemail() },

    PermisoUI(
        "sensores",
        "KMPAccess requiere acceso a sensores del dispositivo",
        "Permite usar giroscopio, acelerómetro, etc.",
        Res.drawable.ico_sensores,
        "ANDROID"
    ) { solicitarPermisoSensores() },

    PermisoUI(
        "actividad_fisica_android",
        "KMPAccess requiere acceso a tu actividad física",
        "Permite monitorear pasos y movimiento.",
        Res.drawable.ico_fitness,
        "ANDROID"
    ) { solicitarPermisoActividadFisica() },

    PermisoUI(
        "alarmas_exactas",
        "KMPAccess requiere configurar alarmas exactas",
        "Permite temporizadores precisos.",
        Res.drawable.ico_alarma,
        "ANDROID"
    ) { solicitarPermisoAlarmasExactas() },

    PermisoUI(
        "instalar_apps",
        "KMPAccess requiere permiso para instalar apps",
        "Permite instalar desde fuentes externas.",
        Res.drawable.ico_instalar,
        "ANDROID"
    ) { solicitarPermisoInstalarApps() },

    PermisoUI(
        "overlay",
        "KMPAccess requiere permiso para superponerse",
        "Permite mostrar elementos flotantes.",
        Res.drawable.ico_overlay,
        "ANDROID"
    ) { solicitarPermisoOverlay() },

    PermisoUI(
        "modificar_ajustes",
        "KMPAccess requiere modificar ajustes del sistema",
        "Permite cambiar configuraciones globales.",
        Res.drawable.ico_modificar_ajustes,
        "ANDROID"
    ) { solicitarPermisoModificarAjustes() }

)