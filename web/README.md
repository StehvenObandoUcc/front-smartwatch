# Panel web

Panel responsive para cargar medicamentos y ver reportes. React + Vite + TypeScript strict, Tailwind CSS con tokens propios y Storybook.

## Comandos

| Comando                                        | Qué hace                                                                                      |
| ---------------------------------------------- | --------------------------------------------------------------------------------------------- |
| `npm run dev`                                  | Servidor de desarrollo                                                                        |
| `npm run storybook`                            | Storybook en `http://localhost:6006` (viewports Móvil, Tablet y Escritorio en la barra)       |
| `npm run lint`                                 | ESLint (incluye la regla que prohíbe valores visuales literales fuera de `src/design/tokens`) |
| `npm run typecheck`                            | `tsc -b`                                                                                      |
| `npm test`                                     | Vitest: pruebas unitarias (jsdom) + cada historia como prueba en Chromium con revisión axe    |
| `npm run test:unit` / `npm run test:storybook` | Cada proyecto por separado                                                                    |
| `npm run format`                               | Prettier                                                                                      |

La primera vez: `npx playwright install chromium` para las pruebas de historias.

## Estructura

```
src/design/tokens        tokens.css (Tailwind @theme) + index.ts (breakpoints, paleta de medicamentos)
src/components/atoms     Button, Text, Input, Badge, ColorDot, Icon, Spinner
src/components/templates AppShell (menú inferior en móvil, lateral en tablet/escritorio)
src/app                  router y navegación
src/pages                pantallas = template + organismos
```

Tailwind solo expone las escalas de los tokens: las paletas y tamaños por defecto están desactivados.
