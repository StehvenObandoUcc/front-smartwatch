import { medicationColorClass, type MedicationColor } from '../../../design/tokens';
import { cn } from '../../../lib/cn';

type Props = {
  color: MedicationColor;
  size?: 'sm' | 'md';
};

// Decorativo: quien lo usa debe mostrar siempre el nombre al lado.
export function ColorDot({ color, size = 'md' }: Props) {
  return (
    <span
      aria-hidden="true"
      data-color={color}
      className={cn(
        'inline-block shrink-0 rounded-full border-2 border-border',
        size === 'sm' ? 'size-3' : 'size-4',
        medicationColorClass[color],
      )}
    />
  );
}
