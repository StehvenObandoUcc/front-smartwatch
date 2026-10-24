---
name: Recuerda
description: Señalética amable para recordar cada medicamento a su hora, en el reloj y en el panel web.
colors:
  on-light: "#f4faf9"
  bg: "#f5f8f7"
  surface: "#e6eeed"
  surface-raised: "#fcfdfd"
  text: "#11201f"
  text-muted: "#3e5250"
  border: "#c9d6d4"
  border-strong: "#11201f"
  sign: "#0e5a57"
  on-sign: "#f4faf9"
  sign-muted: "#c4e3e0"
  primary: "#0e5a57"
  primary-hover: "#0b4744"
  on-primary: "#f4faf9"
  danger: "#b3261e"
  on-danger: "#f4faf9"
  success: "#2f7d32"
  warning: "#9a5b00"
  focus: "#0e5a57"
  state-taken: "#2f7d32"
  state-due: "#9a5b00"
  state-missed: "#b3261e"
  state-skipped: "#5b6b69"
  on-state: "#f4faf9"
  morning: "#f2c14e"
  on-morning: "#11201f"
  afternoon: "#f08a4b"
  on-afternoon: "#11201f"
  night: "#2b3a67"
  on-night: "#f4faf9"
  med-red: "#c62828"
  med-orange: "#d4661a"
  med-yellow: "#b08900"
  med-green: "#2e7d32"
  med-teal: "#00796b"
  med-blue: "#1565c0"
  med-purple: "#6a1b9a"
  med-pink: "#ad1457"
typography:
  display-xl:
    fontFamily: "'Atkinson Hyperlegible Next', 'Atkinson Hyperlegible', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif"
    fontSize: "4.25rem"
    fontWeight: 800
    lineHeight: 1.02
    letterSpacing: "-0.02em"
  display-lg:
    fontFamily: "'Atkinson Hyperlegible Next', 'Atkinson Hyperlegible', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif"
    fontSize: "3.25rem"
    fontWeight: 800
    lineHeight: 1.05
    letterSpacing: "-0.02em"
  display:
    fontFamily: "'Atkinson Hyperlegible Next', 'Atkinson Hyperlegible', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif"
    fontSize: "2.5rem"
    fontWeight: 800
    lineHeight: 1.1
    letterSpacing: "-0.02em"
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
  sm: "8px"
  md: "12px"
  lg: "16px"
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
  measure: "60ch"
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
  card-raised:
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

**Creative North Star: "Señalética amable"**

Recuerda se lee como un lugar de cuidado bien señalizado, sereno y cercano: cada opción, cada sección y cada estado es una señal. Una señal es una placa de esquina suave con un pictograma grande de trazo grueso y, al lado, una sola palabra. El panel web pone esas señales sobre un blanco roto con tinte petróleo, con la cabecera y el riel de navegación como paneles de señal en verde petróleo profundo y los bloques del panel como tarjetas apenas elevadas; el reloj pone las mismas placas sobre un casi negro con tinte petróleo, plano. No hay blanco ni negro puros en ningún sitio. La lectura es por bandas: el día se divide en franjas de color (sol, tarde, noche) y cada dosis termina en una placa de estado con su palabra.

La densidad es de servicio de salud, no de dashboard: texto grande (nada por debajo de 17 px en la web, 14 sp en el reloj), controles de 48 px, mucho aire entre secciones y listas planas separadas por reglas dentro de cada bloque. Cada bloque del panel dice qué pregunta responde. Una sola familia tipográfica humanista, Atkinson Hyperlegible Next, hace todo el trabajo; el peso, el tamaño y un interletrado apretado en los titulares separan los niveles. La profundidad es mínima y amable: un único nivel de sombra suave tintada de petróleo en las tarjetas elevadas, y un degradado tonal de petróleo reservado a tres zonas de marca (banda del hero, banda de CTA y panel del login). Lo demás es color plano, zona tintada y la regla de dos píxeles.

El tono es confiable y amable, nunca infantil ni frío de hospital: se rechazan el saludo de bienvenida, el acento genérico de la categoría, la lista de tarjetas idénticas y las cifras o testimonios inventados.

**Key Characteristics:**
- Placas de pictograma de esquina suave, siempre con su palabra al lado.
- Paneles de señal en petróleo profundo para cabecera, riel y acción principal.
- Blanco roto con tinte petróleo de fondo y tarjetas apenas elevadas; ni blanco ni negro puros.
- Franjas del día como bandas de color: sol, tarde y noche.
- Cinco placas de estado con palabra: Tomada, Pendiente, No tomada, Omitida y Más tarde.
- Un solo nivel de elevación tintada y un degradado tonal de petróleo solo en tres zonas de marca.
- Una sola letra, Atkinson Hyperlegible Next, con cifras tabulares.
- La misma paleta de medicamentos, las mismas claves y el mismo vocabulario de pictogramas en web y reloj.

## Colors

Neutros con tinte petróleo, un único petróleo profundo como voz de la señal y de la acción, y dos familias de color con significado fijo: los estados de una dosis y las franjas del día. Los valores normativos son los del tema claro de la web (frontmatter); el tema oscuro y el reloj se describen abajo y viven en `.impeccable/design.json`.

### Primary
- **Petróleo de señal** (`sign` / `primary`): el panel de la cabecera, el riel lateral, la placa de sección y la acción principal (Tomada, Guardar). Claro (`on-light`) encima a 7,6:1. Al pulsar baja a **Petróleo hondo** (`primary-hover`), que también ilumina la opción bajo el cursor en el riel. El foco (`focus`) usa el mismo petróleo.
- **Petróleo velado** (`sign-muted`): la segunda línea de la cabecera (paciente y fecha) sobre el panel de señal.

### Secondary
Los estados de una dosis. Cada uno es una placa de color plano con texto claro (`on-state`, ≥ 4,8:1) y siempre lleva su palabra y su pictograma.
- **Verde Tomada** (`state-taken`, igual que `success`): Tomada; también la marca de éxito del reloj.
- **Ámbar Pendiente** (`state-due`, igual que `warning`): la dosis está en su hora y no tiene registro.
- **Rojo No tomada** (`state-missed`, igual que `danger`): pasó su ventana sin registro; también el botón destructivo y el error.
- **Gris Omitida** (`state-skipped`): la persona decidió omitirla.
- **Más tarde** no tiene color propio: es una placa de contorno de tinta (`border-strong`) con el reloj de arena, para lo que todavía no tiene estado.

### Tertiary
Las franjas del día, placas de color con pictograma y palabra.
- **Amarillo sol** (`morning`) con tinta (`on-morning`, ≥ 7:1): Mañana, 5:00 a 12:00. En el reloj, el mismo amarillo es el color de los avisos (`accent`).
- **Naranja tarde** (`afternoon`) con tinta (`on-afternoon`): Tarde, 12:00 a 19:00.
- **Azul marino noche** (`night`) con claro (`on-night`): Noche, 19:00 a 5:00.

### Paleta de medicamentos
Ocho tonos profundos (`med-red`, `med-orange`, `med-yellow`, `med-green`, `med-teal`, `med-blue`, `med-purple`, `med-pink`) que pintan la placa del medicamento con la cápsula en claro (`on-light`). La API guarda el hex que eligió la persona (a veces un 500 de Tailwind); web (`medicationColorFor`) y reloj (`medicationColor`) lo llevan al tono de la paleta con el **matiz** más cercano, nunca por distancia RGB. Las claves y los hex son idénticos en ambas plataformas.

### Neutral
- **Claro de sala** (`bg`): blanco roto con tinte petróleo, fondo de la página.
- **Tarjeta** (`surface-raised`): casi blanco, para tarjetas, campos y bloques elevados.
- **Claro sobre color** (`on-light`): el texto y los pictogramas sobre la señal, los estados y la noche; sustituye al blanco puro.
- **Zona tintada** (`surface`): avisos en la página, esqueletos de carga, placas neutras y la superficie del botón secundario al pasar el cursor.
- **Tinta petróleo** (`text`, `border-strong`): todo el texto, el contorno de campos y del botón secundario. 15,7:1 sobre el fondo.
- **Tinta suave** (`text-muted`): posología, pistas y opciones inactivas del menú móvil. ≥ 7:1.
- **Regla** (`border`): las reglas de dos píxeles entre filas, el borde de las tarjetas y del menú móvil.

### Tema oscuro (web)
Por `prefers-color-scheme: dark` o `data-theme='dark'`. Fondo `#0d1716`, superficies `#15211f` / `#1a2826`, texto `#eef5f4` / `#a9bdba`, regla `#2c3d3b`. El panel de señal baja a `#0b3f3d`, y la acción principal sube a celeste petróleo `#5fc2ba` con texto `#062321`; la noche sube a `#3b4c82`. Estados, franjas claras y paleta de medicamentos no cambian.

### Reloj
Un único modo oscuro sobre un casi negro con tinte petróleo, plano: `#0c1615`. Superficie `#142320`, contorno `#3a5a56`, texto claro `#eef5f4` y suave `#b5ccc9`. La acción principal y la placa de encabezado son un petróleo más claro (`#127a73`) para leerse sobre OLED; los datos (gráfica de adherencia, carga) van en celeste petróleo `#5fc2ba`, el mismo valor que la acción principal del tema oscuro web. Estados, franjas, tinta `#11201f` y paleta de medicamentos son los mismos hex que la web.

| Papel | Web claro | Web oscuro | Reloj |
| --- | --- | --- | --- |
| Fondo | `#f5f8f7` | `#0d1716` | `#0c1615` |
| Texto | `#11201f` | `#eef5f4` | `#eef5f4` |
| Superficie | `#e6eeed` | `#15211f` | `#142320` |
| Señal / placa de sección | `#0e5a57` | `#0b3f3d` | `#127a73` |
| Acción principal | `#0e5a57` | `#5fc2ba` | `#127a73` |
| Dato / celeste | — | `#5fc2ba` | `#5fc2ba` |
| Estados, franjas, `med-*` | iguales | iguales | iguales |

### Named Rules
**The Color Never Alone Rule.** Ningún color lleva información solo: la placa del medicamento va con su nombre, la placa de estado con su palabra y la franja con su nombre y su horario. Si quitas el color, la pantalla se sigue entendiendo.

**The One Petrol Rule.** El petróleo de señal es a la vez el panel y la acción principal; no hay un segundo acento. Una sola acción principal por grupo.

**The No Pure Rule.** Ni `#000000` ni `#ffffff` (ni `black` ni `white`) en ningún estilo, tema o recurso, en web y reloj. El claro es `on-light` (`#f4faf9`, web) o `#eef5f4` (reloj); el oscuro, la tinta petróleo o el fondo tintado.

**The Same Key Rule.** Un color de medicamento se guarda como hex y se pinta siempre con el tono de la paleta de matiz más cercano, el mismo en web y reloj.

## Typography

**Display Font:** Atkinson Hyperlegible Next (con Atkinson Hyperlegible, system-ui, Segoe UI, Roboto, sans-serif)
**Body Font:** la misma
**Label/Mono Font:** la misma, con cifras tabulares (`tnum`)

**Character:** Una letra humanista diseñada para baja visión, que distingue 0/O y 1/l/I. Se carga en tres pesos (400, 700, 800) en la web; en el reloj va empaquetada como fuente variable (400, 500, 700, 800). Las cifras son tabulares en todo el panel para que horas y porcentajes no bailen.

### Hierarchy (web)
- **Display XL / LG** (800, 68 / 52 px, 1,02 / 1,05, interletrado −0,02em): solo titulares de la landing; texto de apoyo de 17–18 px en tinta suave, como máximo `measure` (60 caracteres) por línea.
- **Display** (800, 40 px, 1,1, interletrado −0,02em): el dato que se mira de un vistazo y el título de las pantallas de entrada.
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
- **Landing:** secciones separadas por 96–128 px en escritorio y 64 px en móvil; textos de apoyo de 60 caracteres como máximo.
- **Panel en bento:** los bloques tienen tamaños según su importancia (Hoy el mayor, las franjas a todo el ancho, Adherencia, Reloj y Avisos medianos); nunca cuatro tarjetas iguales. En el móvil, una columna en el orden Hoy, franjas, Avisos, Adherencia, Reloj.
- **Reloj:** listas que se encogen y desvanecen en el borde (`edgeItem`) para pantallas redondas y cuadradas, páginas horizontales con indicador, espaciado de 4/8/12/16 dp. Con letra del sistema ≥ 1,15× las filas se apilan.

### Named Rules
**The Block Rule.** Cada bloque del panel lleva (1) pictograma y título de una o dos palabras, (2) una frase con la pregunta que responde («¿Se tomó todo hoy?») y (3) el dato. Un bloque que no responde una pregunta clara sobra.

## Elevation & Depth

Recuerda tiene un único nivel de elevación: una sombra suave, difusa y tintada de petróleo (`--elevation`, utilidad `shadow-raised` / `raised`) en las tarjetas elevadas de la web, como los bloques del panel y las tarjetas de la landing. Todo lo demás es plano: la profundidad se marca con color plano (el panel de señal frente al fondo), con la zona tintada para avisos y esqueletos, y con reglas y contornos de 2 px. El reloj no dibuja sombras. El botón se oscurece al pulsar; no se mueve ni se eleva.

El degradado tonal de petróleo (`--sign-gradient`, de `#0b4744` a `#127a73`, mismo matiz) existe solo en tres zonas de marca: la banda del hero (con franjas verticales sutiles, `--sign-stripes`), la banda final de CTA y el panel del login. Encima solo va texto de 18 px o más (claro a 4,9:1 en el extremo claro) o maquetas del producto.

### Named Rules
**The One Lift Rule.** Un solo nivel de elevación, siempre tintado de petróleo y difuso; nunca una sombra dura, de color o con desplazamiento grande, y nunca dos niveles apilados.

**The Three Gradients Rule.** El degradado tonal de petróleo solo vive en la banda del hero, la banda de CTA y el panel del login. Nunca en el panel de datos, en botones ni detrás de texto pequeño.

## Shapes

Esquinas suaves y regulares: 8 px (`sm`) en las placas de estado y las barras del esqueleto, 12 px (`md`) en placas de pictograma, botones, campos, franjas y opciones de navegación, 16 px (`lg`) en tarjetas, bloques del panel y zonas tintadas. El círculo completo (`full`) queda para el punto de color de medicamento y los anillos. Los bordes son de 2 px: sólidos en campos y botón secundario, discontinuos en el estado vacío. Los pictogramas son Lucide con trazo de 2,25.

En el reloj las placas usan 6 dp y las tarjetas 12 dp; los botones son píldoras (convención de Wear OS para pantallas redondas), la marca de éxito es un disco verde y los anillos son círculos completos.

## Components

### Plate (átomo, firma del sistema)
Cuadrado de esquina suave (12 px) con un pictograma centrado. Tamaños 36 / 44 / 56 px con glifo de 20 / 24 / 32 px. Tonos: `sign` (petróleo con claro), `onSign` (claro con petróleo, sobre la cabecera), `sun` / `dusk` / `night`, `neutral` (zona tintada). Con `hex` se pinta en el tono de medicamento más cercano con la cápsula en claro. Es decorativa salvo que reciba `label`; la palabra va al lado. En el reloj, `BwPlate` (28 / 22 dp, esquina 6 dp) elige tinta o claro según la luminancia de la placa.

### Badge: placas de estado (átomo)
Placa de 32 px mínimo de alto, esquina 8 px, palabra en Label con pictograma de 20 px: Tomada (visto), Pendiente (reloj), No tomada (aspa), Omitida (círculo con menos) y Más tarde (reloj de arena, tinte neutro sin borde). En el reloj, `BwStatePlate` tiene los cuatro primeros con texto Support claro.

### Buttons (átomo)
- **Shape:** esquina suave (12 px), alto mínimo 48 px, ancho mínimo 44 px, 20 px de relleno lateral, palabra en Body 700 con pictograma opcional a la izquierda.
- **Primary:** petróleo con texto claro; al pasar o pulsar, petróleo hondo. Transición de color de 100 ms, anulada con `prefers-reduced-motion`.
- **Secondary:** tarjeta con contorno de tinta de 2 px; al pasar, zona tintada.
- **Danger:** rojo con texto claro, 90 % de opacidad al pasar.
- **Ghost:** texto petróleo sin fondo, subrayado al pasar; para enlaces de acción («Ver historial»).
- **onSign:** transparente sobre la cabecera, texto y foco claros.
- **Disabled:** zona tintada, tinta suave, cursor bloqueado. **Loading:** spinner en lugar del pictograma y `aria-busy`.
- **Reloj:** `BwButton` es una píldora de 52 dp, principal en petróleo `#127a73` plano con texto claro, secundaria en superficie con contorno de 1 dp; al pulsar baja al 80 % de opacidad.

### Cards / Containers
- **Card:** tarjeta casi blanca (`surface-raised`), borde de regla de 2 px, esquina 16 px, sin sombra; contiene formularios (24 px de relleno en entrada).
- **Card raised** (`raised`): la misma tarjeta sin borde y con el único nivel de elevación; los bloques del panel en bento y las tarjetas de la landing.
- **Well:** zona tintada de esquina 16 px para avisos en la página («Falta verificar tu correo»).
- **Rows:** la forma preferida de mostrar dosis, medicamentos y resúmenes: filas planas con regla de 2 px, no tarjetas.

### Inputs / Fields
- **Style:** tarjeta casi blanca, contorno de tinta de 2 px, esquina 12 px, alto 48 px, texto Body.
- **Focus:** contorno de foco petróleo de 3 px separado 2 px (`:focus-visible` global).
- **Error / Disabled:** contorno rojo y mensaje en rojo debajo con `role="alert"` y `aria-describedby`; deshabilitado al 50 %. `FormField` pone la etiqueta encima y la pista en tinta suave.

### Navigation (plantilla AppShell)
- **Cabecera:** panel de señal fijo, placa `onSign` de la sección, palabra en Headline 800 (se recorta con puntos suspensivos, no se parte) y segunda línea en Label `sign-muted`.
- **Riel (tablet y escritorio):** panel petróleo; cada opción es pictograma de 28 px + palabra, alto mínimo 44 px; la activa se enciende en claro con texto petróleo; al pasar, petróleo hondo.
- **Menú inferior (móvil):** franja de tarjeta con regla superior; cada opción es una placa pequeña + palabra corta; la activa lleva la placa petróleo y la tinta plena, las demás placa neutra y tinta suave.
- Enlace «Saltar al contenido» visible al recibir foco.

### SignBar (molécula)
Encabezado de sección como banda de color: pictograma de 28 px, palabra en Title 800 y un dato a la derecha («5:00 a 12:00»). Alto mínimo 44 px, esquina 12 px, tonos `sign` / `sun` / `dusk` / `night` / `neutral`. En el reloj, `BwSignBar` centra el pictograma y la palabra.

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
- **Do** dar a cada bloque del panel su pictograma, su título, la pregunta que responde y el dato (The Block Rule).
- **Do** usar movimiento con propósito (entrada suave de secciones, anillo que se dibuja, cifras que cuentan) con `ease-out` y anularlo con `prefers-reduced-motion`.
- **Do** tomar todo valor visual de los tokens (`tokens.css` en la web, `Tokens.kt` en el reloj).

### Don't:
- **Don't** partir el nombre de un medicamento con guion ni a mitad de palabra.
- **Don't** escribir hex, px, rem ni valores arbitrarios de Tailwind (`p-[13px]`, `bg-[#fff]`) fuera de `src/design/tokens`; la regla de ESLint lo bloquea.
- **Don't** usar `#000000`, `#ffffff`, `black` ni `white` en ningún estilo, tema o recurso (The No Pure Rule).
- **Don't** usar más de un nivel de sombra, sombras duras o de color, brillos ni desenfoques; ni degradados fuera de la banda del hero, la banda de CTA y el panel del login.
- **Don't** salirse de la escala de esquinas (8 / 12 / 16 px) ni convertir placas o botones en píldoras en la web.
- **Don't** poner información solo en el color ni inventar un segundo acento junto al petróleo.
- **Don't** usar una segunda familia tipográfica ni pesos fuera de los cargados en la web (400, 700, 800).
- **Don't** volver al estándar de la categoría: saludo de bienvenida («Buenos días, Clara»), cuatro tarjetas iguales o cifras, testimonios y logos inventados; donde haga falta un dato real que no existe, va el marcador [DATO REAL PENDIENTE].
