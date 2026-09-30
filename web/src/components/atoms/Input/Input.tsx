import type { InputHTMLAttributes } from 'react';

import { cn } from '../../../lib/cn';

type Props = InputHTMLAttributes<HTMLInputElement> & {
  invalid?: boolean;
};

// La etiqueta y el mensaje de error los pone la molécula FormField.
export function Input({ invalid = false, className, ...rest }: Props) {
  return (
    <input
      aria-invalid={invalid || undefined}
      className={cn(
        'min-h-touch w-full rounded-md border-2 bg-surface-raised px-3 text-body text-text',
        'placeholder:text-text-muted disabled:cursor-not-allowed disabled:opacity-50',
        invalid ? 'border-danger' : 'border-border',
        className,
      )}
      {...rest}
    />
  );
}
