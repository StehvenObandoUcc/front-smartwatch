# Validación pendiente del reloj (`wear/`)

El rediseño del reloj (tema propio, Lotes 1 a 3 de la auditoría Nielsen y el estilo del concepto visual) se cerró **sin validación visual**: no hubo reloj ni emulador. Este documento recoge lo que no se ha verificado y lo que hace falta para verificarlo.

Rama: `feat/wear-tema-propio`.

## Qué sí está verificado

- `./gradlew ktlintCheck detekt testDebugUnitTest` en verde (48 pruebas).
- Tema actual: un único modo oscuro, como en el concepto visual. Fondo negro con difuminado púrpura en todas las pantallas, acción principal en degradado turquesa-verde, Posponer y Omitir en cápsulas magenta, avisos en amarillo y tiempo en cian.
- Contrastes WCAG calculados (fórmula de luminancia relativa):

| Par | Contraste |
|---|---|
| Texto blanco / centro del difuminado (#3A1F5E) | 13,66 |
| Texto blanco / superficie (#1E1D2A) | ≥ 13 |
| Texto suave (#B7B4CC) / centro del difuminado | 6,77 |
| Texto oscuro / degradado principal (turquesa a verde) | 9,06 a 10,5 |
| Texto blanco / cápsula magenta (#B52DAB a #8E1F86) | 5,35 a 7,81 |
| Texto oscuro / amarillo de aviso | 11,26 |
| Amarillo / superficie | 10,93 |
| Colores de medicamento (mínimo sobre superficie y difuminado) | 4,3 |

- El gris de la referencia (#8E8CA6) no se usa para texto: sobre el difuminado da 4,2:1.
- `DoseAlertScreenTest` usa Robolectric con gráficos nativos, en un reloj redondo de 192 dp con fuente al 1,3x. Comprueba que «Tomada» se ve sin desplazarse, también con un nombre de dos líneas. Robolectric mide con sus propias fuentes, no con las del reloj, así que es orientativo.

## Qué no está verificado

1. **Apariencia real.** Difuminado, degradados, cápsulas e insignia de éxito en pantalla real.
2. **Cápsulas de la alerta con fuente grande.** «Posponer» y «Omitir» van lado a lado en una sola línea; con la fuente al máximo podrían cortarse en relojes de 192 dp.
3. **Casillas del código de vinculación** con códigos largos: se estrechan para caber, pero no se ha visto con el código real.
4. **TalkBack.**
   - Tarjetas anunciadas con «Abrir dosis».
   - Títulos anunciados como encabezado.
   - Icono de aviso anunciado como «Aviso».
   - Cápsulas leídas con su etiqueta y sus minutos.
   - Código de vinculación leído letra a letra.
   - Micrófono anunciado con su etiqueta.
5. **Rotary.** Desplazamiento con la corona o el bisel en todas las listas.
6. **Pantalla cuadrada.** Solo se ha pensado para redonda.
7. **Fuente máxima del sistema.** Las pruebas usan 1,3x.
8. **Marca de éxito sobre la pantalla de bloqueo** durante 1,5 s antes de cerrarse.
9. **Iconos vectoriales** (aviso, visto, cerrar, atrás, reloj, micrófono): tamaño y nitidez.
10. **Indicador de scroll y hora** sobre el difuminado.

## Pendientes de diseño (no implementados)

- **Deshacer «Tomada».** Decidido no implementarlo por ahora.
  - Una ventana corta (1,5 a 2 s), registrando al final, no toca datos, pero da poco tiempo y pierde la toma si se desliza para salir.
  - Un deshacer útil (5 s o más) necesita revertir un registro ya guardado, lo que toca la capa de datos.
- **Lotes 4 a 6 de la auditoría** (Inicio y Hoy, Vinculación, Asistente): sin empezar en lo funcional (estados de carga, avisos de plan y textos).
- **Vistas del concepto ya en la app:** resumen tras posponer («Pospuesta 2 de 3», hora en que vuelve a sonar y cuántas quedan), marca de «Tomada» con medicamento, dosis y hora, contador «1 de 3 tomadas» en Hoy y aviso «Sin conexión» en Inicio (toca para reintentar). Inicio son tres páginas que se deslizan en horizontal, con indicador de página: Próxima toma, Hoy y Adherencia. Tras la crítica de diseño (25/40): las listas se encogen al llegar al borde redondo; Próxima toma sin título ni caja, con «En N min» a 40 sp; Hoy en filas (hora a la izquierda); avisos compactos sin borde amarillo; Posponer neutra y Omitir solo contorno (sin magenta). Segunda pasada (28/40 → ~31): con letra grande la cifra encoge en vez de cortarse y las palabras largas se parten con guion; estados vacíos con icono y «Agrega tus medicamentos en el panel web»; el estado del plan solo aparece si pide algo; «Pospuesta» con su insignia; las horas siguen el ajuste de 12/24 h. Pantalla de adherencia (tercera página): porcentaje de los últimos 14 días y una barra por día, guardada para verla sin conexión (contrato 0.8.0, `GET /devices/me/adherence`).
- **Vistas del concepto que faltan:**
  - Anillo de próxima toma: será un Tile, que el plan deja para el cierre.
  - Esfera: es una watch face, fuera del plan.

## Capturas necesarias

Idealmente en un reloj redondo de 192 dp con la fuente del sistema al máximo, y una tanda en reloj cuadrado si hay.

1. Inicio con los dos avisos de permiso y la próxima dosis.
2. Inicio desplazado hasta el final (estado del plan).
3. Hoy con 3 o más dosis de colores distintos, incluidos naranja, amarillo y rojo.
4. Alerta pendiente sin desplazar.
5. Alerta con el tope de pospuestas («Sin más pospuestas»).
6. Alerta con un nombre de medicamento de dos líneas.
7. Confirmación de omitir.
8. Marca de éxito tras «Tomada», también abierta desde la pantalla de bloqueo.
9. Vinculación con código.
10. Asistente con respuesta.
11. La notificación de dosis.
12. Resumen tras posponer (con 1 y con 2 pospuestas previas).
13. Inicio sin conexión (modo avión) y su reintento.
14. Adherencia con datos, sin dosis vencidas y antes de la primera sincronización.
15. Bienvenida: «Usar en este reloj» y «Vincular con el panel web».
16. Nuevo medicamento en el reloj (teclado, −/+ de la hora, Otra hora) y su alarma sin vincular ni internet.
17. Página Reloj: sin vincular (lista local, Vincular) y vinculado (Desvincular con confirmación).
18. Vincular por pasos (código arriba y dirección del panel) y el paso guiado de permisos.
