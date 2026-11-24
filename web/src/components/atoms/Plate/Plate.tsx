import { medicationColorClass, medicationColorFor } from '../../../design/tokens';
import { cn } from '../../../lib/cn';
import { Icon, type IconName } from '../Icon/Icon';

type Tone = 'sign' | 'onSign' | 'ink' | 'sun' | 'dusk' | 'night' | 'neutral';
type Size = 'sm' | 'md' | 'lg';

// La placa de pictograma: cada opción y categoría lleva la suya.
const toneClass: Record<Tone, string> = {
  // Tono suave de la marca con el glifo en acento: secciones y bloques.
  sign: 'bg-sign text-on-sign',
  // Tarjeta blanca sobre un panel de franja.
  onSign: 'bg-surface-veil text-on-panel',
  // Relleno de la acción principal: la marca de Recuerda.
  ink: 'bg-primary text-on-primary',
  sun: 'bg-morning text-on-morning',
  dusk: 'bg-afternoon text-on-afternoon',
  night: 'bg-night text-on-night',
  neutral: 'bg-surface text-text',
};

const sizeClass: Record<Size, { box: string; glyph: string }> = {
  sm: { box: 'size-plate-sm', glyph: 'size-5' },
  md: { box: 'size-plate-md', glyph: 'size-6' },
  lg: { box: 'size-plate-lg', glyph: 'size-7' },
};

type Props = {
  icon: IconName;
  size?: Size;
  /** Si se pasa, la placa se anuncia; si no, es decorativa (la palabra va al lado). */
  label?: string;
  className?: string;
} & ({ tone?: Tone; hex?: never } | { hex: string; tone?: never });

/**
 * Placa cuadrada de esquina suave con un pictograma. `hex` es el color que eligió la persona para un
 * medicamento: se pinta con el tono de la paleta más cercano y el pictograma en claro; quien la usa
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
        hex ? `${medicationColorClass[medicationColorFor(hex)]} text-on-light` : toneClass[tone],
        className,
      )}
    >
      <Icon name={icon} className={glyph} />
    </span>
  );
}
