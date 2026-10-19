# Rediseño del reloj: estado y siguientes pasos

Rama: `feat/wear-tema-propio`. Al 18 de octubre de 2026 está subida al remoto, pero todavía sin PR. `./gradlew ktlintCheck detekt testDebugUnitTest` pasa en verde, con 48 pruebas.

## Hecho

### Lotes 1 a 3 de la auditoría Nielsen
- Tema propio sin Material 3: solo se conservan `AppScaffold`, `ScreenScaffold`, `ScrollIndicator` y `TimeText`, dentro de `BwScaffold.kt`.
- Componentes base en `ui/components/`: `BwText`, `BwButton`, `BwActionPill`, `BwCard`, `BwHeader`, `BwBanner`, `BwIcon`, `BwMicButton`, `BwProgress`, `BwSuccessMark` y `MedicationLabel`.
- Alerta de dosis (A1 a A6):
  - «Tomada» se ve sin desplazarse.
  - Al tocarla aparece la marca de éxito durante 1,5 s.
  - Al llegar al tope se muestra «Sin más pospuestas».
  - Omitir pide confirmación nombrando el medicamento.
  - Los números de posponer salen de `SnoozePolicy`.

### Estilo del concepto visual
Referencia de 12 vistas redondas: https://claude.ai/artifact/Sp9beKWxm2sQt3Z7fUX4Wz

- Un solo modo oscuro: se quitaron `ThemePrefs`, la paleta clara y el botón «Tema».
- Fondo negro con difuminado púrpura en todas las pantallas.
- «Tomada» y «Volver» en degradado turquesa a verde; Posponer y Omitir en cápsulas magenta.
- Avisos en amarillo; tiempo y asistente en cian.
- Asistente con micrófono redondo.
- Vinculación con el código letra a letra en casillas.
- Todos los colores están en `ui/theme/Tokens.kt` y los contrastes calculados en `docs/validacion-pendiente.md`.

### Otros
- La notificación toma los minutos de posponer de `SnoozePolicy`.
- Los iconos son vectoriales: no dependen de la fuente del reloj.
- El fondo de la ventana coincide con el tema (`res/values/themes.xml`), así que no hay destello al abrir.

## Pendiente

1. **Abrir el PR** de `feat/wear-tema-propio` hacia `main`.
2. **Vistas del concepto que la app no tiene.** Necesitan datos o pantallas nuevas:
   - Esfera con la próxima toma marcada.
   - Anillo de cuenta atrás hasta la siguiente dosis.
   - Adherencia (porcentaje y barras por día).
   - Pantalla de pospuestas («2/3, suena a las 10:50»).
   - Pantalla de sin conexión («Tus alarmas siguen sonando»).
3. **Lotes 4 a 6 de la auditoría**: Inicio y Hoy, Vinculación y Asistente. Faltan estados de carga, avisos del plan destacados y textos más claros.
4. **Comprobar en un reloj** las cápsulas de la alerta con la fuente del sistema al máximo; podrían cortarse en 192 dp.
5. **Deshacer «Tomada»**: decidido no hacerlo por ahora (ver `docs/validacion-pendiente.md`).

## Reglas acordadas
- Commits con la identidad `stehven.obando@campusucc.edu.co` y sin co-autor.
- Push y PR solo con confirmación.
- Solo se toca `ui/` y `strings.xml`, salvo excepción aprobada. ViewModels, repositorios y `SnoozePolicy` solo se leen.
- No hay reloj ni emulador: lo que no se pueda comprobar se anota en `docs/validacion-pendiente.md`.
