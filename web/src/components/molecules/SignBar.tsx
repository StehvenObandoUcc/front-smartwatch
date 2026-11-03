import type { ElementType, ReactNode } from 'react';

import { cn } from '../../lib/cn';
import type { IconName } from '../atoms/Icon/Icon';
import { Plate } from '../atoms/Plate/Plate';

type Tone = 'sign' | 'sun' | 'dusk' | 'night' | 'neutral';

type Props = {
  icon: IconName;
  title: string;
  tone?: Tone;
  /** Dato a la derecha (p. ej. «5:00 a 12:00» o «1 de 3»). */
  meta?: ReactNode;
  /** Nivel del encabezado; por defecto h2. */
  as?: ElementType;
  id?: string;
  className?: string;
};

/** Encabezado de sección: placa del color de la franja, su palabra y un dato, sobre un filete. */
export function SignBar({
  icon,
  title,
  tone = 'sign',
  meta,
  as: Heading = 'h2',
  id,
  className,
}: Props) {
  return (
    <div
      className={cn(
        'flex min-h-touch flex-wrap items-center gap-x-3 gap-y-1 border-b border-border pb-3',
        className,
      )}
    >
      <Plate icon={icon} tone={tone} size="sm" />
      <Heading id={id} className="flex-1 text-lg leading-tight font-medium text-balance">
        {title}
      </Heading>
      {meta && <span className="text-caption whitespace-nowrap text-text-muted">{meta}</span>}
    </div>
  );
}
