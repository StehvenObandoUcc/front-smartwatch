import '@testing-library/jest-dom/vitest';
import { cleanup } from '@testing-library/react';
import { afterEach, beforeEach } from 'vitest';

import { i18n } from '../i18n';

// Las pruebas afirman los textos en español, el idioma por defecto (jsdom dice «en-US»).
beforeEach(async () => {
  await i18n.changeLanguage('es');
});

afterEach(() => {
  cleanup();
});
