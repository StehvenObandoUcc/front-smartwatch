import 'i18next';

import type es from './es.json';

// Las claves de t() se comprueban contra es.json: una clave que no existe no compila.
declare module 'i18next' {
  interface CustomTypeOptions {
    defaultNS: 'translation';
    resources: { translation: typeof es };
  }
}
