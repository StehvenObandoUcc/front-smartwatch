<div align="center">

<img src="docs/assets/logo.svg" alt="Logo de Recuerda" width="120" />

# Recuerda · front-smartwatch

**Un reloj Wear OS que avisa a la hora de cada medicamento, y un panel web para que quien cuida
sepa si se tomó.**

![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white)
![Wear OS](https://img.shields.io/badge/Wear_OS-Compose-4285F4?logo=wearos&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-strict-3178C6?logo=typescript&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4-06B6D4?logo=tailwindcss&logoColor=white)

</div>

## Contenido

1. [Qué es](#qué-es)
2. [Cómo funciona](#cómo-funciona)
3. [El reloj](#el-reloj-wear)
4. [El panel web](#el-panel-web-web)
5. [Tecnologías](#tecnologías)
6. [Diseño](#diseño)
7. [La API y el contrato](#la-api-y-el-contrato)
8. [Puesta en marcha](#puesta-en-marcha)
9. [Pruebas y CI](#pruebas-y-ci)
10. [Despliegue](#despliegue)
11. [Estructura](#estructura)

## Qué es

Recuerda es un sistema de recordatorios de medicamentos para personas que deben tomar medicación a
sus horas y para quien las cuida. Tiene dos clientes en este repositorio y **no hay app de
teléfono**:

- **`wear/`**: app **Wear OS independiente**, el producto principal. Suena a la hora de cada dosis,
  deja marcarla como tomada, posponerla u omitirla y **funciona sin internet**.
- **`web/`**: **panel web** responsive donde el paciente o su cuidador carga medicamentos y
  horarios, ve dosis por dosis si se tomaron, revisa la adherencia y los reportes, y vincula el
  reloj.

Los dos hablan con la API de [`back-smartwatch`](https://github.com/StehvenObandoUcc/back-smartwatch)
(FastAPI, en otro repositorio). Están en **español o inglés**.

## Cómo funciona

```mermaid
flowchart LR
    subgraph Reloj["wear/ (Wear OS)"]
        UI["Compose UI"] --> Repo["Repositorios"]
        Repo --> Room[("Room: plan de 7 días<br/>y eventos")]
        Alarm["AlarmManager<br/>alarmas exactas"] --> UI
        Sync["WorkManager<br/>plan y subida de eventos"] --> Repo
    end
    subgraph Web["web/ (React)"]
        Pages["Páginas"] --> Query["TanStack Query<br/>cliente Orval"]
    end
    Sync -->|REST + token del reloj| API["back-smartwatch"]
    Query -->|REST + JWT, refresh en cookie httpOnly| API
```

Una dosis de punta a punta:

1. En el panel web se carga un medicamento con su horario y se vincula el reloj escribiendo el
   código corto que el reloj muestra (sin teclado en el reloj).
2. El reloj descarga el plan de 7 días (`GET /devices/me/plan`, con `ETag`) cada 15 minutos y al
   abrirse, lo guarda en Room y programa una **alarma exacta** por dosis.
3. A la hora suena la alerta a pantalla completa: **Tomada**, **Posponer** (10 min, hasta 3 veces)
   u **Omitir** (con confirmación). La acción se guarda al momento en Room con un `eventId` único.
4. Con conexión, WorkManager sube los eventos en lote (`POST /devices/me/dose-events`); el backend
   es idempotente, así que reintentar es seguro.
5. En el panel, Hoy y el historial muestran cada dosis y el porcentaje de adherencia; si una dosis
   no se registra, el backend avisa por Telegram o correo.

Sin internet el reloj sigue sonando: el plan y las alarmas son locales.

## El reloj (`wear/`)

Capturas reales tomadas en un Galaxy Watch4 con `adb exec-out screencap`.

**Inicio.** Muestra en grande la próxima dosis («En 3 min», «En 1 h») y, por el borde, el anillo del
día: un tramo por dosis, verde si se tomó, con un hueco arriba para la hora. El fondo toma el
brillo de la franja del momento: mañana, tarde o noche. Las páginas se pasan deslizando hacia los
lados.

<p align="center"><img src="docs/assets/capturas/reloj/es-inicio.webp" width="200" alt="Inicio con la próxima dosis" /></p>

**Alerta de dosis.** A la hora exacta suena a pantalla completa, aunque no haya internet. Tiene
tres acciones: **Tomada**, **Posponer** 10 minutos (hasta 3 veces) y **Omitir** (con
confirmación). Usa alarmas exactas (`setAlarmClock()` con el permiso `USE_EXACT_ALARM`, que se
concede al instalar) y se reprograman tras reiniciar el reloj o actualizar la app.

<p align="center"><img src="docs/assets/capturas/reloj/es-alerta.webp" width="200" alt="Alerta de dosis" /> <img src="docs/assets/capturas/reloj/en-alerta.webp" width="200" alt="Dose alert" /> <img src="docs/assets/capturas/reloj/en-tomada.webp" width="200" alt="Dose recorded as taken" /></p>

**Hoy.** Las dosis del día agrupadas por franja, cada una con su estado: pendiente, tomada, omitida
o más tarde.

<p align="center"><img src="docs/assets/capturas/reloj/es-hoy.webp" width="200" alt="Hoy" /> <img src="docs/assets/capturas/reloj/es-hoy-2.webp" width="200" alt="Hoy, más abajo" /></p>

**Mes.** El porcentaje de dosis tomadas en el mes, en grande, y los últimos 7 días. Solo aparece
con el reloj vinculado, porque ese dato lo calcula el backend.

<p align="center"><img src="docs/assets/capturas/reloj/es-mes.webp" width="200" alt="Mes" /></p>

**Historial.** Las últimas dosis registradas, día por día.

<p align="center"><img src="docs/assets/capturas/reloj/es-historial.webp" width="200" alt="Historial" /></p>

**Asistente.** Se le pregunta hablando o escribiendo, y responde en pantalla y en voz alta con
3 frases como mucho. Puede proponer un medicamento nuevo o una hora nueva para uno que ya existe,
pero solo los crea si se confirma. No sustituye al médico.

<p align="center"><img src="docs/assets/capturas/reloj/es-asistente.webp" width="200" alt="Asistente" /> <img src="docs/assets/capturas/reloj/es-asistente-respuesta.webp" width="200" alt="Respuesta del asistente" /></p>

**Reloj.** Muestra el estado de la vinculación y permite desvincular, con confirmación. Si el
reloj no está vinculado, aquí se ven y se crean los medicamentos guardados en él.

<p align="center"><img src="docs/assets/capturas/reloj/es-reloj.webp" width="200" alt="Página Reloj" /> <img src="docs/assets/capturas/reloj/es-desvincular.webp" width="200" alt="Confirmar desvinculación" /></p>

**Bienvenida y vinculación.** El reloj se puede usar de dos maneras:

- solo, con los medicamentos creados en el propio reloj, que suenan sin cuenta ni internet;
- vinculado al panel web, escribiendo en el panel (*Ajustes → Vincular reloj*) el código corto que
  muestra el reloj.

<p align="center"><img src="docs/assets/capturas/reloj/es-bienvenida.webp" width="200" alt="Bienvenida" /> <img src="docs/assets/capturas/reloj/es-bienvenida-2.webp" width="200" alt="Bienvenida, segunda opción" /> <img src="docs/assets/capturas/reloj/es-vincular.webp" width="200" alt="Código para vincular" /></p>

**Además:**

- un Tile «Anillo del día» y una complication «Próxima toma» para la esfera;
- **idioma** español (por defecto) o inglés, que se elige solo para la app en *Ajustes → Apps →
  Recordatorios → Idioma*.

<p align="center"><img src="docs/assets/capturas/reloj/en-inicio.webp" width="200" alt="Home" /> <img src="docs/assets/capturas/reloj/en-hoy.webp" width="200" alt="Today" /> <img src="docs/assets/capturas/reloj/en-mes.webp" width="200" alt="Month" /> <img src="docs/assets/capturas/reloj/en-historial.webp" width="200" alt="History" /></p>
<p align="center"><img src="docs/assets/capturas/reloj/en-asistente.webp" width="200" alt="Assistant" /> <img src="docs/assets/capturas/reloj/en-reloj.webp" width="200" alt="Watch" /></p>

## El panel web (`web/`)

Capturas reales de la web desplegada (<https://front-smartwatch.vercel.app>), tomadas con
Playwright y una cuenta de demostración. Cada pantalla aparece en escritorio (1440 px) y en móvil
(390 px). Al final de la sección están las versiones en tema oscuro y en inglés.

**Landing.** Explica qué es Recuerda. El titular cambia de palabra (se puede pausar) y hay una
vista del reloj. Desde aquí se elige el idioma y el tema.

<p align="center"><img src="docs/assets/capturas/web/landing-escritorio-light-es.webp" width="600" alt="Landing en escritorio" /> <img src="docs/assets/capturas/web/landing-movil-light-es.webp" width="210" alt="Landing en el móvil" /></p>

**Entrar y crear cuenta.** La cuenta puede ser para uno mismo o de cuidador. El correo se verifica
con un enlace y, mientras tanto, un aviso lo recuerda. La contraseña se recupera por correo.

<p align="center"><img src="docs/assets/capturas/web/login-escritorio-light-es.webp" width="600" alt="Iniciar sesión en escritorio" /> <img src="docs/assets/capturas/web/login-movil-light-es.webp" width="210" alt="Iniciar sesión en el móvil" /></p>
<p align="center"><img src="docs/assets/capturas/web/registro-escritorio-light-es.webp" width="600" alt="Crear cuenta en escritorio" /> <img src="docs/assets/capturas/web/registro-movil-light-es.webp" width="210" alt="Crear cuenta en el móvil" /></p>

**Hoy.** Muestra:

- el anillo de dosis tomadas;
- la dosis que toca ahora, con «Marcar tomada» y «Omitir»;
- el estado del reloj (batería y última conexión);
- la semana, día por día;
- las dosis del día por franja, con la opción de deshacer.

<p align="center"><img src="docs/assets/capturas/web/hoy-escritorio-light-es.webp" width="600" alt="Hoy en escritorio" /> <img src="docs/assets/capturas/web/hoy-movil-light-es.webp" width="210" alt="Hoy en el móvil" /></p>

**Medicinas.** Los medicamentos activos, cada uno con su dosis, sus horas, sus días y un color para
reconocerlo. Aquí se agregan, se editan y se quitan.

<p align="center"><img src="docs/assets/capturas/web/medicamentos-escritorio-light-es.webp" width="600" alt="Medicinas en escritorio" /> <img src="docs/assets/capturas/web/medicamentos-movil-light-es.webp" width="210" alt="Medicinas en el móvil" /></p>

**Agenda.** Las dosis de los próximos días. Cada una se puede marcar como tomada u omitida, o
deshacer lo marcado.

<p align="center"><img src="docs/assets/capturas/web/agenda-escritorio-light-es.webp" width="600" alt="Agenda en escritorio" /> <img src="docs/assets/capturas/web/agenda-movil-light-es.webp" width="210" alt="Agenda en el móvil" /></p>

**Historial.** La adherencia de los últimos 7 o 30 días: el porcentaje, las dosis tomadas,
omitidas y no tomadas, y una gráfica día a día con su tabla equivalente.

<p align="center"><img src="docs/assets/capturas/web/adherencia-escritorio-light-es.webp" width="600" alt="Historial en escritorio" /> <img src="docs/assets/capturas/web/adherencia-movil-light-es.webp" width="210" alt="Historial en el móvil" /></p>

**Reportes.** Cada lunes se genera un resumen semanal, y también se puede pedir uno en cualquier
momento. Cada reporte trae la adherencia por medicamento y se descarga en PDF.

<p align="center"><img src="docs/assets/capturas/web/reportes-escritorio-light-es.webp" width="600" alt="Reportes en escritorio" /> <img src="docs/assets/capturas/web/reportes-movil-light-es.webp" width="210" alt="Reportes en el móvil" /></p>
<p align="center"><img src="docs/assets/capturas/web/reporte-escritorio-light-es.webp" width="600" alt="Detalle de un reporte en escritorio" /> <img src="docs/assets/capturas/web/reporte-movil-light-es.webp" width="210" alt="Detalle de un reporte en el móvil" /></p>

**Asistente.** Responde preguntas sobre el plan de medicamentos, escribiendo la respuesta a medida
que llega. Pide un consentimiento aparte, tiene un límite diario de mensajes y no guarda la
conversación.

<p align="center"><img src="docs/assets/capturas/web/asistente-escritorio-light-es.webp" width="600" alt="Asistente en escritorio" /> <img src="docs/assets/capturas/web/asistente-movil-light-es.webp" width="210" alt="Asistente en el móvil" /></p>

**Avisos.** Aquí se eligen el canal (correo o Telegram) y los avisos que se quieren recibir, como
una dosis no tomada o el reporte semanal.

<p align="center"><img src="docs/assets/capturas/web/avisos-escritorio-light-es.webp" width="600" alt="Avisos en escritorio" /> <img src="docs/assets/capturas/web/avisos-movil-light-es.webp" width="210" alt="Avisos en el móvil" /></p>

**Ajustes.** Reúne:

- la cuenta;
- el idioma, que se guarda en la cuenta para que los correos y avisos lleguen en ese idioma;
- la vinculación del reloj, con el código que muestra;
- los cuidadores y sus invitaciones;
- los consentimientos;
- eliminar la cuenta.

<p align="center"><img src="docs/assets/capturas/web/ajustes-escritorio-light-es.webp" width="600" alt="Ajustes en escritorio" /> <img src="docs/assets/capturas/web/ajustes-movil-light-es.webp" width="210" alt="Ajustes en el móvil" /></p>

**Más (móvil).** En el móvil, la barra inferior lleva a Hoy, Medicinas, Historial y Más. Desde Más
se llega al resto de las pantallas y al tema.

<p align="center"><img src="docs/assets/capturas/web/mas-movil-light-es.webp" width="210" alt="Más en el móvil" /> <img src="docs/assets/capturas/web/mas-movil-dark-es.webp" width="210" alt="Más en el móvil, tema oscuro" /></p>

El tema, claro u oscuro, sigue al del sistema o se elige a mano. Ese tema y el idioma (español o
inglés) se recuerdan en el navegador.

<details>
<summary><b>Tema oscuro</b></summary>

<p align="center"><img src="docs/assets/capturas/web/landing-escritorio-dark-es.webp" width="600" alt="Landing" /> <img src="docs/assets/capturas/web/landing-movil-dark-es.webp" width="210" alt="Landing" /></p>

<p align="center"><img src="docs/assets/capturas/web/login-escritorio-dark-es.webp" width="600" alt="Iniciar sesión" /> <img src="docs/assets/capturas/web/login-movil-dark-es.webp" width="210" alt="Iniciar sesión" /></p>

<p align="center"><img src="docs/assets/capturas/web/registro-escritorio-dark-es.webp" width="600" alt="Crear cuenta" /> <img src="docs/assets/capturas/web/registro-movil-dark-es.webp" width="210" alt="Crear cuenta" /></p>

<p align="center"><img src="docs/assets/capturas/web/hoy-escritorio-dark-es.webp" width="600" alt="Hoy" /> <img src="docs/assets/capturas/web/hoy-movil-dark-es.webp" width="210" alt="Hoy" /></p>

<p align="center"><img src="docs/assets/capturas/web/medicamentos-escritorio-dark-es.webp" width="600" alt="Medicinas" /> <img src="docs/assets/capturas/web/medicamentos-movil-dark-es.webp" width="210" alt="Medicinas" /></p>

<p align="center"><img src="docs/assets/capturas/web/agenda-escritorio-dark-es.webp" width="600" alt="Agenda" /> <img src="docs/assets/capturas/web/agenda-movil-dark-es.webp" width="210" alt="Agenda" /></p>

<p align="center"><img src="docs/assets/capturas/web/adherencia-escritorio-dark-es.webp" width="600" alt="Historial" /> <img src="docs/assets/capturas/web/adherencia-movil-dark-es.webp" width="210" alt="Historial" /></p>

<p align="center"><img src="docs/assets/capturas/web/reportes-escritorio-dark-es.webp" width="600" alt="Reportes" /> <img src="docs/assets/capturas/web/reportes-movil-dark-es.webp" width="210" alt="Reportes" /></p>

<p align="center"><img src="docs/assets/capturas/web/reporte-escritorio-dark-es.webp" width="600" alt="Reporte" /> <img src="docs/assets/capturas/web/reporte-movil-dark-es.webp" width="210" alt="Reporte" /></p>

<p align="center"><img src="docs/assets/capturas/web/asistente-escritorio-dark-es.webp" width="600" alt="Asistente" /> <img src="docs/assets/capturas/web/asistente-movil-dark-es.webp" width="210" alt="Asistente" /></p>

<p align="center"><img src="docs/assets/capturas/web/avisos-escritorio-dark-es.webp" width="600" alt="Avisos" /> <img src="docs/assets/capturas/web/avisos-movil-dark-es.webp" width="210" alt="Avisos" /></p>

<p align="center"><img src="docs/assets/capturas/web/ajustes-escritorio-dark-es.webp" width="600" alt="Ajustes" /> <img src="docs/assets/capturas/web/ajustes-movil-dark-es.webp" width="210" alt="Ajustes" /></p>

</details>

<details>
<summary><b>In English · light</b></summary>

<p align="center"><img src="docs/assets/capturas/web/landing-escritorio-light-en.webp" width="600" alt="Landing" /> <img src="docs/assets/capturas/web/landing-movil-light-en.webp" width="210" alt="Landing" /></p>

<p align="center"><img src="docs/assets/capturas/web/login-escritorio-light-en.webp" width="600" alt="Iniciar sesión" /> <img src="docs/assets/capturas/web/login-movil-light-en.webp" width="210" alt="Iniciar sesión" /></p>

<p align="center"><img src="docs/assets/capturas/web/registro-escritorio-light-en.webp" width="600" alt="Crear cuenta" /> <img src="docs/assets/capturas/web/registro-movil-light-en.webp" width="210" alt="Crear cuenta" /></p>

<p align="center"><img src="docs/assets/capturas/web/hoy-escritorio-light-en.webp" width="600" alt="Hoy" /> <img src="docs/assets/capturas/web/hoy-movil-light-en.webp" width="210" alt="Hoy" /></p>

<p align="center"><img src="docs/assets/capturas/web/medicamentos-escritorio-light-en.webp" width="600" alt="Medicinas" /> <img src="docs/assets/capturas/web/medicamentos-movil-light-en.webp" width="210" alt="Medicinas" /></p>

<p align="center"><img src="docs/assets/capturas/web/agenda-escritorio-light-en.webp" width="600" alt="Agenda" /> <img src="docs/assets/capturas/web/agenda-movil-light-en.webp" width="210" alt="Agenda" /></p>

<p align="center"><img src="docs/assets/capturas/web/adherencia-escritorio-light-en.webp" width="600" alt="Historial" /> <img src="docs/assets/capturas/web/adherencia-movil-light-en.webp" width="210" alt="Historial" /></p>

<p align="center"><img src="docs/assets/capturas/web/reportes-escritorio-light-en.webp" width="600" alt="Reportes" /> <img src="docs/assets/capturas/web/reportes-movil-light-en.webp" width="210" alt="Reportes" /></p>

<p align="center"><img src="docs/assets/capturas/web/reporte-escritorio-light-en.webp" width="600" alt="Reporte" /> <img src="docs/assets/capturas/web/reporte-movil-light-en.webp" width="210" alt="Reporte" /></p>

<p align="center"><img src="docs/assets/capturas/web/asistente-escritorio-light-en.webp" width="600" alt="Asistente" /> <img src="docs/assets/capturas/web/asistente-movil-light-en.webp" width="210" alt="Asistente" /></p>

<p align="center"><img src="docs/assets/capturas/web/avisos-escritorio-light-en.webp" width="600" alt="Avisos" /> <img src="docs/assets/capturas/web/avisos-movil-light-en.webp" width="210" alt="Avisos" /></p>

<p align="center"><img src="docs/assets/capturas/web/ajustes-escritorio-light-en.webp" width="600" alt="Ajustes" /> <img src="docs/assets/capturas/web/ajustes-movil-light-en.webp" width="210" alt="Ajustes" /></p>

</details>

<details>
<summary><b>In English · dark</b></summary>

<p align="center"><img src="docs/assets/capturas/web/landing-escritorio-dark-en.webp" width="600" alt="Landing" /> <img src="docs/assets/capturas/web/landing-movil-dark-en.webp" width="210" alt="Landing" /></p>

<p align="center"><img src="docs/assets/capturas/web/login-escritorio-dark-en.webp" width="600" alt="Iniciar sesión" /> <img src="docs/assets/capturas/web/login-movil-dark-en.webp" width="210" alt="Iniciar sesión" /></p>

<p align="center"><img src="docs/assets/capturas/web/registro-escritorio-dark-en.webp" width="600" alt="Crear cuenta" /> <img src="docs/assets/capturas/web/registro-movil-dark-en.webp" width="210" alt="Crear cuenta" /></p>

<p align="center"><img src="docs/assets/capturas/web/hoy-escritorio-dark-en.webp" width="600" alt="Hoy" /> <img src="docs/assets/capturas/web/hoy-movil-dark-en.webp" width="210" alt="Hoy" /></p>

<p align="center"><img src="docs/assets/capturas/web/medicamentos-escritorio-dark-en.webp" width="600" alt="Medicinas" /> <img src="docs/assets/capturas/web/medicamentos-movil-dark-en.webp" width="210" alt="Medicinas" /></p>

<p align="center"><img src="docs/assets/capturas/web/agenda-escritorio-dark-en.webp" width="600" alt="Agenda" /> <img src="docs/assets/capturas/web/agenda-movil-dark-en.webp" width="210" alt="Agenda" /></p>

<p align="center"><img src="docs/assets/capturas/web/adherencia-escritorio-dark-en.webp" width="600" alt="Historial" /> <img src="docs/assets/capturas/web/adherencia-movil-dark-en.webp" width="210" alt="Historial" /></p>

<p align="center"><img src="docs/assets/capturas/web/reportes-escritorio-dark-en.webp" width="600" alt="Reportes" /> <img src="docs/assets/capturas/web/reportes-movil-dark-en.webp" width="210" alt="Reportes" /></p>

<p align="center"><img src="docs/assets/capturas/web/reporte-escritorio-dark-en.webp" width="600" alt="Reporte" /> <img src="docs/assets/capturas/web/reporte-movil-dark-en.webp" width="210" alt="Reporte" /></p>

<p align="center"><img src="docs/assets/capturas/web/asistente-escritorio-dark-en.webp" width="600" alt="Asistente" /> <img src="docs/assets/capturas/web/asistente-movil-dark-en.webp" width="210" alt="Asistente" /></p>

<p align="center"><img src="docs/assets/capturas/web/avisos-escritorio-dark-en.webp" width="600" alt="Avisos" /> <img src="docs/assets/capturas/web/avisos-movil-dark-en.webp" width="210" alt="Avisos" /></p>

<p align="center"><img src="docs/assets/capturas/web/ajustes-escritorio-dark-en.webp" width="600" alt="Ajustes" /> <img src="docs/assets/capturas/web/ajustes-movil-dark-en.webp" width="210" alt="Ajustes" /></p>

</details>

## Tecnologías

| Cliente | Para qué | Tecnología |
| --- | --- | --- |
| Reloj | Lenguaje y UI | Kotlin 2.4, Jetpack Compose for Wear OS 1.7 |
| Reloj | Datos locales | Room 2.8 (plan, eventos, medicamentos locales) |
| Reloj | Segundo plano | WorkManager 2.12, AlarmManager |
| Reloj | Red | Retrofit 3 + OkHttp 5 + kotlinx.serialization (cliente generado) |
| Reloj | Inyección | Hilt 2.60 |
| Reloj | Esfera | Tiles 1.6, Complications 1.3 |
| Reloj | Plataforma | minSdk 33 (Wear OS 4), targetSdk 37, AGP 9.4 |
| Reloj | Calidad | ktlint, detekt, JUnit, Robolectric 4.17, Truth |
| Web | Base | React 19, TypeScript 6 (strict), Vite 8 |
| Web | Estilos | Tailwind CSS 4 con tokens propios, fuente Inter Tight |
| Web | Datos | TanStack Query 5 con cliente y mocks MSW generados por Orval 8 |
| Web | Formularios | React Hook Form 7 + Zod 4 |
| Web | Rutas e idiomas | React Router 8, i18next 26 + react-i18next 17 |
| Web | Calidad | Vitest 5, Testing Library, Playwright 1.63, Storybook 10 (con axe), ESLint 9, Prettier 3 |
| CI | Integración | GitHub Actions: `.github/workflows/wear.yml` y `web.yml` |

## Diseño

- Paleta común a las dos apps: petróleo, crema, aguamarina, verde para «Tomada» y naranja para la
  alarma.
- Los colores van en **tres capas**:
  1. primitivos con nombre: `--p-*` en `web/src/design/tokens/tokens.css` y `Palette` en
     `wear/…/ui/theme/Tokens.kt`;
  2. papeles semánticos (`--bg`, `--state-taken`; `AppColors`, `BwBackgrounds`);
  3. componentes, que solo usan los papeles.
- Ningún componente lleva un color fijo: en la web lo exige ESLint. Dos pruebas vigilan las copias
  inevitables (`colors.xml` del reloj y los colores de medicamento de la web).
- Accesibilidad: WCAG 2.2 AA, texto base de 17 px en la web y 14 sp o más en el reloj, áreas
  táctiles de 48 px o más y estados con icono y texto, nunca solo color.

## La API y el contrato

- Los clientes **se generan** desde el contrato OpenAPI del backend: openapi-generator para el
  reloj (`wear/api/generated`) y Orval para la web (`web/src/api/generated*`). No se escriben a
  mano llamadas ni tipos de la API.
- `contract.lock` fija el commit del backend que se usa (hoy, el contrato **0.13.0**), y
  `scripts/sync-contract.sh` lo descarga a `.contract/openapi.yaml`.
- Para actualizar el contrato:
  1. Cambiar `contract.lock` al nuevo commit del backend.
  2. Ejecutar `bash scripts/sync-contract.sh`.
  3. Regenerar los clientes: `npx orval` en `web/` y `./gradlew :api:generateApi` en `wear/`.
- Autenticación:
  - **web**: JWT de acceso solo en memoria y refresh token en cookie `httpOnly` (Vercel reenvía
    `/auth/*` al backend para que sea de primera parte);
  - **reloj**: token del dispositivo, cifrado con el Android Keystore.
- Los errores llegan como `application/problem+json`; cada app traduce los códigos que conoce.

## Puesta en marcha

### Requisitos

- Node.js 24 y npm.
- JDK 17 o superior (sirve el de Android Studio) y Android SDK 37. El reloj necesita un
  `wear/local.properties` con `sdk.dir=...`, que no se versiona.
- [GitHub CLI](https://cli.github.com/) autenticado, para descargar el contrato.

```sh
git clone https://github.com/StehvenObandoUcc/front-smartwatch.git
cd front-smartwatch
bash scripts/sync-contract.sh
```

### Panel web

```sh
cd web
npm ci
npm run dev        # http://localhost:5173, contra la API desplegada
```

Por defecto usa la API de Heroku. Para una API local, crea `web/.env.local` con
`VITE_API_URL=http://localhost:8000`.

| Comando | Qué hace |
| --- | --- |
| `npm run dev` | Servidor de desarrollo |
| `npm run build` | Comprobación de tipos y build de producción |
| `npm run lint` · `npm run typecheck` · `npm run format:check` | ESLint, `tsc -b`, Prettier |
| `npm run test:unit` | Pruebas unitarias (Vitest + jsdom) |
| `npm test` | Unitarias más cada historia de Storybook en Chromium con axe |
| `npm run test:e2e` | E2E con Playwright sobre el build, con la API simulada |
| `npm run storybook` | Storybook en `http://localhost:6006` |

La primera vez, para las historias y las E2E: `npx playwright install chromium`.

### Reloj

```sh
cd wear
./gradlew ktlintCheck detekt testDebugUnitTest   # comprobaciones
./gradlew assembleRelease                        # APK optimizado en app/build/outputs/apk/release/
```

- **URL de la API**: la app apunta al backend desplegado (`apiBaseUrl` en
  `wear/gradle.properties`). Para el backend local en el emulador:
  `-PapiBaseUrl=http://10.0.2.2:8000/`.
- **Firma**: la release se firma con la clave de depuración, así que se instala encima de la
  versión anterior sin perder datos.

**Instalar en un reloj real por ADB inalámbrico:**

1. En el reloj, activa *Opciones de desarrollador → Depuración inalámbrica* y vincúlalo con
   `adb pair`.
2. Comprueba que aparece con `adb devices`.
3. Instala:
   ```sh
   adb push app/build/outputs/apk/release/app-release.apk /data/local/tmp/app.apk
   adb shell pm install -r /data/local/tmp/app.apk
   ```
   La instalación directa (`adb install`) a veces se queda colgada por wifi.

Comprobaciones útiles:
- `adb shell dumpsys alarm | grep recordatorios`: las alarmas programadas.
- `adb shell dumpsys package com.smartwatch.recordatorios | grep EXACT`: el permiso de alarma
  exacta.

Usa la versión **release** para probar: la de depuración pesa unos 50 MB y va mucho más lenta.

## Pruebas y CI

- **Reloj**: pruebas unitarias y de pantalla con Robolectric de alarmas, sincronización del plan,
  cola de eventos, desvinculación, asistente, formato de hora, idiomas y colores. Las pruebas de
  pantalla corren en español (`app/src/test/resources/robolectric.properties`).
- **Web**: pruebas unitarias de formularios, errores, idioma y estado de las dosis; historias de
  Storybook con axe; y E2E de humo (landing, login, panel, eliminar la cuenta y cambio a inglés).
- **CI** en cada PR:
  - `wear.yml`: ktlint, detekt, pruebas y build;
  - `web.yml`: lint, formato, tipos, pruebas, build, E2E y Storybook.

  Los cambios entran a `main` por PR con la CI en verde.

## Despliegue

- **Web**: Vercel, desde `main`. `web/vercel.json` reenvía `/auth/*` al backend, para que la cookie
  de sesión sea de primera parte. El resto de la API se llama directamente en Heroku.
- **Backend**: Heroku, desde su propio repositorio.
- **Reloj**: no se publica en Play Store; se instala como se explica arriba.

## Estructura

```
front-smartwatch/
├── wear/                               App Wear OS
│   ├── app/src/main/java/com/smartwatch/recordatorios/
│   │   ├── alarm/        Alarmas exactas, notificación de dosis, acciones y arranque
│   │   ├── complication/ Complication «Próxima toma»
│   │   ├── data/         local/ (Room) · remote/ (API, tokens, plan, eventos) · repository/
│   │   ├── di/           Módulos de Hilt
│   │   ├── sync/         WorkManager: plan y subida de eventos
│   │   ├── tile/         Tile «Anillo del día»
│   │   └── ui/           theme/ (tokens) · components/ · screens/
│   ├── app/src/main/res/ Textos (values/ en español, values-en/ en inglés), colores, fuente
│   └── api/generated/    Cliente generado desde el contrato
├── web/                                Panel web
│   ├── src/
│   │   ├── api/          Cliente y mocks MSW generados con Orval
│   │   ├── app/          Rutas, layout raíz y navegación
│   │   ├── components/   atoms/ · molecules/ · templates/
│   │   ├── design/tokens Tokens (primitivos y papeles)
│   │   ├── features/     Lógica por dominio (auth, doses, medications, patients, reports…)
│   │   ├── i18n/         Textos en es.json y en.json
│   │   ├── lib/          Cliente HTTP, fechas, errores, tema
│   │   └── pages/        Pantallas
│   ├── e2e/              Pruebas E2E con Playwright
│   └── .storybook/       Storybook
├── docs/assets/                         Logo y capturas/ (reloj/ y web/) que usa este README
├── scripts/sync-contract.sh            Descarga el contrato fijado
├── contract.lock                       Commit del contrato del backend
└── .github/workflows/                  CI del reloj y de la web
```

No hay archivo de licencia.
