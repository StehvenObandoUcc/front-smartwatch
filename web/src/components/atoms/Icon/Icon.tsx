import { cn } from '../../../lib/cn';
import { icons, type IconName } from './icons';

export type { IconName } from './icons';

type Props = {
  name: IconName;
  /** Si se pasa, el icono se anuncia; si no, es decorativo. */
  label?: string;
  className?: string;
};

export function Icon({ name, label, className }: Props) {
  const Glyph = icons[name];
  return (
    <Glyph
      strokeWidth={2.25}
      absoluteStrokeWidth={false}
      role={label ? 'img' : undefined}
      aria-label={label}
      aria-hidden={label ? undefined : true}
      className={cn('size-6 shrink-0', className)}
    />
  );
}
