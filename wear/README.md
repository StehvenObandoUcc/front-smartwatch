# Reloj Wear OS

App **independiente** (standalone) en Kotlin + Compose for Wear OS (Material 3), Room, AlarmManager y Hilt.

## Fase 0: prototipo de alarma

- Al abrir la app por primera vez se crean **2 dosis falsas** en Room (dentro de 1 y 2 minutos) y se programan con `AlarmManager.setAlarmClock()`.
- Al vencer, `DoseAlarmReceiver` publica una notificación de alarma (sonido de alarma insistente, vibración, full-screen intent) con las acciones **Tomada** y **Posponer 10 min** (máximo 3 veces). La pantalla de alerta también ofrece **Omitir** con confirmación.
- Cada acción se guarda en `dose_events` con `eventId` UUID y estado `PENDING` (se subirá al backend en la fase 3).
- `BootReceiver` reprograma las dosis pendientes tras reiniciar, actualizar la app o conceder el permiso de alarmas exactas.
- Inicio muestra los botones para conceder **alarmas exactas** y **notificaciones** si faltan, y "Reiniciar prueba" para volver a crear las 2 dosis.

## Comandos

```bash
./gradlew ktlintCheck detekt test   # obligatorio antes de cada push
./gradlew ktlintFormat              # corrige el formato
./gradlew assembleDebug             # APK en app/build/outputs/apk/debug/
```

Requiere JDK 17+ y el SDK de Android (API 37). Crea `local.properties` con `sdk.dir=...` (no se versiona).

## Probar la alarma con el reloj en reposo (emulador)

1. Crea un AVD **Wear OS** (imagen `system-images;android-36;android-wear;x86_64` o superior) e instala: `./gradlew installDebug`.
2. Abre la app, concede notificaciones y alarmas exactas desde Inicio y pulsa "Reiniciar prueba".
3. Pon el reloj en reposo: `adb shell input keyevent KEYCODE_SLEEP`, y fuerza Doze si quieres: `adb shell dumpsys deviceidle force-idle`.
4. Espera 1 minuto: debe encenderse la pantalla, sonar/vibrar y mostrar la alerta con Tomada / Posponer.
5. Reinicio: `adb reboot` antes de que venza la segunda dosis; tras arrancar debe seguir sonando a su hora.
6. Comprueba las alarmas programadas con `adb shell dumpsys alarm | grep recordatorios`.

## Estructura

```
app/src/main/java/com/smartwatch/recordatorios/
├── alarm/   DoseAlarmScheduler, DoseNotifier, DoseActionHandler, receivers (alarma, acciones, arranque)
├── data/    local/ (Room: DoseEntity, DoseEventEntity, DAOs) · repository/ (DoseRepository, DemoDoses)
├── di/      AppModule (Room, AlarmManager, Clock, scope)
└── ui/      theme/ (tokens) · components/ · screens/ (home, alert)
```
