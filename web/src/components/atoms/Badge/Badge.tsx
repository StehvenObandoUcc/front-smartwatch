import type { ReactNode } from 'react';

import { cn } from '../../../lib/cn';
import { Icon, type IconName } from '../Icon/Icon';

type Tone = 'neutral' | 'success' | 'warning' | 'danger' | 'outline';

// Chip de estado: relleno pleno con texto crema (claro) o tinta (oscuro), ≥ 4,8:1, o un tinte neutro para lo que
// aún no tiene estado (sin borde, para que no se confunda con un botón secundario). El tono siempre acompaña una palabra y, si se pasa, un pictograma.
const toneClass: Record<Tone, string> = {
  neutral: 'bg-state-skipped text-on-state',
  success: 'bg-state-taken text-on-state',
  warning: 'bg-state-due text-on-state',
  danger: 'bg-state-missed text-on-state',
  outline: 'bg-surface text-text',
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
        'inline-flex min-h-8 max-w-full items-center gap-1.5 rounded-sm px-3 py-1 text-caption leading-tight font-medium',
        toneClass[tone],
      )}
    >
      {icon && <Icon name={icon} className="size-4" />}
      {children}
    </span>
  );
}
