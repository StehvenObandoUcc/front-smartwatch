import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { LanguageSwitch } from '../components/molecules/LanguageSwitch';
import en from './en.json';
import es from './es.json';
import { detectLanguage, i18n, LANGUAGE_KEY, useLanguage } from '.';

function keys(value: object, prefix = ''): string[] {
  return Object.entries(value).flatMap(([key, child]) =>
    typeof child === 'object' && child !== null
      ? keys(child as object, `${prefix}${key}.`)
      : [`${prefix}${key}`],
  );
}

function Connected() {
  const { language, setLanguage } = useLanguage();
  return <LanguageSwitch value={language} onChange={setLanguage} />;
}

afterEach(() => {
  localStorage.clear();
  vi.restoreAllMocks();
});

describe('idioma', () => {
  it('sin elección guardada usa el del navegador: «en…» es inglés, lo demás español', () => {
    vi.spyOn(navigator, 'language', 'get').mockReturnValue('en-US');
    expect(detectLanguage()).toBe('en');
    vi.spyOn(navigator, 'language', 'get').mockReturnValue('fr-FR');
    expect(detectLanguage()).toBe('es');
  });

  it('la elección guardada manda sobre el navegador', () => {
    vi.spyOn(navigator, 'language', 'get').mockReturnValue('en-GB');
    localStorage.setItem(LANGUAGE_KEY, 'es');
    expect(detectLanguage()).toBe('es');
  });

  it('un almacenamiento que falla no rompe la detección', () => {
    vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
      throw new Error('bloqueado');
    });
    vi.spyOn(navigator, 'language', 'get').mockReturnValue('es-CO');
    expect(detectLanguage()).toBe('es');
  });

  it('el selector cambia el idioma, lo guarda y actualiza <html lang>', async () => {
    render(<Connected />);
    expect(screen.getByRole('radio', { name: 'Español' })).toBeChecked();
    await userEvent.click(screen.getByRole('radio', { name: 'English' }));
    expect(screen.getByRole('radio', { name: 'English' })).toBeChecked();
    expect(i18n.language).toBe('en');
    expect(localStorage.getItem(LANGUAGE_KEY)).toBe('en');
    expect(document.documentElement.lang).toBe('en');
    expect(screen.getByRole('group', { name: 'Language' })).toBeInTheDocument();
  });

  it('inglés tiene las mismas claves que español', () => {
    expect(keys(en).sort()).toEqual(keys(es).sort());
  });
});
