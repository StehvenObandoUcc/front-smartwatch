import type { ElementType, ReactNode } from 'react';

import { cn } from '../../../lib/cn';

type Variant = 'display' | 'title' | 'subtitle' | 'body' | 'caption';
type Tone = 'default' | 'muted' | 'danger' | 'success';

type Props = {
  as?: ElementType;
  variant?: Variant;
  tone?: Tone;
  className?: string;
  id?: string;
  children: ReactNode;
};

// Una sola letra en tres pesos: el peso y el tamaño separan los niveles, no otra familia.
const variantClass: Record<Variant, string> = {
  display: 'text-2xl font-extrabold tracking-tight text-balance',
  title: 'text-xl font-extrabold tracking-tight text-balance',
  subtitle: 'text-lg font-bold text-balance',
  body: 'text-body text-pretty',
  caption: 'text-caption text-pretty',
};

const toneClass: Record<Tone, string> = {
  default: 'text-text',
  muted: 'text-text-muted',
  danger: 'text-danger',
  success: 'text-success',
};

const defaultElement: Record<Variant, ElementType> = {
  display: 'h1',
  title: 'h2',
  subtitle: 'h3',
  body: 'p',
  caption: 'p',
};

export function Text({ as, variant = 'body', tone = 'default', className, id, children }: Props) {
  const Component = as ?? defaultElement[variant];
  return (
    <Component id={id} className={cn(variantClass[variant], toneClass[tone], className)}>
      {children}
    </Component>
  );
}
