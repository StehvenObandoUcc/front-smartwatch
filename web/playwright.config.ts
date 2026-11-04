import { defineConfig, devices } from '@playwright/test';

// E2E de humo sobre el build de producción (vite preview); la API se simula en cada prueba.
export default defineConfig({
  testDir: './e2e',
  forbidOnly: Boolean(process.env.CI),
  retries: process.env.CI ? 1 : 0,
  // Español, el idioma por defecto: el navegador de CI dice «en-US».
  use: { baseURL: 'http://127.0.0.1:4173', locale: 'es-CO', trace: 'retain-on-failure' },
  projects: [
    { name: 'movil', use: { ...devices['Pixel 7'] } },
    { name: 'escritorio', use: { ...devices['Desktop Chrome'] } },
  ],
  webServer: {
    command: 'npx vite preview --port 4173 --strictPort --host 127.0.0.1',
    url: 'http://127.0.0.1:4173',
    reuseExistingServer: !process.env.CI,
  },
});
