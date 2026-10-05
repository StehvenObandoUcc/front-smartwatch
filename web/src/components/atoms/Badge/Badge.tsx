import type { ReactNode } from 'react';

import { cn } from '../../../lib/cn';

type Tone = 'neutral' | 'success' | 'warning' | 'danger';

const toneClass: Record<Tone, string> = {
  neutral: 'border-border-strong text-text-muted',
  success: 'border-success text-success',
  warning: 'border-warning text-warning',
  danger: 'border-danger text-danger',
};

// El tono siempre acompaña un texto: la información nunca va solo por color.
export function Badge({ tone = 'neutral', children }: { tone?: Tone; children: ReactNode }) {
  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full border-2 bg-surface-raised px-3 py-0.5 text-caption font-semibold',
        toneClass[tone],
      )}
    >
      {children}
    </span>
  );
}
