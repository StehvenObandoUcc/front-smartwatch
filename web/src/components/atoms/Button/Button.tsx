import type { ComponentProps, ReactNode } from 'react';

import { cn } from '../../../lib/cn';
import { buttonClass, type Variant } from './buttonClass';
import { Icon, type IconName } from '../Icon/Icon';
import { Spinner } from '../Spinner/Spinner';

// ComponentProps incluye `ref` (React 19): quien lo necesite puede mover el foco al botón.
type Props = ComponentProps<'button'> & {
  variant?: Variant;
  loading?: boolean;
  fullWidth?: boolean;
  /** Pictograma del vocabulario a la izquierda de la palabra (o un nodo propio). */
  icon?: IconName | ReactNode;
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
        buttonClass(variant),
        'disabled:cursor-not-allowed disabled:border-border disabled:bg-surface disabled:text-text-muted',
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
