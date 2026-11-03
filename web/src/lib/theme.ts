import { useEffect, useSyncExternalStore } from 'react';

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

// Sin almacenamiento (modo privado), la elección vive solo en memoria durante la visita.
let memory: ThemeChoice | null = null;
const listeners = new Set<() => void>();

function current(): ThemeChoice {
  return memory ?? readChoice();
}

function saveChoice(choice: ThemeChoice) {
  memory = choice;
  try {
    if (choice === 'system') localStorage.removeItem(THEME_KEY);
    else localStorage.setItem(THEME_KEY, choice);
  } catch {
    // Se queda en memoria.
  }
  listeners.forEach((notify) => notify());
}

function subscribe(notify: () => void) {
  listeners.add(notify);
  return () => listeners.delete(notify);
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

/**
 * Tema elegido, guardado y aplicado; todos los selectores de la página comparten la elección. Con
 * «Sistema» sigue los cambios del sistema en vivo.
 */
export function useTheme() {
  const choice = useSyncExternalStore(subscribe, current, () => 'system' as const);
  useEffect(() => {
    applyTheme(resolveTheme(choice, systemTheme()));
    if (choice !== 'system' || !window.matchMedia) return;
    const media = window.matchMedia(DARK_QUERY);
    const onChange = () => applyTheme(systemTheme());
    media.addEventListener('change', onChange);
    return () => media.removeEventListener('change', onChange);
  }, [choice]);
  return { choice, setChoice: saveChoice };
}
