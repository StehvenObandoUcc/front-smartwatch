import {
  medicationColorClass,
  medicationColorFor,
  type MedicationColor,
} from '../../../design/tokens';
import { cn } from '../../../lib/cn';

type Props = {
  size?: 'sm' | 'md';
} & ({ color: MedicationColor; hex?: never } | { hex: string; color?: never });

// Decorativo: quien lo usa debe mostrar siempre el nombre al lado.
// `hex` es el color que eligió la persona: se pinta con el tono de la paleta más cercano.
export function ColorDot({ color, hex, size = 'md' }: Props) {
  return (
    <span
      aria-hidden="true"
      data-color={color ?? hex}
      className={cn(
        'inline-block shrink-0 rounded-full border-2 border-border',
        size === 'sm' ? 'size-3' : 'size-4',
        medicationColorClass[color ?? medicationColorFor(hex ?? '')],
      )}
    />
  );
}
