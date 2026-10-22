import { medicationColorClass, medicationColorFor } from '../../../design/tokens';
import { cn } from '../../../lib/cn';
import { Icon, type IconName } from '../Icon/Icon';

type Tone = 'sign' | 'onSign' | 'sun' | 'dusk' | 'night' | 'neutral';
type Size = 'sm' | 'md' | 'lg';

// La placa de pictograma es la firma del sistema: cada opción y categoría lleva la suya.
const toneClass: Record<Tone, string> = {
  sign: 'bg-sign text-on-sign',
  onSign: 'bg-on-sign text-sign',
  sun: 'bg-morning text-on-morning',
  dusk: 'bg-afternoon text-on-afternoon',
  night: 'bg-night text-on-night',
  neutral: 'bg-surface text-text',
};

const sizeClass: Record<Size, { box: string; glyph: string }> = {
  sm: { box: 'size-plate-sm', glyph: 'size-5' },
  md: { box: 'size-plate-md', glyph: 'size-6' },
  lg: { box: 'size-plate-lg', glyph: 'size-8' },
};

type Props = {
  icon: IconName;
  size?: Size;
  /** Si se pasa, la placa se anuncia; si no, es decorativa (la palabra va al lado). */
  label?: string;
  className?: string;
} & ({ tone?: Tone; hex?: never } | { hex: string; tone?: never });

/**
 * Placa cuadrada de esquina corta con un pictograma. `hex` es el color que eligió la persona para un
 * medicamento: se pinta con el tono de la paleta más cercano y el pictograma en blanco; quien la usa
 * muestra siempre el nombre al lado.
 */
export function Plate({ icon, size = 'md', tone = 'sign', hex, label, className }: Props) {
  const { box, glyph } = sizeClass[size];
  return (
    <span
      role={label ? 'img' : undefined}
      aria-label={label}
      aria-hidden={label ? undefined : true}
      data-color={hex}
      className={cn(
        'inline-grid shrink-0 place-items-center rounded-md',
        box,
        hex ? `${medicationColorClass[medicationColorFor(hex)]} text-on-state` : toneClass[tone],
        className,
      )}
    >
      <Icon name={icon} className={glyph} />
    </span>
  );
}
