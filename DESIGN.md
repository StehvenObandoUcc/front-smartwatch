---
name: Recuerda
description: Señalética de salud para recordar cada medicamento a su hora, en el reloj y en el panel web.
colors:
  bg: "#ffffff"
  surface: "#e6eeed"
  surface-raised: "#ffffff"
  text: "#11201f"
  text-muted: "#3e5250"
  border: "#c9d6d4"
  border-strong: "#11201f"
  sign: "#0e5a57"
  on-sign: "#ffffff"
  sign-muted: "#c4e3e0"
  primary: "#0e5a57"
  primary-hover: "#0b4744"
  on-primary: "#ffffff"
  danger: "#b3261e"
  on-danger: "#ffffff"
  success: "#2f7d32"
  warning: "#9a5b00"
  focus: "#0e5a57"
  state-taken: "#2f7d32"
  state-due: "#9a5b00"
  state-missed: "#b3261e"
  state-skipped: "#5b6b69"
  on-state: "#ffffff"
  morning: "#f2c14e"
  on-morning: "#11201f"
  afternoon: "#f08a4b"
  on-afternoon: "#11201f"
  night: "#2b3a67"
  on-night: "#ffffff"
  med-red: "#c62828"
  med-orange: "#d4661a"
  med-yellow: "#b08900"
  med-green: "#2e7d32"
  med-teal: "#00796b"
  med-blue: "#1565c0"
  med-purple: "#6a1b9a"
  med-pink: "#ad1457"
typography:
  display:
    fontFamily: "'Atkinson Hyperlegible Next', 'Atkinson Hyperlegible', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif"
    fontSize: "2.5rem"
    fontWeight: 800
    lineHeight: 1.1
    letterSpacing: "-0.025em"
    fontFeature: "tnum"
  headline:
    fontFamily: "'Atkinson Hyperlegible Next', 'Atkinson Hyperlegible', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif"
    fontSize: "1.75rem"
    fontWeight: 800
    lineHeight: 1.25
    letterSpacing: "-0.025em"
    fontFeature: "tnum"
  title:
    fontFamily: "'Atkinson Hyperlegible Next', 'Atkinson Hyperlegible', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif"
    fontSize: "1.375rem"
    fontWeight: 700
    lineHeight: 1.35
    fontFeature: "tnum"
  body:
    fontFamily: "'Atkinson Hyperlegible Next', 'Atkinson Hyperlegible', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif"
    fontSize: "1.125rem"
    fontWeight: 400
    lineHeight: 1.55
    fontFeature: "tnum"
  label:
    fontFamily: "'Atkinson Hyperlegible Next', 'Atkinson Hyperlegible', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif"
    fontSize: "1.0625rem"
    fontWeight: 700
    lineHeight: 1.4
    fontFeature: "tnum"
rounded:
  sm: "4px"
  md: "8px"
  lg: "12px"
  full: "9999px"
spacing:
  unit: "4px"
  touch: "44px"
  control: "48px"
  plate-sm: "36px"
  plate-md: "44px"
  plate-lg: "56px"
  sidebar: "256px"
  content: "1152px"
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.on-primary}"
    typography: "{typography.body}"
    rounded: "{rounded.md}"
    padding: "0 20px"
    height: "{spacing.control}"
  button-primary-hover:
    backgroundColor: "{colors.primary-hover}"
    textColor: "{colors.on-primary}"
  button-secondary:
    backgroundColor: "{colors.surface-raised}"
    textColor: "{colors.text}"
    typography: "{typography.body}"
    rounded: "{rounded.md}"
    padding: "0 20px"
    height: "{spacing.control}"
  button-secondary-hover:
    backgroundColor: "{colors.surface}"
  button-danger:
    backgroundColor: "{colors.danger}"
    textColor: "{colors.on-danger}"
    rounded: "{rounded.md}"
    padding: "0 20px"
    height: "{spacing.control}"
  button-ghost:
    textColor: "{colors.primary}"
    rounded: "{rounded.md}"
    padding: "0 20px"
    height: "{spacing.control}"
  plate-sign:
    backgroundColor: "{colors.sign}"
    textColor: "{colors.on-sign}"
    rounded: "{rounded.md}"
    size: "{spacing.plate-md}"
  plate-on-sign:
    backgroundColor: "{colors.on-sign}"
    textColor: "{colors.sign}"
    rounded: "{rounded.md}"
    size: "{spacing.plate-sm}"
  state-taken:
    backgroundColor: "{colors.state-taken}"
    textColor: "{colors.on-state}"
    typography: "{typography.label}"
    rounded: "{rounded.sm}"
    padding: "4px 10px"
  state-due:
    backgroundColor: "{colors.state-due}"
    textColor: "{colors.on-state}"
    typography: "{typography.label}"
    rounded: "{rounded.sm}"
    padding: "4px 10px"
  state-missed:
    backgroundColor: "{colors.state-missed}"
    textColor: "{colors.on-state}"
    typography: "{typography.label}"
    rounded: "{rounded.sm}"
    padding: "4px 10px"
  state-skipped:
    backgroundColor: "{colors.state-skipped}"
    textColor: "{colors.on-state}"
    typography: "{typography.label}"
    rounded: "{rounded.sm}"
    padding: "4px 10px"
  state-upcoming:
    textColor: "{colors.text}"
    typography: "{typography.label}"
    rounded: "{rounded.sm}"
    padding: "4px 10px"
  signbar-sun:
    backgroundColor: "{colors.morning}"
    textColor: "{colors.on-morning}"
    typography: "{typography.title}"
    rounded: "{rounded.md}"
    padding: "10px 16px"
    height: "{spacing.touch}"
  signbar-dusk:
    backgroundColor: "{colors.afternoon}"
    textColor: "{colors.on-afternoon}"
    typography: "{typography.title}"
    rounded: "{rounded.md}"
    padding: "10px 16px"
    height: "{spacing.touch}"
  signbar-night:
    backgroundColor: "{colors.night}"
    textColor: "{colors.on-night}"
    typography: "{typography.title}"
    rounded: "{rounded.md}"
    padding: "10px 16px"
    height: "{spacing.touch}"
  input:
    backgroundColor: "{colors.surface-raised}"
    textColor: "{colors.text}"
    typography: "{typography.body}"
    rounded: "{rounded.md}"
    padding: "0 16px"
    height: "{spacing.control}"
  card:
    backgroundColor: "{colors.surface-raised}"
    rounded: "{rounded.lg}"
    padding: "24px"
  well:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.lg}"
  header-sign:
    backgroundColor: "{colors.sign}"
    textColor: "{colors.on-sign}"
    typography: "{typography.headline}"
    padding: "0 16px"
    height: "64px"
  nav-rail-active:
    backgroundColor: "{colors.on-sign}"
    textColor: "{colors.sign}"
    rounded: "{rounded.md}"
    height: "{spacing.touch}"
---

# Design System: Recuerda

## Overview

**Creative North Star: "Señalética de salud"**

Recuerda se lee como un hospital bien señalizado: cada opción, cada sección y cada estado es una señal. Una señal es una placa cuadrada de esquina corta con un pictograma grande de trazo grueso y, al lado, una sola palabra. El panel web pone esas señales sobre fondo blanco, con la cabecera y el riel de navegación como paneles de señal en verde petróleo profundo; el reloj pone las mismas placas sobre negro plano de pantalla OLED. La lectura es por bandas: el día se divide en franjas de color (sol, tarde, noche) y cada dosis termina en una placa de estado con su palabra.

La densidad es de servicio de salud, no de dashboard: texto grande (nada por debajo de 17 px en la web, 14 sp en el reloj), controles de 48 px y listas planas separadas por reglas en lugar de tarjetas iguales apiladas. Una sola familia tipográfica humanista, Atkinson Hyperlegible Next, hace todo el trabajo; el peso y el tamaño separan los niveles. Las superficies son planas: sin sombras, sin degradados, sin brillo. La profundidad la ponen el color plano del panel de señal, la zona tintada y la regla de dos píxeles.

El tono es clínico y confiable, nunca infantil ni frío de hospital: se rechazan el saludo de bienvenida, la tarjeta blanca redondeada de «próxima dosis», el acento verde azulado genérico y la lista de tarjetas idénticas.

**Key Characteristics:**
- Placas de pictograma cuadradas, de esquina corta, siempre con su palabra al lado.
- Paneles de señal en petróleo profundo para cabecera, riel y acción principal.
- Franjas del día como bandas de color: sol, tarde y noche.
- Cinco placas de estado con palabra: Tomada, Pendiente, No tomada, Omitida y Más tarde.
- Plano por completo: sin sombras, sin degradados, sin brillo.
- Una sola letra, Atkinson Hyperlegible Next, con cifras tabulares.
- La misma paleta de medicamentos, las mismas claves y el mismo vocabulario de pictogramas en web y reloj.

## Colors

Neutros con tinte petróleo, un único petróleo profundo como voz de la señal y de la acción, y dos familias de color con significado fijo: los estados de una dosis y las franjas del día. Los valores normativos son los del tema claro de la web (frontmatter); el tema oscuro y el reloj se describen abajo y viven en `.impeccable/design.json`.

### Primary
- **Petróleo de señal** (`sign` / `primary`): el panel de la cabecera, el riel lateral, la placa de sección y la acción principal (Tomada, Guardar). Blanco encima a 7,6:1. Al pulsar baja a **Petróleo hondo** (`primary-hover`), que también ilumina la opción bajo el cursor en el riel. El foco (`focus`) usa el mismo petróleo.
- **Petróleo velado** (`sign-muted`): la segunda línea de la cabecera (paciente y fecha) sobre el panel de señal.

### Secondary
Los estados de una dosis. Cada uno es una placa de color plano con texto blanco (≥ 5:1) y siempre lleva su palabra y su pictograma.
- **Verde Tomada** (`state-taken`, igual que `success`): Tomada; también la marca de éxito del reloj.
- **Ámbar Pendiente** (`state-due`, igual que `warning`): la dosis está en su hora y no tiene registro.
- **Rojo No tomada** (`state-missed`, igual que `danger`): pasó su ventana sin registro; también el botón destructivo y el error.
- **Gris Omitida** (`state-skipped`): la persona decidió omitirla.
- **Más tarde** no tiene color propio: es una placa de contorno de tinta (`border-strong`) con el reloj de arena, para lo que todavía no tiene estado.

### Tertiary
Las franjas del día, placas de color con pictograma y palabra.
- **Amarillo sol** (`morning`) con tinta (`on-morning`, ≥ 7:1): Mañana, 5:00 a 12:00. En el reloj, el mismo amarillo es el color de los avisos (`accent`).
- **Naranja tarde** (`afternoon`) con tinta (`on-afternoon`): Tarde, 12:00 a 19:00.
- **Azul marino noche** (`night`) con blanco (`on-night`): Noche, 19:00 a 5:00.

### Paleta de medicamentos
Ocho tonos profundos (`med-red`, `med-orange`, `med-yellow`, `med-green`, `med-teal`, `med-blue`, `med-purple`, `med-pink`) que pintan la placa del medicamento con la cápsula en blanco. La API guarda el hex que eligió la persona (a veces un 500 de Tailwind); web (`medicationColorFor`) y reloj (`medicationColor`) lo llevan al tono de la paleta con el **matiz** más cercano, nunca por distancia RGB. Las claves y los hex son idénticos en ambas plataformas.

### Neutral
- **Blanco de sala** (`bg`, `surface-raised`): fondo de la página, de las tarjetas y de los campos.
- **Zona tintada** (`surface`): avisos en la página, esqueletos de carga, placas neutras y la superficie del botón secundario al pasar el cursor.
- **Tinta petróleo** (`text`, `border-strong`): todo el texto, el contorno de campos, del botón secundario y de la placa Más tarde. ≥ 13:1 sobre el fondo.
- **Tinta suave** (`text-muted`): posología, pistas y opciones inactivas del menú móvil. ≥ 7:1.
- **Regla** (`border`): las reglas de dos píxeles entre filas, el borde de las tarjetas y del menú móvil.

### Tema oscuro (web)
Por `prefers-color-scheme: dark` o `data-theme='dark'`. Fondo `#0d1716`, superficies `#15211f` / `#1a2826`, texto `#eef5f4` / `#a9bdba`, regla `#2c3d3b`. El panel de señal baja a `#0b3f3d`, y la acción principal sube a celeste petróleo `#5fc2ba` con texto `#062321`; la noche sube a `#3b4c82`. Estados, franjas claras y paleta de medicamentos no cambian.

### Reloj
Un único modo oscuro sobre negro `#000000`. Superficie `#142320`, contorno `#3a5a56`, texto blanco y suave `#b5ccc9`. La acción principal y la placa de encabezado son un petróleo más claro (`#127a73`) para leerse sobre OLED; los datos (gráfica de adherencia, carga) van en celeste petróleo `#5fc2ba`, el mismo valor que la acción principal del tema oscuro web. Estados, franjas, tinta `#11201f` y paleta de medicamentos son los mismos hex que la web.

| Papel | Web claro | Web oscuro | Reloj |
| --- | --- | --- | --- |
| Fondo | `#ffffff` | `#0d1716` | `#000000` |
| Superficie | `#e6eeed` | `#15211f` | `#142320` |
| Señal / placa de sección | `#0e5a57` | `#0b3f3d` | `#127a73` |
| Acción principal | `#0e5a57` | `#5fc2ba` | `#127a73` |
| Dato / celeste | — | `#5fc2ba` | `#5fc2ba` |
| Estados, franjas, `med-*` | iguales | iguales | iguales |

### Named Rules
**The Color Never Alone Rule.** Ningún color lleva información solo: la placa del medicamento va con su nombre, la placa de estado con su palabra y la franja con su nombre y su horario. Si quitas el color, la pantalla se sigue entendiendo.

**The One Petrol Rule.** El petróleo de señal es a la vez el panel y la acción principal; no hay un segundo acento. Una sola acción principal por grupo.

**The Same Key Rule.** Un color de medicamento se guarda como hex y se pinta siempre con el tono de la paleta de matiz más cercano, el mismo en web y reloj.

## Typography

**Display Font:** Atkinson Hyperlegible Next (con Atkinson Hyperlegible, system-ui, Segoe UI, Roboto, sans-serif)
**Body Font:** la misma
**Label/Mono Font:** la misma, con cifras tabulares (`tnum`)

**Character:** Una letra humanista diseñada para baja visión, que distingue 0/O y 1/l/I. Se carga en tres pesos (400, 700, 800) en la web; en el reloj va empaquetada como fuente variable (400, 500, 700, 800). Las cifras son tabulares en todo el panel para que horas y porcentajes no bailen.

### Hierarchy (web)
- **Display** (800, 40 px, 1,1, tracking ajustado): el dato que se mira de un vistazo y el título de las pantallas de entrada.
- **Headline** (800, 28 px, 1,25): la palabra de la cabecera de señal, el resumen «0 de 3 tomadas».
- **Title** (700–800, 22 px, 1,35): el nombre del medicamento (800) y la palabra de cada franja; subtítulos (700).
- **Body** (400, 18 px, 1,55): texto corriente, posología, botones (700).
- **Label** (700, 17 px, 1,4): palabras de las placas de estado, del menú y la segunda línea de la cabecera (400). Es el tamaño mínimo: no existe nada más pequeño.

### Hierarchy (reloj)
- **Display** (700, 40 sp, tnum): «En 21 min», «86 %», «Tomada»; con autoajuste baja hasta 18 sp si no cabe.
- **Code** (800, 32 sp, tnum): el código de vinculación.
- **Title** (800, 18 sp): nombre del medicamento y encabezados; un nombre de una palabra se achica hasta 14 sp antes que partirse.
- **Body / Label** (400 / 700, 16 sp): texto y botones.
- **Support** (400, 14 sp): la hora curva, las placas de estado, los detalles. Mínimo del reloj.

### Named Rules
**The One Voice Rule.** Una sola familia; el peso y el tamaño separan los niveles, nunca otra fuente.

**The Whole Name Rule.** El nombre de un medicamento nunca se parte con guion ni a mitad de palabra («Hidroclorotiazid-a»). En el reloj, un nombre de una palabra va en una línea y se achica hasta caber; uno de varias palabras se parte solo entre palabras. En la web, el nombre parte solo si desborda, sin guion.

## Layout

Móvil primero, con tres tramos: móvil < 640 px, tablet 640–1024 px (`tablet`, 40rem) y escritorio > 1024 px (`desktop`, 64.0625rem). La escala de espaciado es de 4 px; los ritmos habituales son 12 px dentro de una fila, 16 px de margen de página en el móvil y 24 px desde tablet.

- **Móvil:** cabecera de señal fija arriba (mínimo 64 px) con la placa de la sección, su palabra y una segunda línea (paciente · fecha); navegación inferior fija de placas y palabras de 80 px de alto, con la activa encendida en petróleo; el contenido deja 128 px abajo para no quedar tapado y respeta `safe-area-inset-bottom`.
- **Tablet:** la navegación pasa a un riel lateral petróleo de 112 px (placa encima de la palabra).
- **Escritorio:** el riel se ensancha a `sidebar` (256 px) con placa y palabra en línea; el contenido se centra hasta `content` (1152 px).
- Las listas son filas planas (`rows`) separadas por una regla de 2 px y 12 px de aire vertical; las filas se reacomodan con `flex-wrap`: la hora y la placa de estado bajan de línea antes de recortar nada.
- **Reloj:** listas que se encogen y desvanecen en el borde (`edgeItem`) para pantallas redondas y cuadradas, páginas horizontales con indicador, espaciado de 4/8/12/16 dp. Con letra del sistema ≥ 1,15× las filas se apilan.

## Elevation & Depth

Recuerda es plano. No hay sombras: la escala `--shadow-*` de Tailwind está anulada en los tokens, y el reloj no dibuja ninguna. La profundidad se marca con color plano (el panel de señal frente al blanco), con la zona tintada para avisos y esqueletos, y con reglas y contornos de 2 px. El botón se oscurece al pulsar; no se mueve ni se eleva.

### Named Rules
**The Flat Sign Rule.** Una señal es una placa pintada: sin sombra, sin degradado, sin brillo, sin desenfoque. Si algo necesita destacar, cambia de color o gana un contorno.

## Shapes

Esquinas cortas, como una placa de señal: 4 px (`sm`) en placas de estado y barras del esqueleto, 8 px (`md`) en placas de pictograma, botones, campos, franjas y opciones de navegación, 12 px (`lg`) en tarjetas y zonas tintadas. El círculo completo (`full`) queda solo para el punto de color de medicamento. Los bordes son de 2 px: sólidos en tarjetas, campos y botón secundario, discontinuos en el estado vacío. Los pictogramas son Lucide con trazo de 2,25.

En el reloj las placas usan 6 dp y las tarjetas 12 dp; los botones son píldoras (convención de Wear OS para pantallas redondas), la marca de éxito es un disco verde y el resto respeta la esquina corta.

## Components

### Plate (átomo, firma del sistema)
Cuadrado de esquina corta (8 px) con un pictograma centrado. Tamaños 36 / 44 / 56 px con glifo de 20 / 24 / 32 px. Tonos: `sign` (petróleo con blanco), `onSign` (blanco con petróleo, sobre la cabecera), `sun` / `dusk` / `night`, `neutral` (zona tintada). Con `hex` se pinta en el tono de medicamento más cercano con la cápsula blanca. Es decorativa salvo que reciba `label`; la palabra va al lado. En el reloj, `BwPlate` (28 / 22 dp, esquina 6 dp) elige tinta o blanco según la luminancia de la placa.

### Badge: placas de estado (átomo)
Placa de 32 px mínimo de alto, esquina 4 px, palabra en Label con pictograma de 20 px: Tomada (visto), Pendiente (reloj), No tomada (aspa), Omitida (círculo con menos) y Más tarde (reloj de arena, solo contorno). En el reloj, `BwStatePlate` tiene los cuatro primeros con texto Support blanco.

### Buttons (átomo)
- **Shape:** esquina corta (8 px), alto mínimo 48 px, ancho mínimo 44 px, 20 px de relleno lateral, palabra en Body 700 con pictograma opcional a la izquierda.
- **Primary:** petróleo con texto blanco; al pasar o pulsar, petróleo hondo. Transición de color de 100 ms, anulada con `prefers-reduced-motion`.
- **Secondary:** blanco con contorno de tinta de 2 px; al pasar, zona tintada.
- **Danger:** rojo con texto blanco, 90 % de opacidad al pasar.
- **Ghost:** texto petróleo sin fondo, subrayado al pasar; para enlaces de acción («Ver historial»).
- **onSign:** transparente sobre la cabecera, texto y foco blancos.
- **Disabled:** zona tintada, tinta suave, cursor bloqueado. **Loading:** spinner en lugar del pictograma y `aria-busy`.
- **Reloj:** `BwButton` es una píldora de 52 dp, principal en petróleo `#127a73` plano con texto blanco, secundaria en superficie con contorno de 1 dp; al pulsar baja al 80 % de opacidad.

### Cards / Containers
- **Card:** blanco, borde de regla de 2 px, esquina 12 px, sin sombra; contiene formularios (24 px de relleno en entrada).
- **Well:** zona tintada de esquina 12 px para avisos en la página («Falta verificar tu correo»).
- **Rows:** la forma preferida de mostrar dosis, medicamentos y resúmenes: filas planas con regla de 2 px, no tarjetas.

### Inputs / Fields
- **Style:** blanco, contorno de tinta de 2 px, esquina 8 px, alto 48 px, texto Body.
- **Focus:** contorno de foco petróleo de 3 px separado 2 px (`:focus-visible` global).
- **Error / Disabled:** contorno rojo y mensaje en rojo debajo con `role="alert"` y `aria-describedby`; deshabilitado al 50 %. `FormField` pone la etiqueta encima y la pista en tinta suave.

### Navigation (plantilla AppShell)
- **Cabecera:** panel de señal fijo, placa `onSign` de la sección, palabra en Headline 800 (se recorta con puntos suspensivos, no se parte) y segunda línea en Label `sign-muted`.
- **Riel (tablet y escritorio):** panel petróleo; cada opción es pictograma de 28 px + palabra, alto mínimo 44 px; la activa se enciende en blanco con texto petróleo; al pasar, petróleo hondo.
- **Menú inferior (móvil):** franja blanca con regla superior; cada opción es una placa pequeña + palabra corta; la activa lleva la placa petróleo y la tinta plena, las demás placa neutra y tinta suave.
- Enlace «Saltar al contenido» visible al recibir foco.

### SignBar (molécula)
Encabezado de sección como banda de color: pictograma de 28 px, palabra en Title 800 y un dato a la derecha («5:00 a 12:00»). Alto mínimo 44 px, esquina 8 px, tonos `sign` / `sun` / `dusk` / `night` / `neutral`. En el reloj, `BwSignBar` centra el pictograma y la palabra.

### DoseRow y SummaryRow
La dosis: placa del medicamento en su color, nombre en Title 800 y posología en tinta suave; a la derecha la hora en Title 800 y la placa de estado; debajo, Tomada (principal) y Omitir (secundario) si falta registrarla. El resumen: placa pequeña de la categoría, su nombre, el dato en la misma línea desde tablet y el enlace a la derecha.

### Estados de carga, vacío y error
- **Skeleton:** filas con la forma del contenido real (placa + dos barras) en zona tintada, pulso solo con `motion-safe`.
- **EmptyState:** borde discontinuo de 2 px, placa neutra grande, qué pasa y el siguiente paso; nunca un vacío sin salida.
- **ErrorRetry:** borde rojo de 2 px, aviso con pictograma y botón secundario Reintentar.
- **Reloj:** arco celeste que gira (`BwProgress`), disco verde con visto dibujado (`BwSuccessMark`) y estado vacío centrado.

## Do's and Don'ts

### Do:
- **Do** acompañar cada color con su palabra: placa de medicamento + nombre, placa de estado + palabra, franja + nombre y horario.
- **Do** usar las cinco palabras de estado tal cual: Tomada, Pendiente, No tomada, Omitida y Más tarde.
- **Do** dar a cada opción y categoría su placa de pictograma del vocabulario común (`icons.ts` en web, los mismos glifos Lucide en el reloj).
- **Do** mantener el texto en 17 px o más en la web (14 sp en el reloj) y las áreas táctiles en 44 px o más (48 px en botones y campos; 48 dp en el reloj).
- **Do** cumplir WCAG 2.2 AA: foco visible de 3 px, `aria-*` en placas que se anuncian, el foco nunca tapado por la navegación fija.
- **Do** resolver cada pantalla con datos en cuatro estados: esqueleto, vacío con salida, error con reintento y éxito.
- **Do** tomar todo valor visual de los tokens (`tokens.css` en la web, `Tokens.kt` en el reloj).

### Don't:
- **Don't** partir el nombre de un medicamento con guion ni a mitad de palabra.
- **Don't** escribir hex, px, rem ni valores arbitrarios de Tailwind (`p-[13px]`, `bg-[#fff]`) fuera de `src/design/tokens`; la regla de ESLint lo bloquea.
- **Don't** usar sombras, degradados, brillos ni desenfoques: las señales son planas.
- **Don't** redondear las placas más allá de la esquina corta ni convertirlas en círculos o píldoras en la web.
- **Don't** poner información solo en el color ni inventar un segundo acento junto al petróleo.
- **Don't** usar una segunda familia tipográfica ni pesos fuera de los cargados en la web (400, 700, 800).
- **Don't** volver al estándar de la categoría: saludo de bienvenida, tarjeta blanca redondeada de «próxima dosis» o lista de tarjetas iguales.
