import { useEffect, useState } from 'react';

const DURATION_MS = 700;

function reducedMotion() {
  return window.matchMedia?.('(prefers-reduced-motion: reduce)').matches ?? true;
}

/**
 * La cifra cuenta de 0 a `target` al aparecer; con menos movimiento (o sin matchMedia) llega ya
 * contada. Quien la muestra deja el valor final para lectores de pantalla.
 */
export function useCountUp(target: number) {
  const [value, setValue] = useState(0);
  const still = reducedMotion();
  useEffect(() => {
    if (still) return;
    const start = performance.now();
    let frame = 0;
    const tick = (now: number) => {
      const p = Math.min((now - start) / DURATION_MS, 1);
      setValue(Math.round(target * (1 - (1 - p) ** 3)));
      if (p < 1) frame = requestAnimationFrame(tick);
    };
    frame = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(frame);
  }, [target, still]);
  return still ? target : value;
}
