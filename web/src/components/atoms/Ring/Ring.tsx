import type { ReactNode } from 'react';

import { cn } from '../../../lib/cn';

type Props = {
  value: number;
  total: number;
  /** Lo que va en el centro (la cifra y su palabra). */
  children?: ReactNode;
  className?: string;
};

/**
 * Anillo de progreso que se dibuja al aparecer (si el sistema no pide menos movimiento). Es
 * decorativo: quien lo usa dice lo mismo en palabras en el centro.
 */
export function Ring({ value, total, children, className }: Props) {
  const filled = total === 0 ? 0 : Math.min(value / total, 1) * 100;
  return (
    <div className={cn('relative grid size-ring shrink-0 place-items-center', className)}>
      <svg viewBox="0 0 120 120" aria-hidden="true" className="absolute inset-0 -rotate-90">
        <circle cx="60" cy="60" r="52" fill="none" strokeWidth="12" className="stroke-surface" />
        {filled > 0 && (
          <circle
            cx="60"
            cy="60"
            r="52"
            fill="none"
            strokeWidth="12"
            strokeLinecap="round"
            pathLength={100}
            strokeDasharray="100"
            strokeDashoffset={100 - filled}
            className="stroke-state-taken motion-safe:animate-ring"
          />
        )}
      </svg>
      <div className="relative flex flex-col items-center text-center">{children}</div>
    </div>
  );
}
