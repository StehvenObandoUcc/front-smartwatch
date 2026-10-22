# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

> Este archivo es la verdad compartida del producto y la del panel web (`web/`). El reloj (`wear/`)
> tiene su propio `wear/PRODUCT.md` con la plataforma `android`; todo lo de aquí le aplica también.

## Users

- **Paciente.** Adulto mayor o de mediana edad, con poca alfabetización digital. Usa sobre todo el
  reloj: mira cuánto falta para la próxima toma, recibe la alarma y marca Tomada, Posponer u Omitir.
  Puede tener cuenta en el panel web y gestionarse solo.
- **Cuidador familiar.** De mediana edad, entra al panel web a menudo desde el móvil. Carga
  medicamentos y horarios, revisa si cada dosis se tomó y recibe avisos por Telegram o correo.
- **Profesionales de salud** (médicos, enfermeras, farmaceutas). Cargan o revisan el tratamiento
  desde el panel web. *Decisión abierta:* el contrato del backend solo tiene los roles `patient` y
  `caregiver`; hoy un profesional entraría como cuidador. Un rol propio está por decidir.

## Product Purpose

Que el paciente tome cada medicamento a su hora y que quien lo cuida sepa si lo tomó. El éxito es
una adherencia alta, alarmas que siempre suenan y que el paciente use el reloj sin dudar ni pedir
ayuda.

## Positioning

Un reloj Wear OS independiente, sin app de teléfono, que funciona sin conexión: guarda el plan de 7
días y programa alarmas locales. También se puede usar solo en el reloj, sin cuenta ni internet,
creando los medicamentos ahí mismo. El panel web completa el ciclo: el cuidador o el profesional
carga el tratamiento y ve, dosis por dosis, si se tomó.

## Operating Context

- **Reloj:** alerta a pantalla completa con Tomada, Posponer (10 min, como máximo 3 veces) y Omitir
  con confirmación. Inicio dice cuánto falta para la próxima toma; Hoy lista las dosis del día.
  Vinculación sin teclado: el reloj muestra un código corto que se escribe en el panel web. Pregunta
  por voz al asistente (respuesta corta en pantalla y leída en voz alta).
- **Panel web:** pastillero del día por franjas (mañana, tarde, noche) con el estado de cada dosis,
  agenda de 7 días, adherencia, reportes semanales en PDF, avisos por Telegram y correo,
  consentimientos y vinculación del reloj.
- Uso familiar, en casa, con tono clínico: debe transmitir la seriedad de un servicio de salud sin
  depender de una clínica.

## Capabilities and Constraints

- Idioma: español (zona horaria America/Bogota, Colombia).
- Datos de salud bajo la Ley 1581 de 2012: consentimiento por finalidad (`health_data`, `ai_chat`,
  `notifications`), cifrado y borrado de cuenta.
- El backend es la fuente de verdad y su contrato OpenAPI manda; el frontend no lo edita.
- Asistente de IA (DeepSeek) por voz con guardarraíles y límite diario. Hoy no modifica el plan.
  *Decisión abierta:* que pueda agregar dosis con confirmación del usuario está pedido al backend y
  cambia el principio original de «la IA no modifica el plan».
- Reloj: Wear OS 4 o superior (API 33), pantallas redondas y cuadradas, solo Android.
- Sin notificaciones push todavía (FCM al cierre): el reloj sincroniza cada 15 min y cada 2 min con
  la app abierta.

## Brand Commitments

- **Nombre: Recuerda**, el mismo en el reloj y en el panel web. Elegido el 2026-10-22; falta
  verificar que el nombre esté disponible como marca y en la tienda.
- **Voz:** profesional y clínica, confiable, nunca infantil. Frases cortas en español claro, sin
  jerga: «No tomada» en lugar de «dosis vencida», «Quitar» en lugar de «Archivar».
- **Restricción pedida por el usuario:** minimalismo con iconos representativos para cada opción y
  cada categoría, con el mismo criterio en el panel web y en el reloj.
- El icono actual (disco azul #0B5CAD con una cápsula celeste y blanca) es la base existente; no es
  definitivo.

## Evidence on Hand

No hay testimonios, clientes, instituciones, métricas de uso ni estudios. Todo lo que se muestre
como prueba social o resultado debe ser real; no se inventa. Los únicos datos son de ejemplo y de
prueba.

## Product Principles

1. **La alarma siempre suena.** Nada de la interfaz puede ponerlo en riesgo: permisos guiados,
   funcionamiento sin red y avisos claros cuando algo lo impide.
2. **Un vistazo responde la pregunta.** En el reloj: ¿qué tomo y cuándo? En la web: ¿se lo tomó?
3. **Una acción principal por pantalla.** Lo que no se deshace pide confirmación y ofrece primero la
   opción segura.
4. **Nunca solo color.** Cada estado y cada medicamento llevan icono y texto.
5. **Un solo lenguaje.** Las mismas palabras, iconos y estados en el reloj y en el panel web.

## Accessibility & Inclusion

- WCAG 2.2 AA en el panel web; texto base de 17 px o más, áreas táctiles de 44 px o más y foco
  visible.
- Reloj: nada por debajo de 14 sp, el dato principal a 40 sp, botones de 48 dp o más, y la letra
  grande del sistema no debe cortar texto.
- Poca alfabetización digital: cada opción con icono y etiqueta, sin gestos ocultos como única vía,
  sin abreviaturas ni términos técnicos.
