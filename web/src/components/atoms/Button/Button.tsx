import type { ButtonHTMLAttributes, ReactNode } from 'react';

import { cn } from '../../../lib/cn';
import { Spinner } from '../Spinner/Spinner';

type Variant = 'primary' | 'secondary' | 'danger' | 'ghost';

type Props = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: Variant;
  loading?: boolean;
  fullWidth?: boolean;
  icon?: ReactNode;
};

const variantClass: Record<Variant, string> = {
  // Elevados en reposo; al presionar se hunden (active) sin depender solo de la sombra: cambia también el color.
  primary: 'bg-primary text-on-primary shadow-raised-sm hover:bg-primary-hover active:shadow-inset',
  secondary:
    'border-2 border-primary bg-surface-raised text-primary shadow-raised-sm hover:bg-surface active:bg-surface active:shadow-inset',
  danger: 'bg-danger text-on-danger shadow-raised-sm active:shadow-inset active:brightness-90',
  ghost: 'bg-transparent text-primary hover:bg-surface active:shadow-inset',
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
  return (
    <button
      type={type}
      disabled={inactive}
      aria-busy={loading || undefined}
      className={cn(
        'inline-flex min-h-touch min-w-touch items-center justify-center gap-2 rounded-md px-5 text-body font-semibold transition-all duration-150 motion-reduce:transition-none',
        'disabled:cursor-not-allowed disabled:opacity-50 disabled:shadow-none',
        variantClass[variant],
        fullWidth && 'w-full',
        className,
      )}
      {...rest}
    >
      {loading ? <Spinner size="sm" label="Procesando" /> : icon}
      {children}
    </button>
  );
}
