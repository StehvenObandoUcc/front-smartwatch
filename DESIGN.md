---
name: Recuerda
description: El día en franjas de luz, para recordar cada medicamento a su hora, en el reloj y en el panel web.
colors:
  bg: "#fbf6ef"
  surface: "#f1ebe2"
  surface-raised: "#fffdfa"
  text: "#0d0d0c"
  text-muted: "#63615e"
  border: "rgba(13,13,12,0.10)"
  border-strong: "#63615e"
  accent: "#0f766e"
  sign: "#e3ece6"
  on-sign: "#0f766e"
  primary: "#0d0d0c"
  on-primary: "#fbf6ef"
  danger: "#b42318"
  success: "#1f7a4d"
  warning: "#c2410c"
  state-taken: "#1f7a4d"
  state-due: "#c2410c"
  state-missed: "#b42318"
  state-skipped: "#5f5b57"
  on-state: "#fbf6ef"
  dark-bg: "#083b38"
  dark-surface-raised: "#1b4a46"
  dark-text: "#f7f2ec"
  dark-text-muted: "#e2d7cc"
  dark-accent: "#7fe0d4"
  dark-primary: "#f7f2ec"
  dark-taken: "#8fe3b0"
  dark-due: "#ffb08a"
  dark-missed: "#ff8a70"
  panel-morning: "#f2ecdf → #f4ebc6, brillo rgb(246,222,104)"
  panel-afternoon: "#f8dcc7 → #f9cba9, brillo rgb(249,150,84)"
  panel-night: "#f0e4e6 → #ecd6e2, brillo rgb(222,150,200)"
  panel-complete: "#e9ebe0 → #dfe9d4, brillo rgb(150,206,150)"
typography:
  family: "'Inter Tight' variable (autoalojada, latín), system-ui"
  display-xl:
    {
      fontSize: "4.5rem",
      fontWeight: 500,
      lineHeight: 0.98,
      letterSpacing: "-0.03em",
    }
  display-lg:
    {
      fontSize: "3rem",
      fontWeight: 500,
      lineHeight: 1.02,
      letterSpacing: "-0.03em",
    }
  display:
    {
      fontSize: "2rem",
      fontWeight: 500,
      lineHeight: 1.1,
      letterSpacing: "-0.03em",
    }
  title: { fontSize: "1.5rem", fontWeight: 500, lineHeight: 1.2 }
  subtitle: { fontSize: "1.25rem", fontWeight: 500, lineHeight: 1.3 }
  body: { fontSize: "1.0625rem", fontWeight: 400, lineHeight: 1.5 }
  caption: { fontSize: "0.9375rem", fontWeight: 400, lineHeight: 1.4 }
  label: { fontSize: "0.875rem", fontWeight: 500, lineHeight: 1.3 }
rounded:
  card: "20px"
  chip: "14px"
  button: "12px"
spacing:
  scale: "4 / 8 / 12 / 16 / 24 / 32 / 48"
  touch: "48px"
  control: "56px"
  control-sm: "48px"
  sidebar: "256px"
  content: "1152px"
  form: "440px"
  band: "120px"
---

# Design System: Recuerda

## Overview

**North Star: «El día en franjas de luz».** El panel web se ordena como el día del paciente:
mañana, tarde y noche, cada una con su luz. La tarjeta principal de Hoy se pinta con el panel de la
franja en curso (Completo, verde, cuando ya se tomó todo) y responde en 3 segundos cuántas dosis van
y cuál es la próxima. Todo lo demás es tranquilo: crema cálida, tinta casi negra, tarjetas blancas
con una sola sombra suave y filetes finos.

Dos temas con la misma estructura, por `html[data-theme]`: **claro cálido** (por defecto si el
sistema no pide oscuro) y **oscuro petróleo**, la paleta de la esfera del reloj. El selector
Sistema / Claro / Oscuro está en la cabecera, en «Más» y en la entrada; la elección se guarda en
`localStorage` y un script en `index.html` la aplica antes de pintar.

Los usuarios son mayores y cuidadores que miran desde el móvil: texto base de 17 px, nada por debajo
de 14 px, controles de 48–56 px, ningún movimiento continuo en el panel.

## Colors

Los valores viven en `web/src/design/tokens/tokens.css`; ningún color literal fuera de ahí.

**Tres capas, como en el diseño atómico:**

1. **Primitivos** (`--p-*` en `:root`): la paleta con nombre propio (`--p-petrol-900`, `--p-cream`,
   `--p-mint`…). Es la única capa con hex. El reloj tiene la misma paleta, con los mismos nombres y
   valores, en `Palette` (`wear/…/ui/theme/Tokens.kt`).
2. **Papeles semánticos** (`--bg`, `--text-muted`, `--state-taken`… en cada tema): solo
   referencian primitivos con `var()`; las transparencias salen del primitivo con `color-mix`.
   En el reloj son `AppColors` y `BwBackgrounds`.
3. **Componentes**: solo usan papeles (`bg-surface`, `text-text-muted`, `BwTheme.colors.content`),
   nunca un primitivo. ESLint lo exige en la web; en el reloj, los componentes solo leen `BwTheme`.

Para cambiar un color se cambia su primitivo una vez. Dos pruebas vigilan las copias que no se
pueden evitar: los hex de medicamento de `tokens/index.ts` (web) y `res/values/colors.xml` (reloj).

| Papel                             | Claro                     | Oscuro                | Contraste                 |
| --------------------------------- | ------------------------- | --------------------- | ------------------------- |
| Fondo                             | `#fbf6ef`                 | `#083b38`             | —                         |
| Tarjeta                           | `#fffdfa`                 | `#1b4a46`             | —                         |
| Texto                             | `#0d0d0c`                 | `#f7f2ec`             | 18:1 · 11:1               |
| Texto suave                       | `#63615e` (tinta al 64 %) | `#e2d7cc`             | 5,7:1 · 7:1 sobre tarjeta |
| Acción principal                  | tinta con crema           | crema con tinta       | 18:1 · 16:1               |
| Aguamarina (foco, enlaces, ahora) | `#0f766e`                 | `#7fe0d4`             | 5,1:1 · 6,4:1             |
| Tomada                            | `#1f7a4d`                 | `#8fe3b0`             | 5:1 · 6,5:1               |
| Pendiente (alarma)                | `#c2410c`                 | `#ffb08a`             | 4,8:1 · 5,6:1             |
| No tomada / error                 | `#b42318`                 | `#ff8a70` / `#ffa08a` | 6,1:1 · 5:1               |
| Omitida                           | `#5f5b57`                 | `#c9cfcd`             | 6,3:1 · 6,3:1             |

Los chips de estado son un relleno pleno con crema encima (claro) o tinta encima (oscuro).

**Paneles por franja:** degradado vertical y dos brillos radiales de su tono (tabla del
frontmatter). En oscuro, los mismos brillos con poca opacidad sobre el degradado petróleo. Encima,
siempre tinta (claro, ≥ 10:1) o crema (oscuro, ≥ 6:1); el texto suave, tinta al 74 % (≥ 5,9:1).
El grano de ruido y la deriva lenta de los brillos solo en la landing y la entrada; en el panel, el
degradado está quieto.

**El reloj de muestra** es siempre la esfera petróleo del reloj real (`--watch-*`), en los dos temas.

**Paleta de medicamentos:** ocho tonos profundos, los mismos que el reloj; la placa del medicamento
siempre va con su nombre (The Color Never Alone Rule).

## Typography

Inter Tight variable, autoalojada en `web/public/fonts` (solo latín, OFL). 500 en titulares, cifras
y controles; 400 en texto. Cifras tabulares en todo el panel. `font-bold` y `font-extrabold` son
alias de 500 para que ninguna pantalla pinte más fuerte que el sistema.

## Layout

- Móvil (< 640): barra superior (título, segunda línea con persona y fecha, tema compacto, salir) y
  barra inferior de pestañas con indicador en aguamarina suave.
- Tablet: barra lateral estrecha (pictograma sobre la palabra). Escritorio: barra lateral de 256 px;
  la opción activa es una tarjeta elevada.
- **Hoy:** tarjeta principal (8 columnas) con anillo «5 de 8 / dosis de hoy», «Próxima 14:00» y
  «Marcar tomada»; a su lado el reloj (batería, última conexión y aviso naranja si lleva más de 3 h
  sin conexión o tiene menos del 20 %) y la semana (porcentaje y un punto por día); debajo, las tres
  franjas con sus filas y las acciones visibles. En el móvil: principal, reloj, franjas, semana.
- **Entrada:** dividida en escritorio (panel de franja y formulario de 440 px); en el móvil, banda
  de 120 px arriba.

## Components

- **Button:** principal 56 px, relleno pleno; secundario 48 px con borde; `quiet` para acciones de
  cabecera y Deshacer; `danger`. Esquina 12 px.
- **Badge:** chip de estado de esquina 14 px con pictograma y palabra.
- **Input / PasswordInput:** 56 px, borde fino, etiqueta visible encima; la contraseña con
  «Mostrar» / «Ocultar»; el error con icono y texto.
- **Plate:** placa de pictograma (aguamarina suave, sobre panel, de marca en tinta, franjas).
- **Ring:** anillo del día; decorativo, la cifra va en palabras.
- **ThemeSwitch:** grupo de radios nativo Sistema / Claro / Oscuro.
- **Block:** tarjeta con pictograma, título, la pregunta que responde y el dato.

## Reloj

El reloj (`wear/`) usa la misma paleta: esferas a sangre con el degradado tonal de su papel
(petróleo `#083b38` → `#0b605a`, alarma `#3f1408` → `#b8401c`, tomada `#0d3a1f` → `#2a7a35`), con el
brillo de la franja del momento en el borde de Inicio y Hoy,
aguamarina `#7fe0d4`, claro `#f7f2ec`, tinta `#1b1613`; estados en tonos claros con tinta encima.
Sus tokens viven en `wear/…/ui/theme/Tokens.kt`.

## Do's and Don'ts

- **Do** acompañar cada color con su palabra y su pictograma.
- **Do** usar las palabras de estado del contrato: Tomada, Pendiente, No tomada, Omitida y Más tarde.
- **Do** resolver cada pantalla con datos en cargando, vacío, error con reintento y éxito.
- **Don't** escribir colores, tamaños ni fuentes literales fuera de `src/design/tokens` (ESLint).
- **Don't** mover nada en el panel: el grano y la deriva son de la landing y la entrada.
- **Don't** inventar datos: ni avisos con contador, ni estados que la API no tiene, ni cifras.
