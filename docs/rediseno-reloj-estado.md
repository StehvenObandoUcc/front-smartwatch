# Rediseño: estado y siguientes pasos

Al 3 de noviembre de 2026. `main` está limpio y no hay PR abiertos. La historia detallada está en los PR #29 a #69; aquí solo lo vigente.

## Sistema visual vigente

- **Reloj (`wear/`)**: «esferas a sangre», cuatro colores (`ui/theme/Tokens.kt`): petróleo `#083B38` de fondo en degradado tonal, aguamarina para arcos, «ahora» y pendiente, naranja rojizo para la alarma, verde para «Tomada». Texto crema `#F7F2EC`, acción principal en relleno crema con tinta, vidrio claro para superficies. Sin negro ni blanco puros, un solo tema oscuro.
- **Web (`web/`)**: los mismos colores del reloj (#62), un solo tema oscuro; cabecera y menú inferior de vidrio, filo claro en la elevación y botones que responden al pulsar (#63). Títulos sin guion automático.
- Documentos de diseño: `PRODUCT.md` y `DESIGN.md` en la raíz, `wear/PRODUCT.md`. Mesa de diseño (artifact con las decisiones) y concepto de referencia: ver `docs/producto-recuerda`.

## Hecho

- Reloj: alerta con Tomada / Posponer / Omitir, Inicio en 3 páginas (Próxima, Hoy, Adherencia), adherencia del mes e historial de 7 días, Tile «Anillo del día», modo local sin cuenta, vinculación por pasos y desvinculación, deshacer «Tomada» (subida retrasada 10 s), asistente por voz en español, batería informada al backend.
- Web: landing en `/`, login dividido con `WatchMock`, panel bento (Hoy con anillo, Franjas con «Marcar», Adherencia, Reloj con batería real, Avisos), medicamentos y horarios, agenda, historial, ajustes, deshacer con `DELETE` real.
- Contrato 0.10.0 integrado en ambos clientes (#61); la web va con el 0.11.0 (#65).
- Eliminar la cuenta desde Ajustes (#65), con contraseña, confirmación y aviso en el login.
- Reloj: limpia plan, alarmas y eventos cuando la sesión se pierde desde fuera (#66) y complication «Próxima toma» (#67).
- Storybook: todos los átomos, moléculas y plantillas tienen historia (#68).
- E2E de humo con Playwright en CI, móvil y escritorio, con la API simulada (#69).
- Backend en Heroku (v21, d0a2fdc): migraciones 0011 y 0012 aplicadas en la fase release; `TRUSTED_PROXY_HOPS=2` puesto. Los pedidos al backend de esta etapa están resueltos.

## Pendiente

Alcance recortado el 3 de noviembre de 2026: la app no se publica en Play Store. Solo queda lo que hace falta para que funcione bien:

1. **Validación en emulador o reloj** (Steve): ver `docs/validacion-pendiente.md`.
2. **Arreglos que salgan de esa validación.**
3. **Diseño de la web**: se retoma aparte.

Fuera del alcance: Play Store, política de privacidad, FCM y Firebase (basta la sincronización periódica), cobertura alta, E2E amplias, esfera propia (watch face) y cuenta de prueba en Heroku.

## Reglas acordadas

- Commits convencionales, un archivo por commit, con la identidad de Steve y sin co-autor ni firmas de IA.
- Una rama y un PR por tarea; se fusionan con CI en verde. Push y PR de cambios fuera del plan, con confirmación.
- No se abre el emulador: lo prueba Steve al cierre de cada sprint.
- En local solo lint y pruebas rápidas (`./gradlew ktlintCheck detekt testDebugUnitTest`; web: `eslint`, `tsc --noEmit`, `vitest run --project unit`).
- Máquina con poca RAM: Gradle con `--no-daemon` y `-Xmx1200m`.
