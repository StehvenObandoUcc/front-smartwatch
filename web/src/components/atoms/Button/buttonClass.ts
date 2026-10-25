import { cn } from '../../../lib/cn';

export type Variant = 'primary' | 'secondary' | 'danger' | 'ghost' | 'onSign' | 'inverse';

// Placa de señal: esquina corta, plana, sin sombra. La principal es el petróleo de la señal; la
// secundaria, contorno de tinta; la de peligro, rojo. Al pulsar se oscurece, no se mueve.
const variantClass: Record<Variant, string> = {
  primary: 'bg-primary text-on-primary hover:bg-primary-hover',
  secondary: 'border-2 border-border-strong bg-surface-raised text-text hover:bg-surface',
  danger: 'bg-danger text-on-danger hover:opacity-90',
  ghost: 'bg-transparent text-primary underline-offset-4 hover:bg-surface hover:underline',
  // Sobre el panel de señal (cabecera): texto y foco blancos.
  onSign: 'bg-transparent text-on-sign hover:bg-primary-hover focus-visible:outline-on-sign',
  // Principal sobre el degradado (banda de CTA): relleno claro con el petróleo de la señal.
  inverse: 'bg-on-sign text-sign hover:bg-sign-muted focus-visible:outline-on-sign',
};

/** Las clases del botón; también para un enlace que se ve como botón (navegar no es una acción). */
export function buttonClass(variant: Variant = 'primary', className?: string) {
  return cn(
    'inline-flex min-h-control min-w-touch items-center justify-center gap-2 rounded-md px-5 py-2 text-center text-body font-bold transition-colors duration-100 motion-reduce:transition-none',
    variantClass[variant],
    className,
  );
}
