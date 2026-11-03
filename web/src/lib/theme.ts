import { useEffect, useState } from 'react';

/** Lo que elige la persona: seguir al sistema o fijar un tema. */
export type ThemeChoice = 'system' | 'light' | 'dark';
export type Theme = 'light' | 'dark';

// La misma clave que lee el script de index.html antes de pintar.
export const THEME_KEY = 'recuerda-tema';

const DARK_QUERY = '(prefers-color-scheme: dark)';

/** La elección guardada; sin elección (o sin almacenamiento), el sistema. */
export function readChoice(): ThemeChoice {
  try {
    const saved = localStorage.getItem(THEME_KEY);
    return saved === 'light' || saved === 'dark' ? saved : 'system';
  } catch {
    return 'system';
  }
}

function saveChoice(choice: ThemeChoice) {
  try {
    if (choice === 'system') localStorage.removeItem(THEME_KEY);
    else localStorage.setItem(THEME_KEY, choice);
  } catch {
    // Sin almacenamiento (modo privado): el tema vale solo para esta visita.
  }
}

function systemTheme(): Theme {
  return window.matchMedia?.(DARK_QUERY).matches ? 'dark' : 'light';
}

export function resolveTheme(choice: ThemeChoice, system: Theme): Theme {
  return choice === 'system' ? system : choice;
}

/** Pone el tema en <html> y la barra del navegador con el fondo del tema. */
export function applyTheme(theme: Theme) {
  const root = document.documentElement;
  root.dataset['theme'] = theme;
  const bg = getComputedStyle(root).getPropertyValue('--bg').trim();
  const meta = document.querySelector('meta[name="theme-color"]');
  if (meta && bg) meta.setAttribute('content', bg);
}

/** Tema elegido, guardado y aplicado; con «Sistema» sigue los cambios del sistema en vivo. */
export function useTheme() {
  const [choice, setChoice] = useState<ThemeChoice>(readChoice);
  useEffect(() => {
    applyTheme(resolveTheme(choice, systemTheme()));
    if (choice !== 'system' || !window.matchMedia) return;
    const media = window.matchMedia(DARK_QUERY);
    const onChange = () => applyTheme(systemTheme());
    media.addEventListener('change', onChange);
    return () => media.removeEventListener('change', onChange);
  }, [choice]);
  return {
    choice,
    setChoice: (next: ThemeChoice) => {
      saveChoice(next);
      setChoice(next);
    },
  };
}
