import { useEffect, useState } from 'react';

function reducedMotion() {
  return window.matchMedia?.('(prefers-reduced-motion: reduce)').matches ?? true;
}

/**
 * Recorre `count` pasos cada `ms` mientras no esté en pausa. Con movimiento reducido no arranca y se
 * queda en el primero (`still`), sin botón de pausa que ofrecer.
 */
export function useCycle(count: number, ms: number) {
  const [still] = useState(reducedMotion);
  const [index, setIndex] = useState(0);
  const [paused, setPaused] = useState(false);
  useEffect(() => {
    if (still || paused) return;
    const id = window.setInterval(() => setIndex((i) => (i + 1) % count), ms);
    return () => window.clearInterval(id);
  }, [still, paused, count, ms]);
  return { index, paused, still, toggle: () => setPaused((p) => !p) };
}
