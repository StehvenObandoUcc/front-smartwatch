import type { ReactNode } from 'react';

import { cn } from '../../../lib/cn';

type Props = {
  value: number;
  total: number;
  /** Lo que va en el centro (la cifra y su palabra). */
  children?: ReactNode;
  /** `default` sobre tarjeta o panel; `watch` sobre la esfera del reloj de muestra. */
  tone?: 'default' | 'watch';
  /** `sm` (144 px) para el reloj de muestra. */
  size?: 'md' | 'sm';
  className?: string;
};

const toneClass = {
  default: { track: 'stroke-text/10', fill: 'stroke-state-taken' },
  watch: { track: 'stroke-watch-fg/20', fill: 'stroke-watch-accent' },
};

/**
 * Anillo de progreso que se dibuja al aparecer (si el sistema no pide menos movimiento). Es
 * decorativo: quien lo usa dice lo mismo en palabras en el centro.
 */
export function Ring({ value, total, children, tone = 'default', size = 'md', className }: Props) {
  const filled = total === 0 ? 0 : Math.min(value / total, 1) * 100;
  return (
    <div
      className={cn(
        'relative grid shrink-0 place-items-center',
        size === 'sm' ? 'size-36' : 'size-ring',
        className,
      )}
    >
      <svg viewBox="0 0 120 120" aria-hidden="true" className="absolute inset-0 -rotate-90">
        <circle
          cx="60"
          cy="60"
          r="54"
          fill="none"
          strokeWidth="9"
          className={toneClass[tone].track}
        />
        {filled > 0 && (
          <circle
            cx="60"
            cy="60"
            r="54"
            fill="none"
            strokeWidth="9"
            strokeLinecap="round"
            pathLength={100}
            strokeDasharray="100"
            strokeDashoffset={100 - filled}
            className={cn(toneClass[tone].fill, 'motion-safe:animate-ring')}
          />
        )}
      </svg>
      <div className="relative flex flex-col items-center px-6 text-center text-balance">
        {children}
      </div>
    </div>
  );
}
