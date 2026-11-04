import i18n from 'i18next';
import { initReactI18next, useTranslation } from 'react-i18next';

import en from './en.json';
import es from './es.json';

export type Language = 'es' | 'en';

export const LANGUAGE_KEY = 'recuerda-idioma';

/** La elección guardada; si no hay, el idioma del navegador («en…» → inglés, lo demás español). */
export function detectLanguage(): Language {
  try {
    const saved = localStorage.getItem(LANGUAGE_KEY);
    if (saved === 'es' || saved === 'en') return saved;
  } catch {
    // Sin almacenamiento: se usa el del navegador.
  }
  return navigator.language?.toLowerCase().startsWith('en') ? 'en' : 'es';
}

/** Si en este dispositivo se eligió idioma a mano (si no, manda el de la cuenta al entrar). */
export function hasSavedLanguage(): boolean {
  try {
    const saved = localStorage.getItem(LANGUAGE_KEY);
    return saved === 'es' || saved === 'en';
  } catch {
    return false;
  }
}

/** Cambia el idioma, lo guarda y lo pone en <html lang>. */
export function setLanguage(language: Language) {
  try {
    localStorage.setItem(LANGUAGE_KEY, language);
  } catch {
    // Se queda solo para esta visita.
  }
  void i18n.changeLanguage(language);
}

i18n.on('languageChanged', (language) => {
  document.documentElement.lang = language;
});

void i18n.use(initReactI18next).init({
  resources: { es: { translation: es }, en: { translation: en } },
  lng: detectLanguage(),
  fallbackLng: 'es',
  initAsync: false,
  interpolation: { escapeValue: false },
});

/** Idioma activo y cómo cambiarlo; vuelve a pintar al cambiar. */
export function useLanguage() {
  const { i18n: instance } = useTranslation();
  const language: Language = instance.language === 'en' ? 'en' : 'es';
  return { language, setLanguage };
}

export { i18n };
