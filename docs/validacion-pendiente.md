# Validación pendiente del reloj (`wear/`)

El rediseño del reloj (tema propio y Lotes 1 a 3 de la auditoría Nielsen) se cerró **sin validación visual**: no hubo reloj ni emulador. Este documento recoge lo que no se ha verificado y lo que hace falta para verificarlo.

Rama: `feat/wear-tema-propio`.

## Qué sí está verificado

- `./gradlew ktlintCheck detekt testDebugUnitTest` en verde.
- Contrastes WCAG calculados para los dos modos (fórmula de luminancia relativa):

| Par | Oscuro | Claro |
|---|---|---|
| Texto / fondo (y borde / fondo) | 11,52 | 11,22 |
| Texto / superficie (y borde / superficie) | 9,49 | 12,89 |
| Acento / fondo | 8,67 | 5,39 |
| Acento / superficie | 7,14 | 6,19 |
| Texto del botón principal | 11,52 | 11,22 |
| Colores de medicamento (mínimo sobre fondo y superficie) | 3,44 | 3,84 |

- `DoseAlertScreenTest` usa Robolectric con gráficos nativos, en un reloj redondo de 192 dp con fuente al 1,3x. Comprueba que «Tomada» se ve sin desplazarse, también con un nombre de medicamento de dos líneas. Robolectric mide con sus propias fuentes, no con las del reloj, así que es orientativo.

## Qué no está verificado

1. **Apariencia real.** Colores, bordes de 2 dp, formas píldora y tarjetas en pantalla real, en los dos modos.
2. **Acento frente a naranja y amarillo en modo claro.** Distancias de color: naranja `#A84A00` frente a acento `#7A4E00`, ΔE 24; amarillo `#6B6B00` frente al acento, ΔE 26. Si se confunden, **se cambia el acento claro, no los colores de medicamento** (decisión tomada).
3. **TalkBack.**
   - Que lea las tarjetas con «Abrir dosis».
   - Que los títulos se anuncien como encabezado.
   - Que el icono de aviso se anuncie como «Aviso».
   - Que el icono de tema no se lea, porque la etiqueta ya dice «Tema: Oscuro/Claro».
   - Que el indicador de carga se anuncie como «Cargando».
4. **Rotary.** Desplazamiento con la corona o el bisel en todas las listas.
5. **Pantalla cuadrada.** Solo se ha pensado para redonda.
6. **Fuente máxima del sistema.** Las pruebas usan 1,3x; el máximo del reloj puede ser mayor.
7. **Marca de éxito sobre la pantalla de bloqueo.** Que la alerta abierta desde la notificación muestre la marca durante 1,5 s completos antes de cerrarse.
8. **Destello al abrir en modo claro.** El fondo de la ventana se fija al abrir, pero la pantalla de inicio del sistema (antes de que arranque la app) seguirá usando el oscuro del manifiesto.
9. **Iconos vectoriales** (`ic_warning`, `ic_theme`). Tamaño y nitidez en pantalla.
10. **Indicador de scroll y hora** con los colores del tema, en los dos modos.

## Pendientes de diseño (no implementados)

- **Deshacer «Tomada».** Decidido no implementarlo por ahora.
  - Una ventana corta (1,5 a 2 s), registrando al final, no toca datos, pero da poco tiempo y pierde la toma si se desliza para salir.
  - Un deshacer útil (5 s o más) necesita revertir un registro ya guardado, lo que toca la capa de datos.
- **Lotes 4 a 6 de la auditoría** (Inicio y Hoy, Vinculación, Asistente): sin empezar.

## Capturas necesarias (cada una en modo oscuro y en modo claro)

Idealmente en un reloj redondo de 192 dp con la fuente del sistema al máximo, y una tanda en reloj cuadrado si hay.

1. Inicio con los dos avisos de permiso y la próxima dosis.
2. Inicio desplazado hasta el final (estado del plan y botón de tema).
3. Hoy con 3 o más dosis de colores distintos, incluidos naranja, amarillo y rojo.
4. Alerta pendiente sin desplazar.
5. Alerta con el tope de pospuestas («Sin más pospuestas»).
6. Alerta con un nombre de medicamento de dos líneas.
7. Confirmación de omitir.
8. Marca de éxito tras «Tomada», también abierta desde la pantalla de bloqueo.
9. Vinculación con código.
10. Asistente con respuesta.
11. La app abriéndose en modo claro (para ver si hay destello).
12. La notificación de dosis.
