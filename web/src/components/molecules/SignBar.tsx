import type { ElementType, ReactNode } from 'react';

import { cn } from '../../lib/cn';
import { Icon, type IconName } from '../atoms/Icon/Icon';

type Tone = 'sign' | 'sun' | 'dusk' | 'night' | 'neutral';

const toneClass: Record<Tone, string> = {
  sign: 'bg-sign text-on-sign',
  sun: 'bg-morning text-on-morning',
  dusk: 'bg-afternoon text-on-afternoon',
  night: 'bg-night text-on-night',
  neutral: 'bg-surface text-text',
};

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

/** Encabezado de sección como una señal: banda de color, pictograma grande y una palabra. */
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
        'flex min-h-touch flex-wrap items-center gap-x-3 gap-y-1 rounded-md px-4 py-2.5',
        toneClass[tone],
        className,
      )}
    >
      <Icon name={icon} className="size-7" />
      <Heading id={id} className="flex-1 text-lg leading-tight font-extrabold text-balance">
        {title}
      </Heading>
      {meta && <span className="text-caption font-bold whitespace-nowrap">{meta}</span>}
    </div>
  );
}
