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
  // Teclas con borde inferior: al pulsar bajan 3 px y el borde desaparece.
  primary:
    'bg-primary text-on-primary shadow-key hover:bg-primary-hover active:translate-y-0.75 active:shadow-none',
  secondary:
    'border-2 border-primary bg-surface-raised text-primary shadow-key-quiet hover:bg-surface active:translate-y-0.75 active:shadow-none',
  danger: 'bg-danger text-on-danger shadow-key-danger active:translate-y-0.75 active:shadow-none',
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
        'inline-flex min-h-touch min-w-touch items-center justify-center gap-2 rounded-full px-6 text-body font-semibold transition-all duration-100 motion-reduce:transition-none',
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
