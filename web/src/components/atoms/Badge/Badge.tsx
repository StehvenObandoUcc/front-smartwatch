import type { ReactNode } from 'react';

import { cn } from '../../../lib/cn';
import { Icon, type IconName } from '../Icon/Icon';

type Tone = 'neutral' | 'success' | 'warning' | 'danger' | 'outline';

// Placa de estado: color plano con texto blanco (≥ 5:1 en ambos temas), o solo contorno para lo que
// aún no tiene estado. El tono siempre acompaña una palabra y, si se pasa, un pictograma.
const toneClass: Record<Tone, string> = {
  neutral: 'bg-state-skipped text-on-state',
  success: 'bg-state-taken text-on-state',
  warning: 'bg-state-due text-on-state',
  danger: 'bg-state-missed text-on-state',
  outline: 'border-2 border-border-strong text-text',
};

export function Badge({
  tone = 'neutral',
  icon,
  children,
}: {
  tone?: Tone;
  icon?: IconName;
  children: ReactNode;
}) {
  return (
    <span
      className={cn(
        'inline-flex min-h-8 items-center gap-1.5 rounded-sm px-2.5 py-1 text-caption leading-tight font-bold',
        toneClass[tone],
      )}
    >
      {icon && <Icon name={icon} className="size-5" />}
      {children}
    </span>
  );
}
