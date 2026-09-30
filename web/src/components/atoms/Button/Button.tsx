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
  primary: 'bg-primary text-on-primary hover:bg-primary-hover',
  secondary: 'border-2 border-primary bg-surface-raised text-primary hover:bg-surface',
  danger: 'bg-danger text-on-danger',
  ghost: 'bg-transparent text-primary hover:bg-surface',
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
        'inline-flex min-h-touch min-w-touch items-center justify-center gap-2 rounded-md px-5 text-body font-semibold transition-colors',
        'disabled:cursor-not-allowed disabled:opacity-50',
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
