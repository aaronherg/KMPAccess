package com.aarondevs.kmpaccess.shared

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionDenied
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.AppTrackingTransparency.ATTrackingManager
import platform.AppTrackingTransparency.ATTrackingManagerAuthorizationStatusAuthorized
import platform.AppTrackingTransparency.ATTrackingManagerAuthorizationStatusDenied
import platform.Contacts.CNAuthorizationStatusAuthorized
import platform.Contacts.CNAuthorizationStatusNotDetermined
import platform.Contacts.CNContactStore
import platform.Contacts.CNEntityType
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBManagerStatePoweredOff
import platform.CoreBluetooth.CBManagerStatePoweredOn
import platform.CoreBluetooth.CBManagerStateUnauthorized
import platform.CoreBluetooth.CBManagerStateUnsupported
import platform.CoreLocation.CLAccuracyAuthorization
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreMotion.CMMotionActivityManager
import platform.EventKit.EKAuthorizationStatusAuthorized
import platform.EventKit.EKEntityType
import platform.EventKit.EKEventStore
import platform.Foundation.NSDate
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.date
import platform.HomeKit.HMHomeManager
import platform.HomeKit.HMHomeManagerAuthorizationStatusAuthorized
import platform.HomeKit.HMHomeManagerAuthorizationStatusRestricted
import platform.Intents.INPreferences
import platform.Intents.INSiriAuthorizationStatusAuthorized
import platform.Intents.INSiriAuthorizationStatusDenied
import platform.Intents.INSiriAuthorizationStatusRestricted
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.MediaPlayer.MPMediaLibrary
import platform.MediaPlayer.MPMediaLibraryAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.NSObject
import kotlin.coroutines.resume

fun abrirConfiguracion() {
    val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)

    if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
        UIApplication.sharedApplication.openURL(settingsUrl, emptyMap<Any?,Any>(), {})
    }
}

actual fun getPlataforma(): String {
    return "IOS"
}

////////////////////////////////////////////////////////
// UBICACION
// NSLocationWhenInUseUsageDescription
// NSLocationAlwaysAndWhenInUseUsageDescription
////////////////////////////////////////////////////////

actual suspend fun verificarPermisoUbicacionPrecisa(): PermisoRespuesta {
    val status = CLLocationManager.authorizationStatus()
    val manager = CLLocationManager()
    val granted = (status == kCLAuthorizationStatusAuthorizedWhenInUse ||
            status == kCLAuthorizationStatusAuthorizedAlways) &&
            manager.accuracyAuthorization == CLAccuracyAuthorization.CLAccuracyAuthorizationFullAccuracy
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoUbicacionPrecisa(): PermisoRespuesta {
    val status = CLLocationManager.authorizationStatus()
    val manager = CLLocationManager()
    return if (status == kCLAuthorizationStatusDenied) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        manager.requestWhenInUseAuthorization()
        val granted = manager.accuracyAuthorization == CLAccuracyAuthorization.CLAccuracyAuthorizationFullAccuracy
        if (granted) PermisoRespuesta(true, "Permiso Otorgado")
        else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    }
}

actual suspend fun verificarPermisoUbicacionAproximada(): PermisoRespuesta {
    val status = CLLocationManager.authorizationStatus()
    val manager = CLLocationManager()
    val reduced = (status == kCLAuthorizationStatusAuthorizedWhenInUse ||
            status == kCLAuthorizationStatusAuthorizedAlways) &&
            manager.accuracyAuthorization == CLAccuracyAuthorization.CLAccuracyAuthorizationReducedAccuracy
    return if (reduced) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoUbicacionAproximada(): PermisoRespuesta {
    val status = CLLocationManager.authorizationStatus()
    val manager = CLLocationManager()
    return if (status == kCLAuthorizationStatusDenied) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        manager.requestWhenInUseAuthorization()
        val reduced = manager.accuracyAuthorization == CLAccuracyAuthorization.CLAccuracyAuthorizationReducedAccuracy
        if (reduced) PermisoRespuesta(true, "Permiso Otorgado")
        else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    }
}

actual suspend fun verificarPermisoUbicacionWhenInUse(): PermisoRespuesta {
    val status = CLLocationManager.authorizationStatus()
    val granted = status == kCLAuthorizationStatusAuthorizedWhenInUse
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoUbicacionWhenInUse(): PermisoRespuesta {
    val status = CLLocationManager.authorizationStatus()
    val manager = CLLocationManager()
    return if (status == kCLAuthorizationStatusDenied) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        manager.requestWhenInUseAuthorization()
        val accuracy = manager.accuracyAuthorization
        val mensaje = if (accuracy == CLAccuracyAuthorization.CLAccuracyAuthorizationFullAccuracy)
            "Permiso Otorgado"
        else "Permiso Denegado. Asigna el permiso manualmente desde configuración."
        PermisoRespuesta(true, mensaje)
    }
}

actual suspend fun verificarPermisoUbicacionSiempre(): PermisoRespuesta {
    val status = CLLocationManager.authorizationStatus()
    val granted = status == kCLAuthorizationStatusAuthorizedAlways
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoUbicacionSiempre(): PermisoRespuesta {
    val status = CLLocationManager.authorizationStatus()
    return if (status == kCLAuthorizationStatusDenied) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        CLLocationManager().requestAlwaysAuthorization()
        PermisoRespuesta(true, "Permiso Solicitado")
    }
}

actual suspend fun verificarPermisoUbicacionBackground(): PermisoRespuesta {
    val status = CLLocationManager.authorizationStatus()
    val granted = status == kCLAuthorizationStatusAuthorizedAlways
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoUbicacionBackground(): PermisoRespuesta {
    val status = CLLocationManager.authorizationStatus()
    val manager = CLLocationManager()
    return if (status == kCLAuthorizationStatusDenied) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        manager.requestAlwaysAuthorization()
        PermisoRespuesta(true, "Permiso Solicitado")
    }
}



////////////////////////////////////////////////////////
// TRACKING
// NSUserTrackingUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoTracking(): PermisoRespuesta {
    val status = ATTrackingManager.trackingAuthorizationStatus
    val granted = status == ATTrackingManagerAuthorizationStatusAuthorized
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoTracking(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val status = ATTrackingManager.trackingAuthorizationStatus
    return@withContext if (status == ATTrackingManagerAuthorizationStatusDenied) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        suspendCancellableCoroutine { cont ->
            ATTrackingManager.requestTrackingAuthorizationWithCompletionHandler { newStatus ->
                cont.resume(PermisoRespuesta(
                    newStatus == ATTrackingManagerAuthorizationStatusAuthorized,
                    if (newStatus == ATTrackingManagerAuthorizationStatusAuthorized) "Permiso Otorgado"
                    else "Permiso Denegado. Asigna el permiso manualmente desde configuración."
                ))
            }
        }
    }
}


////////////////////////////////////////////////////////
// FACE ID
// NSFaceIDUsageDescription
////////////////////////////////////////////////////////
@OptIn(ExperimentalForeignApi::class)
actual suspend fun verificarPermisoFaceID(): PermisoRespuesta {
    val context = LAContext()
    val granted = context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun solicitarPermisoFaceID(): PermisoRespuesta = withContext(Dispatchers.Main) {
    try {
        val context = LAContext()
        val canEvaluate = context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
        if (!canEvaluate) {
            abrirConfiguracion()
            PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
        } else {
            suspendCancellableCoroutine { cont ->
                context.evaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                    localizedReason = "Acceso Requerido") { success, _ ->
                    cont.resume(PermisoRespuesta(success, if (success) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
                }
            }
        }
    } catch (e: Throwable) {
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    }
}


////////////////////////////////////////////////////////
// MICROFONO
// NSMicrophoneUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoMicrofono(): PermisoRespuesta {
    val granted = AVAudioSession.sharedInstance().recordPermission() == AVAudioSessionRecordPermissionGranted
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoMicrofono(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val status = AVAudioSession.sharedInstance().recordPermission()
    if (status == AVAudioSessionRecordPermissionDenied) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        suspendCancellableCoroutine { cont ->
            AVAudioSession.sharedInstance().requestRecordPermission { granted ->
                cont.resume(PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
            }
        }
    }
}


////////////////////////////////////////////////////////
// CAMARA
// NSCameraUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoCamara(): PermisoRespuesta {
    val granted = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) == AVAuthorizationStatusAuthorized
    return if (granted) PermisoRespuesta(true, "Permiso Otorgado")
    else PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoCamara(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
    if (status == AVAuthorizationStatusDenied) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        suspendCancellableCoroutine { cont ->
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                cont.resume(PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
            }
        }
    }
}

////////////////////////////////////////////////////////
// BLUETOOTH
// NSBluetoothAlwaysUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoBluetooth(): PermisoRespuesta = withContext(Dispatchers.Main) {
    suspendCancellableCoroutine { cont ->
        val manager = CBCentralManager(null, null)

        manager.delegate = object : NSObject(), CBCentralManagerDelegateProtocol {
            override fun centralManagerDidUpdateState(manager: CBCentralManager) {
                val granted = manager.state != CBManagerStateUnauthorized
                val mensaje = if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."
                cont.resume(PermisoRespuesta(granted, mensaje))
            }
        }
    }
}

actual suspend fun solicitarPermisoBluetooth(): PermisoRespuesta =
    withContext(Dispatchers.Main) {

        suspendCancellableCoroutine { cont ->

            val manager = CBCentralManager(null, null)

            manager.delegate = object : NSObject(), CBCentralManagerDelegateProtocol {

                override fun centralManagerDidUpdateState(manager: CBCentralManager) {

                    when (manager.state) {

                        CBManagerStatePoweredOn -> {
                            cont.resume(PermisoRespuesta(true, "Permiso Otorgado"))
                        }

                        CBManagerStateUnauthorized -> {
                            abrirConfiguracion()
                            cont.resume(PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
                        }

                        CBManagerStatePoweredOff -> {
                            cont.resume(PermisoRespuesta(false, "Activa tu Bluetooth"))
                        }

                        CBManagerStateUnsupported -> {
                            cont.resume(PermisoRespuesta(false, "Bluetooth no Soportado"))
                        }

                        else -> {
                            cont.resume(PermisoRespuesta(false, "Estado Desconocido"))
                        }
                    }
                }
            }
        }
    }




actual suspend fun verificarPermisoBluetoothScan(): PermisoRespuesta = verificarPermisoBluetooth()
actual suspend fun solicitarPermisoBluetoothScan(): PermisoRespuesta = solicitarPermisoBluetooth()


////////////////////////////////////////////////////////
// ARCHIVOS / MEDIA
// NSPhotoLibraryUsageDescription
// NSPhotoLibraryAddUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoArchivos(): PermisoRespuesta {
    val status = PHPhotoLibrary.authorizationStatus()
    val granted = status == PHAuthorizationStatusAuthorized || status == PHAuthorizationStatusLimited
    return PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoArchivos(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val status = PHPhotoLibrary.authorizationStatus()
    if (status == PHAuthorizationStatusDenied) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        suspendCancellableCoroutine { cont ->
            PHPhotoLibrary.requestAuthorization { newStatus ->
                val granted = newStatus == PHAuthorizationStatusAuthorized || newStatus == PHAuthorizationStatusLimited
                cont.resume(PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
            }
        }
    }
}
actual suspend fun verificarPermisoFotos(): PermisoRespuesta = verificarPermisoArchivos()
actual suspend fun solicitarPermisoFotos(): PermisoRespuesta = solicitarPermisoArchivos()
actual suspend fun verificarPermisoVideo(): PermisoRespuesta = verificarPermisoArchivos()
actual suspend fun solicitarPermisoVideo(): PermisoRespuesta = solicitarPermisoArchivos()
actual suspend fun verificarPermisoAudio(): PermisoRespuesta = verificarPermisoArchivos()
actual suspend fun solicitarPermisoAudio(): PermisoRespuesta = solicitarPermisoArchivos()

////////////////////////////////////////////////////////
// CONTACTOS
// NSContactsUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoContactos(): PermisoRespuesta {
    val status = CNContactStore.authorizationStatusForEntityType(CNEntityType.CNEntityTypeContacts)
    val granted = status == CNAuthorizationStatusAuthorized
    return PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoContactos(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val status = CNContactStore.authorizationStatusForEntityType(CNEntityType.CNEntityTypeContacts)

    when (status) {
        CNAuthorizationStatusNotDetermined -> {
            suspendCancellableCoroutine { cont ->
                CNContactStore().requestAccessForEntityType(CNEntityType.CNEntityTypeContacts) { granted, _ ->
                    cont.resume(
                        PermisoRespuesta(
                            granted,
                            if (granted) "Permiso Otorgado"
                            else "Permiso Denegado. Asigna el permiso manualmente desde configuración."
                        )
                    )
                }
            }
        }
        CNAuthorizationStatusAuthorized -> {
            PermisoRespuesta(true, "Permiso Otorgado")
        }
        else -> {
            abrirConfiguracion()
            PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
        }
    }
}

////////////////////////////////////////////////////////
// NOTIFICACIONES
// UNUserNotificationCenter
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoNotificaciones(): PermisoRespuesta = withContext(Dispatchers.Main) {
    suspendCancellableCoroutine { cont ->
        UNUserNotificationCenter.currentNotificationCenter()
            .getNotificationSettingsWithCompletionHandler { settings ->
                val granted = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
                val mensaje = if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."
                cont.resume(PermisoRespuesta(granted, mensaje))
            }
    }
}
actual suspend fun solicitarPermisoNotificaciones(): PermisoRespuesta = withContext(Dispatchers.Main) {
    suspendCancellableCoroutine { cont ->
        UNUserNotificationCenter.currentNotificationCenter()
            .requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
            ) { granted, _ ->
                if (!granted) abrirConfiguracion()
                cont.resume(PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
            }
    }
}

////////////////////////////////////////////////////////
// CALENDARIO
// NSCalendarsUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoCalendario(): PermisoRespuesta {
    val status = EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent)
    val granted = status == EKAuthorizationStatusAuthorized
    return PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoCalendario(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val status = EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent)
    if (status != EKAuthorizationStatusAuthorized) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        suspendCancellableCoroutine { cont ->
            EKEventStore().requestAccessToEntityType(EKEntityType.EKEntityTypeEvent) { granted, _ ->
                cont.resume(PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
            }
        }
    }
}

////////////////////////////////////////////////////////
// RECORDATORIOS
// NSRemindersUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoRecordatorios(): PermisoRespuesta {
    val status = EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeReminder)
    val granted = status == EKAuthorizationStatusAuthorized
    return PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoRecordatorios(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val status = EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeReminder)
    if (status != EKAuthorizationStatusAuthorized) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        suspendCancellableCoroutine { cont ->
            EKEventStore().requestAccessToEntityType(EKEntityType.EKEntityTypeReminder) { granted, _ ->
                cont.resume(PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
            }
        }
    }
}

////////////////////////////////////////////////////////
// SIRI
// NSSiriUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoSiri(): PermisoRespuesta = withContext(Dispatchers.Main) {
    runCatching {
        val status = INPreferences.siriAuthorizationStatus()
        val granted = status == INSiriAuthorizationStatusAuthorized
        PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    }.getOrElse {
        PermisoRespuesta(false, "Siri no soportado")
    }
}

actual suspend fun solicitarPermisoSiri(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val siriDisponible = platform.Foundation.NSBundle.mainBundle.objectForInfoDictionaryKey("NSSiriUsageDescription") != null

    if (!siriDisponible) {
        return@withContext PermisoRespuesta(false, "Siri no soportado")
    }

    try {
        val status = INPreferences.siriAuthorizationStatus()

        when (status) {
            INSiriAuthorizationStatusAuthorized ->
                PermisoRespuesta(true, "Permiso Otorgado")

            INSiriAuthorizationStatusDenied, INSiriAuthorizationStatusRestricted -> {
                abrirConfiguracion()
                PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
            }

            else -> {
                suspendCancellableCoroutine { cont ->
                    INPreferences.requestSiriAuthorization { newStatus ->
                        val isOk = newStatus == INSiriAuthorizationStatusAuthorized
                        if (!isOk) abrirConfiguracion()
                        cont.resume(PermisoRespuesta(isOk, if (isOk) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
                    }
                }
            }
        }
    } catch (e: Exception) {
        PermisoRespuesta(false, "Siri no soportado")
    }
}


////////////////////////////////////////////////////////
// MOTION / FITNESS
// NSMotionUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoMovimiento(): PermisoRespuesta {
    val status: Long = CMMotionActivityManager.authorizationStatus()
    val granted = status == 3L
    return PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoMovimiento(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val status = CMMotionActivityManager.authorizationStatus()
    if (status == 2L) {
        abrirConfiguracion()
        PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    } else {
        suspendCancellableCoroutine { cont ->
            CMMotionActivityManager().queryActivityStartingFromDate(
                NSDate.date(),
                toDate = NSDate.date(),
                toQueue = NSOperationQueue.mainQueue
            ) { _, _ ->
                cont.resume(PermisoRespuesta(true, "Permiso Otorgado"))
            }
        }
    }
}

////////////////////////////////////////////////////////
// HOMEKIT
// NSHomeKitUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoHomeKit(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val manager = HMHomeManager()
    val status = manager.authorizationStatus

    val isAuthorized = (status.toLong() and HMHomeManagerAuthorizationStatusAuthorized.toLong()) != 0L

    PermisoRespuesta(
        isAuthorized, if (isAuthorized) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."
    )
}

actual suspend fun solicitarPermisoHomeKit(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val manager = HMHomeManager()
    val status = manager.authorizationStatus

    if ((status.toLong() and HMHomeManagerAuthorizationStatusAuthorized.toLong()) != 0L) {
        return@withContext PermisoRespuesta(true, "Permiso Concedido")
    }

    if (status == HMHomeManagerAuthorizationStatusRestricted) {
        abrirConfiguracion()
        return@withContext PermisoRespuesta(false, "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
    }

    suspendCancellableCoroutine { cont ->
        manager.addHomeWithName("Check") { home, _ ->
            val isAuthorized = (manager.authorizationStatus.toLong() and
                    HMHomeManagerAuthorizationStatusAuthorized.toLong()) != 0L

            if (isAuthorized) {
                home?.let { manager.removeHome(it) {} }
            } else {
                abrirConfiguracion()
            }

            cont.resume(PermisoRespuesta(isAuthorized,
                if (isAuthorized) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
        }
    }
}


////////////////////////////////////////////////////////
// MEDIA LIBRARY (Apple Music)
// NSAppleMusicUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoMediaLibrary(): PermisoRespuesta {
    NSOperationQueue.mainQueue.addOperationWithBlock { }
    val status = MPMediaLibrary.authorizationStatus()
    val granted = status == MPMediaLibraryAuthorizationStatusAuthorized
    return PermisoRespuesta(granted, if (granted) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración.")
}

actual suspend fun solicitarPermisoMediaLibrary(): PermisoRespuesta = withContext(Dispatchers.Main) {
    val status = MPMediaLibrary.authorizationStatus()
    if (status != MPMediaLibraryAuthorizationStatusAuthorized) abrirConfiguracion()
    suspendCancellableCoroutine { cont ->
        MPMediaLibrary.requestAuthorization { newStatus ->
            cont.resume(PermisoRespuesta(newStatus == MPMediaLibraryAuthorizationStatusAuthorized,
                if (newStatus == MPMediaLibraryAuthorizationStatusAuthorized) "Permiso Otorgado" else "Permiso Denegado. Asigna el permiso manualmente desde configuración."))
        }
    }
}

////////////////////////////////////////////////////////
// RED LOCAL (iOS 14+)
// NSLocalNetworkUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoRedLocal(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso esta sin desarrollar")

actual suspend fun solicitarPermisoRedLocal(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso esta sin desarrollar")



////////////////////////////////////////////////////////
// NFC
// NFCReaderUsageDescription
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoNFC(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun solicitarPermisoNFC(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")




////////////////////////////////////////////////////////
// PERMISOS SOLO ANDROID (NO APLICA EN IOS)
////////////////////////////////////////////////////////
actual suspend fun verificarPermisoTelefono(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoTelefono(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoSMS(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoSMS(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoLlamadas(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoLlamadas(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoHistorialLlamadas(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoHistorialLlamadas(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoVoicemail(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoVoicemail(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoSensores(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoSensores(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoActividadFisica(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoActividadFisica(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoAlarmasExactas(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoAlarmasExactas(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoInstalarApps(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoInstalarApps(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoOverlay(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoOverlay(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoModificarAjustes(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoModificarAjustes(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoInternet(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun solicitarPermisoInternet(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")
actual suspend fun verificarPermisoWifi(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun solicitarPermisoWifi(): PermisoRespuesta =
    PermisoRespuesta(true, "Este permiso no está disponible para este ecosistema")

actual suspend fun verificarPermisoVibracion(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

actual suspend fun solicitarPermisoVibracion(): PermisoRespuesta =
    PermisoRespuesta(true, "Permiso Otorgado")

