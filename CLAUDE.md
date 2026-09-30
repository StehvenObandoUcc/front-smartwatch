# CLAUDE.md — Agente Frontend

Eres el Agente Frontend del proyecto de recordatorios de medicamentos. **No hay app de teléfono.** Construyes dos clientes en el repo `front-smartwatch`:
- `wear/`: app **Wear OS independiente** en Kotlin (el producto principal).
- `web/`: **panel web** responsive donde el paciente o el cuidador carga medicamentos y ve reportes.

Trabajas en paralelo con el Agente Backend (repo `back-smartwatch`). El plan completo está en `docs/plan-desarrollo-app.md`.

## Tu territorio
- Escribes solo en este repo (`front-smartwatch`).
- **No editas el contrato** (`../back-smartwatch/contracts/openapi.yaml`). Si necesitas un cambio, pídelo al Agente Backend y trabaja con un mock mientras tanto.

## Contrato y datos
- Los clientes de API se **generan** desde el contrato: openapi-generator (Kotlin + kotlinx.serialization) para `wear/`, Orval (TanStack Query + mocks MSW) para `web/`. Nunca escribas a mano llamadas ni tipos que dupliquen el contrato.
- En local se lee desde `../back-smartwatch/contracts/openapi.yaml`; en CI se descarga del repo del backend fijando un commit. CI falla si regenerar produce diferencias.

## Reloj (`wear/`)

### Stack
Kotlin, Compose for Wear OS (Material 3), Horologist, Room, AlarmManager, NotificationManager, WorkManager, DataStore + Android Keystore, Retrofit/OkHttp o Ktor, Firebase Cloud Messaging, Tiles y Complications, Hilt, Coroutines/Flow. Calidad: ktlint, detekt, JUnit, Turbine, Robolectric, Compose UI tests.

### Reglas
- App **standalone**: `com.google.android.wearable.standalone = true` en el manifiesto. No se usa Data Layer.
- **Funciona sin conexión**: el plan de 7 días vive en Room y las alarmas son locales. Internet solo para descargar el plan y subir eventos.
- Alarmas con `setAlarmClock()`; comprobar `canScheduleExactAlarms()` y guiar al usuario al permiso. `BootReceiver` reprograma todo tras reiniciar.
- Alerta = notificación con acciones Tomada / Posponer (Omitir con confirmación). Posponer 10 min, máximo 3 veces.
- Cada acción se guarda en Room con `eventId` UUID y estado `PENDING`; `EventUploadWorker` (WorkManager, restricción de red, backoff) la sube en lote y la marca `CONFIRMED`.
- Plan nuevo: push FCM de datos → `PlanSyncWorker` descarga `GET /devices/me/plan` con `ETag` y reprograma alarmas. Sincronización periódica de respaldo espaciada (batería).
- Vinculación sin teclado: el reloj muestra un código corto y consulta `POST /devices/token` hasta que el usuario lo confirma en la web. Tokens solo en Keystore.
- `scheduledAt` en epoch UTC; se formatea con la zona del reloj.
- Chat por voz: `RecognizerIntent` → backend → respuesta corta en pantalla y leída con `TextToSpeech`.
- UI: texto grande, botones grandes, compatible con rotary input, pantallas redondas y cuadradas; el color del medicamento siempre con su nombre.
- Estructura: `ui/` (theme, components atómicos, screens), `data/` (local, remote, repository), `sync/`, `alarm/`, `tile/`, `complication/`, `di/`.

## Panel web (`web/`)

### Stack
React + Vite + TypeScript strict, React Router, Tailwind CSS con tokens propios, TanStack Query + Orval, React Hook Form + Zod, i18next, Storybook, Vitest + Testing Library, Playwright, ESLint + Prettier.

### Diseño atómico
```
src/design/tokens        color, tipografía, espaciado, radios, sombras, breakpoints
src/components/atoms     sin conocimiento del dominio
src/components/molecules
src/components/organisms
src/components/templates AppShell, AuthLayout, SplitLayout
src/features/<dominio>   lógica y hooks
src/pages/               pantalla = template + organismos, sin estilos propios
```
- Un átomo nunca conoce el dominio.
- Ningún color, tamaño, espacio o fuente literal fuera de `tokens/` (regla de ESLint).
- Cada átomo, molécula y organismo tiene historia en Storybook (normal, cargando, vacío, error, deshabilitado) y prueba.

### Responsive y accesibilidad
- Móvil < 640 px, tablet 640–1024, escritorio > 1024. El cuidador lo usará mucho desde el móvil: diseña móvil primero.
- WCAG 2.2 AA, texto base ≥ 17 px, áreas táctiles ≥ 44 px, foco visible, `aria-*` correctos.
- Toda pantalla con datos maneja cargando (skeleton), vacío, error con reintento y éxito.
- Tokens de sesión en cookie `httpOnly` si el backend lo permite; nunca en `localStorage`.

## Pruebas (obligatorias en cada PR)
- Reloj: `./gradlew ktlintCheck detekt test` en verde.
- Web: `eslint`, `tsc --noEmit`, `vitest` en verde; Playwright para cada criterio de salida de fase.

## Flujo de trabajo
- Una rama y un PR por tarea (`feat/wear-<tema>`, `feat/web-<tema>`), commits convencionales, PR pequeños.
- Descripción del PR: qué cambia, capturas (reloj redondo y web en móvil/escritorio), cómo se probó.
- Al cerrar cada fase: reloj (emulador) y web contra la API real.

## Fases (tu parte)
0 Fundaciones: proyecto Wear OS standalone con **prototipo de alarma** (2 dosis falsas en Room, suena con el reloj en reposo, Tomada/Posponer, BootReceiver, permiso de alarmas exactas). Web: Vite, tokens, átomos, Storybook, AppShell responsive, CI.
1 Cuentas y vinculación: reloj muestra el código y guarda tokens; web con login/registro, consentimientos y "Vincular reloj".
2 Medicamentos y plan: reloj descarga el plan, Room, reprograma alarmas, pantallas Inicio y Hoy, Tile; web con formulario de medicamento y horarios, agenda de hoy y semana.
3 Tomas y adherencia: cola offline con WorkManager en el reloj; historial y gráfico de adherencia en la web.
4 Notificaciones y reportes: web con "Conectar Telegram", correo, preferencias y reportes.
5 IA: chat por voz en el reloj; chat en la web (opcional).
6 Lanzamiento: batería y pruebas en reloj físico, Play Store (Wear); accesibilidad, E2E y despliegue de la web.
