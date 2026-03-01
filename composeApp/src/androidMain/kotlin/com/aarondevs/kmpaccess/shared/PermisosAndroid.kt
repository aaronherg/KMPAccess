package com.aarondevs.kmpaccess.shared

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import androidx.core.net.toUri

private const val RC_LOCATION = 1001
private const val RC_MIC = 1002
private const val RC_BT = 1003
private const val RC_CAMERA = 1004
private const val RC_STORAGE = 1005
private const val RC_CONTACTS = 1006
private const val RC_PHONE = 1007
private const val RC_SMS = 1008
private const val RC_NOTIFICATIONS = 1009
private const val RC_SENSORS = 1010
private const val RC_ACTIVITY = 1011
private const val RC_BT_SCAN = 1012
private const val RC_MEDIA_VIDEO = 1013
private const val RC_MEDIA_AUDIO = 1014
private const val RC_CALENDAR = 1015

// AGREGA EN EL MainActivity ESTO DENTRO DEL onCreate:
// ActivityProvider.currentActivity = this
@SuppressLint("StaticFieldLeak")
object ActivityProvider {
    var currentActivity: Activity? = null
}

private fun getActivity(): Activity {
    return ActivityProvider.currentActivity
        ?: throw IllegalStateException("Activity no disponible")
}

private fun abrirConfiguracion(act: Activity) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", act.packageName, null)
    )
    act.startActivity(intent)
}

private suspend fun requestPermissionIfNeeded(
    permiso: String,
    requestCode: Int
): Boolean {

    val act = getActivity()

    if (ContextCompat.checkSelfPermission(act, permiso)
        == PackageManager.PERMISSION_GRANTED
    ) return true

    val componentActivity = act as? ComponentActivity
        ?: throw IllegalStateException("Activity debe ser ComponentActivity")

    val prefs = act.getSharedPreferences("permission_state", Activity.MODE_PRIVATE)
    val wasDeniedBefore = prefs.getBoolean(permiso, false)

    if (wasDeniedBefore) {
        abrirConfiguracion(act)
        return false
    }

    return suspendCancellableCoroutine { cont ->

        val launcher = componentActivity.activityResultRegistry.register(
            "perm_$requestCode",
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                if (cont.isActive) cont.resume(true)
                return@register
            }

            val shouldShow =
                ActivityCompat.shouldShowRequestPermissionRationale(act, permiso)

            if (shouldShow) {
                prefs.edit { putBoolean(permiso, true) }
            }

            if (cont.isActive) cont.resume(false)
        }

        launcher.launch(permiso)

        cont.invokeOnCancellation {
            launcher.unregister()
        }
    }
}


actual fun getPlataforma(): String {
    return "ANDROID"
}


////////////////////////////////////////////////////////
// UBICACIÓN
// Permite obtener la ubicación del usuario
// <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
// <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
// <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoUbicacionPrecisa(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(
        getActivity(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoUbicacionPrecisa(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.ACCESS_FINE_LOCATION, RC_LOCATION)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoUbicacionAproximada(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(
        getActivity(),
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoUbicacionAproximada(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.ACCESS_COARSE_LOCATION, RC_LOCATION)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoUbicacionBackground(): PermisoRespuesta {
    val granted = if (Build.VERSION.SDK_INT >= 29)
        ContextCompat.checkSelfPermission(
            getActivity(),
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    else true

    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoUbicacionBackground(): PermisoRespuesta {
    val granted = if (Build.VERSION.SDK_INT >= 29)
        requestPermissionIfNeeded(Manifest.permission.ACCESS_BACKGROUND_LOCATION, RC_LOCATION)
    else true

    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoUbicacionSiempre(): PermisoRespuesta {
    val fineLocationGranted = ContextCompat.checkSelfPermission(
        getActivity(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val backgroundGranted = if (Build.VERSION.SDK_INT >= 29)
        ContextCompat.checkSelfPermission(
            getActivity(),
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    else true

    return if (fineLocationGranted && backgroundGranted)
        PermisoRespuesta(true, "Permiso Otorgado")
    else
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoUbicacionSiempre(): PermisoRespuesta {
    val fineGranted = requestPermissionIfNeeded(Manifest.permission.ACCESS_FINE_LOCATION, RC_LOCATION)

    if (!fineGranted) return PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")

    val backgroundGranted = if (Build.VERSION.SDK_INT >= 29)
        requestPermissionIfNeeded(Manifest.permission.ACCESS_BACKGROUND_LOCATION, RC_LOCATION)
    else true

    return if (backgroundGranted)
        PermisoRespuesta(true, "Permiso Otorgado")
    else
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}


////////////////////////////////////////////////////////
// MICROFONO
// Permite grabar audio
// <uses-permission android:name="android.permission.RECORD_AUDIO"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoMicrofono(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoMicrofono(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.RECORD_AUDIO, RC_MIC)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}


////////////////////////////////////////////////////////
// CAMARA
// Permite tomar fotos y videos
// <uses-permission android:name="android.permission.CAMERA"/>
// <uses-feature android:name="android.hardware.camera" android:required="false"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoCamara(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoCamara(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.CAMERA, RC_CAMERA)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}


////////////////////////////////////////////////////////
// BLUETOOTH
// Permite conexión y escaneo de dispositivos Bluetooth
// <uses-permission android:name="android.permission.BLUETOOTH_CONNECT"/>
// <uses-permission android:name="android.permission.BLUETOOTH_SCAN"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoBluetooth(): PermisoRespuesta {
    val permiso = if (Build.VERSION.SDK_INT >= 31)
        Manifest.permission.BLUETOOTH_CONNECT
    else Manifest.permission.BLUETOOTH

    val granted = ContextCompat.checkSelfPermission(getActivity(), permiso) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoBluetooth(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.BLUETOOTH_CONNECT, RC_BT)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoBluetoothScan(): PermisoRespuesta {
    val permiso = if (Build.VERSION.SDK_INT >= 31)
        Manifest.permission.BLUETOOTH_SCAN
    else
        Manifest.permission.BLUETOOTH

    val granted = ContextCompat.checkSelfPermission(getActivity(), permiso) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoBluetoothScan(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.BLUETOOTH_SCAN, RC_BT_SCAN)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}


////////////////////////////////////////////////////////
// ARCHIVOS / MEDIA
// Permite acceder a imágenes, videos y audio
// <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
// <uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
// <uses-permission android:name="android.permission.READ_MEDIA_VIDEO"/>
// <uses-permission android:name="android.permission.READ_MEDIA_AUDIO"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoArchivos(): PermisoRespuesta {
    val permiso = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_IMAGES
    else Manifest.permission.READ_EXTERNAL_STORAGE

    val granted = ContextCompat.checkSelfPermission(getActivity(), permiso) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoArchivos(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(
        if (Build.VERSION.SDK_INT >= 33)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE,
        RC_STORAGE
    )
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoVideo(): PermisoRespuesta {
    val permiso = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_VIDEO
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    val granted = ContextCompat.checkSelfPermission(getActivity(), permiso) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoVideo(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.READ_MEDIA_VIDEO, RC_MEDIA_VIDEO)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoAudio(): PermisoRespuesta {
    val permiso = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_AUDIO
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    val granted = ContextCompat.checkSelfPermission(getActivity(), permiso) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoAudio(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.READ_MEDIA_AUDIO, RC_MEDIA_AUDIO)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoFotos(): PermisoRespuesta {
    val permiso = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_IMAGES
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    val granted = ContextCompat.checkSelfPermission(getActivity(), permiso) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoFotos(): PermisoRespuesta {
    val permiso = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_IMAGES
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    val granted = requestPermissionIfNeeded(permiso, RC_STORAGE)
    return if (granted) PermisoRespuesta(true, "Permiso Solicitado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}


////////////////////////////////////////////////////////
// CONTACTOS
// Permite acceder a la lista de contactos
// <uses-permission android:name="android.permission.READ_CONTACTS"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoContactos(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoContactos(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.READ_CONTACTS, RC_CONTACTS)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

////////////////////////////////////////////////////////
// TELEFONO / SMS
// Permite acceder al estado del teléfono, llamadas y SMS
// <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
// <uses-permission android:name="android.permission.READ_SMS"/>
// <uses-permission android:name="android.permission.CALL_PHONE"/>
// <uses-permission android:name="android.permission.READ_CALL_LOG"/>
// <uses-permission android:name="android.permission.READ_VOICEMAIL"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoTelefono(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoTelefono(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.READ_PHONE_STATE, RC_PHONE)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoSMS(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoSMS(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.READ_SMS, RC_SMS)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoLlamadas(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoLlamadas(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.CALL_PHONE, RC_PHONE)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoHistorialLlamadas(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoHistorialLlamadas(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.READ_CALL_LOG, RC_PHONE)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoVoicemail(): PermisoRespuesta {
    val tm = getActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val soporteVoicemail = tm.phoneType != TelephonyManager.PHONE_TYPE_NONE

    if (!soporteVoicemail) {
        return PermisoRespuesta(false, "Este permiso no está disponible en este dispositivo")
    }

    val granted = if (Build.VERSION.SDK_INT >= 26)
        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_VOICEMAIL) == PackageManager.PERMISSION_GRANTED
    else true

    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoVoicemail(): PermisoRespuesta {
    val tm = getActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val soporteVoicemail = tm.phoneType != TelephonyManager.PHONE_TYPE_NONE

    if (!soporteVoicemail) {
        return PermisoRespuesta(false, "Este dispositivo no tiene soporte con voicemails")
    }

    return if (Build.VERSION.SDK_INT >= 26) {
        val granted = requestPermissionIfNeeded(Manifest.permission.READ_VOICEMAIL, RC_PHONE)
        if (granted) PermisoRespuesta(true, "Permiso Otorgado")
        else PermisoRespuesta(false, "Este dispositivo no tiene soporte con voicemails")
    } else {
        PermisoRespuesta(true, "Permiso Otorgado")
    }
}



////////////////////////////////////////////////////////
// NOTIFICACIONES
// Permite enviar notificaciones (Android 13+)
// <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoNotificaciones(): PermisoRespuesta {
    val granted = if (Build.VERSION.SDK_INT >= 33)
        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    else true

    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoNotificaciones(): PermisoRespuesta {
    val granted = if (Build.VERSION.SDK_INT >= 33)
        requestPermissionIfNeeded(Manifest.permission.POST_NOTIFICATIONS, RC_NOTIFICATIONS)
    else true

    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

////////////////////////////////////////////////////////
// CALENDARIO
// Permite leer y escribir eventos en el calendario
// <uses-permission android:name="android.permission.READ_CALENDAR"/>
// <uses-permission android:name="android.permission.WRITE_CALENDAR"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoCalendario(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoCalendario(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.READ_CALENDAR, RC_CALENDAR)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

////////////////////////////////////////////////////////
// SENSORES / ACTIVIDAD FISICA
// Permite acceder a sensores biométricos y actividad física
// <uses-permission android:name="android.permission.BODY_SENSORS"/>
// <uses-permission android:name="android.permission.ACTIVITY_RECOGNITION"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoSensores(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(
        getActivity(),
        Manifest.permission.BODY_SENSORS
    ) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoSensores(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.BODY_SENSORS, RC_SENSORS)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun verificarPermisoActividadFisica(): PermisoRespuesta {
    val granted = ContextCompat.checkSelfPermission(
        getActivity(),
        Manifest.permission.ACTIVITY_RECOGNITION
    ) == PackageManager.PERMISSION_GRANTED
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoActividadFisica(): PermisoRespuesta {
    val granted = requestPermissionIfNeeded(Manifest.permission.ACTIVITY_RECOGNITION, RC_ACTIVITY)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}


////////////////////////////////////////////////////////
// PERMISOS ESPECIALES
// Alarmas exactas, Overlay, Modificar ajustes, Instalar apps
// <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
// <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
// <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
// <uses-permission android:name="android.permission.WRITE_SETTINGS"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoAlarmasExactas(): PermisoRespuesta {
    if (Build.VERSION.SDK_INT >= 31) {
        val act = getActivity()
        val alarmManager = act.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        return if (alarmManager.canScheduleExactAlarms())
            PermisoRespuesta(true, "Permiso Otorgado")
        else
            PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    }
    return PermisoRespuesta(true, "Permiso Otorgado")
}

actual suspend fun solicitarPermisoAlarmasExactas(): PermisoRespuesta {
    if (Build.VERSION.SDK_INT >= 31) {
        val act = getActivity()
        val alarmManager = act.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            act.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            return PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
        }
    }
    return PermisoRespuesta(true, "Permiso Otorgado")
}

actual suspend fun verificarPermisoInstalarApps(): PermisoRespuesta {
    val act = getActivity()
    return if (Build.VERSION.SDK_INT >= 26) {
        if (act.packageManager.canRequestPackageInstalls())
            PermisoRespuesta(true, "Permiso Otorgado")
        else
            PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else PermisoRespuesta(true, "Permiso Otorgado")
}

actual suspend fun solicitarPermisoInstalarApps(): PermisoRespuesta {
    val act = getActivity()
    if (Build.VERSION.SDK_INT >= 26 && !act.packageManager.canRequestPackageInstalls()) {
        act.startActivity(
            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                .setData("package:${act.packageName}".toUri())
        )
        return PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    }
    return PermisoRespuesta(true, "Permiso Otorgado")
}

actual suspend fun verificarPermisoOverlay(): PermisoRespuesta {
    return if (Settings.canDrawOverlays(getActivity()))
        PermisoRespuesta(true, "Permiso Otorgado")
    else
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoOverlay(): PermisoRespuesta {
    val act = getActivity()
    if (!Settings.canDrawOverlays(act)) {
        act.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        return PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    }
    return PermisoRespuesta(true, "Permiso Otorgado")
}

actual suspend fun verificarPermisoModificarAjustes(): PermisoRespuesta {
    return if (Settings.System.canWrite(getActivity()))
        PermisoRespuesta(true, "Permiso Otorgado")
    else
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoModificarAjustes(): PermisoRespuesta {
    val act = getActivity()
    if (!Settings.System.canWrite(act)) {
        act.startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS))
        return PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    }
    return PermisoRespuesta(true, "Permiso Otorgado")
}




////////////////////////////////////////////////////////
// Reconocimiento Biometrico
// Reconocimiento biometrico como faceId, huella dactilar, etc.
// <uses-permission android:name="android.permission.USE_BIOMETRIC" />
// <uses-permission android:name="android.permission.USE_FINGERPRINT" />
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoFaceID(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun solicitarPermisoFaceID(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")



////////////////////////////////////////////////////////
// INTERNET / RED / NFC / VIBRACION
// <uses-permission android:name="android.permission.INTERNET"/>
// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
// <uses-permission android:name="android.permission.NFC"/>
// <uses-permission android:name="android.permission.VIBRATE"/>
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoInternet(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun solicitarPermisoInternet(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun verificarPermisoWifi(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun solicitarPermisoWifi(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun verificarPermisoNFC(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun solicitarPermisoNFC(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun verificarPermisoVibracion(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun solicitarPermisoVibracion(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")



////////////////////////////////////////////////////////
// IOS (SOLO PERMISOS PARA IOS)
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoUbicacionWhenInUse(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoUbicacionWhenInUse(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoTracking(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoTracking(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoRecordatorios(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoRecordatorios(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoSiri(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoSiri(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoMovimiento(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoMovimiento(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoHomeKit(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoHomeKit(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoMediaLibrary(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoMediaLibrary(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoRedLocal(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoRedLocal(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")