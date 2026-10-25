import { cn } from '../../../lib/cn';

export type Variant = 'primary' | 'secondary' | 'danger' | 'ghost' | 'onSign' | 'inverse';

// Como los botones del reloj: el principal, relleno claro con tinta; el secundario, vidrio con un
// contorno fino; el de peligro, rojo claro. Responden al pulsar (no al soltar): se hunden un poco.
const variantClass: Record<Variant, string> = {
  primary: 'bg-primary text-on-primary hover:bg-primary-hover',
  secondary: 'border border-border-strong bg-surface-raised text-text hover:bg-surface',
  danger: 'bg-danger text-on-danger hover:opacity-90',
  ghost: 'bg-transparent text-primary underline-offset-4 hover:bg-surface hover:underline',
  // Sobre el panel de señal (cabecera): texto y foco blancos.
  onSign: 'bg-transparent text-on-sign hover:bg-on-sign/10 focus-visible:outline-on-sign',
  // Principal sobre el degradado (banda de CTA): relleno claro con el petróleo de la señal.
  inverse: 'bg-on-sign text-sign hover:bg-sign-muted focus-visible:outline-on-sign',
};

/** Las clases del botón; también para un enlace que se ve como botón (navegar no es una acción). */
export function buttonClass(variant: Variant = 'primary', className?: string) {
  return cn(
    'inline-flex min-h-control min-w-touch items-center justify-center gap-2 rounded-md px-5 py-2 text-center text-body font-bold transition duration-100 ease-out active:scale-97 motion-reduce:transition-none motion-reduce:active:scale-100',
    variantClass[variant],
    className,
  );
}
