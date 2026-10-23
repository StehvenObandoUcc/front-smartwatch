import type { ComponentProps, ReactNode } from 'react';

import { cn } from '../../../lib/cn';
import { Icon, type IconName } from '../Icon/Icon';
import { Spinner } from '../Spinner/Spinner';

type Variant = 'primary' | 'secondary' | 'danger' | 'ghost' | 'onSign';

// ComponentProps incluye `ref` (React 19): quien lo necesite puede mover el foco al botón.
type Props = ComponentProps<'button'> & {
  variant?: Variant;
  loading?: boolean;
  fullWidth?: boolean;
  /** Pictograma del vocabulario a la izquierda de la palabra (o un nodo propio). */
  icon?: IconName | ReactNode;
};

// Placa de señal: esquina corta, plana, sin sombra. La principal es el petróleo de la señal; la
// secundaria, contorno de tinta; la de peligro, rojo. Al pulsar se oscurece, no se mueve.
const variantClass: Record<Variant, string> = {
  primary: 'bg-primary text-on-primary hover:bg-primary-hover',
  secondary: 'border-2 border-border-strong bg-surface-raised text-text hover:bg-surface',
  danger: 'bg-danger text-on-danger hover:opacity-90',
  ghost: 'bg-transparent text-primary underline-offset-4 hover:bg-surface hover:underline',
  // Sobre el panel de señal (cabecera): texto y foco blancos.
  onSign: 'bg-transparent text-on-sign hover:bg-primary-hover focus-visible:outline-on-sign',
};

export function Button({
  variant = 'primary',
  loading = false,
  fullWidth = false,
  disabled,
  icon,
  type = 'button',
  className,
  children,
  ...rest
}: Props) {
  const inactive = disabled || loading;
  const glyph = typeof icon === 'string' ? <Icon name={icon as IconName} /> : icon;
  return (
    <button
      type={type}
      disabled={inactive}
      aria-busy={loading || undefined}
      className={cn(
        'inline-flex min-h-control min-w-touch items-center justify-center gap-2 rounded-md px-5 py-2 text-center text-body font-bold transition-colors duration-100 motion-reduce:transition-none',
        'disabled:cursor-not-allowed disabled:border-border disabled:bg-surface disabled:text-text-muted',
        variantClass[variant],
        fullWidth && 'w-full',
        className,
      )}
      {...rest}
    >
      {loading ? <Spinner size="sm" label="Procesando" /> : glyph}
      {children}
    </button>
  );
}
