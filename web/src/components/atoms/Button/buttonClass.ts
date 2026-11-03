import { cn } from '../../../lib/cn';

export type Variant = 'primary' | 'secondary' | 'danger' | 'ghost' | 'quiet' | 'inverse';

// El principal es un relleno pleno de 56 px (tinta en claro, crema en oscuro); el secundario, de
// 48 px con un contorno; el de peligro, rojo. `quiet` es una acción discreta de la cabecera y
// `ghost`, un enlace de acción. Se hunden un poco al pulsar.
const variantClass: Record<Variant, string> = {
  primary: 'min-h-control px-6 bg-primary text-on-primary hover:bg-primary-hover',
  secondary:
    'min-h-control-sm px-5 border border-border-strong bg-transparent text-text hover:bg-surface',
  danger: 'min-h-control px-6 bg-danger text-on-danger hover:opacity-90',
  ghost:
    'min-h-control-sm px-3 bg-transparent text-accent underline underline-offset-4 hover:bg-surface',
  quiet: 'min-h-control-sm px-3 bg-transparent text-text hover:bg-surface',
  // Sobre un panel de franja: el mismo relleno principal.
  inverse: 'min-h-control px-6 bg-primary text-on-primary hover:bg-primary-hover',
};

/** Las clases del botón; también para un enlace que se ve como botón (navegar no es una acción). */
export function buttonClass(variant: Variant = 'primary', className?: string) {
  return cn(
    'inline-flex min-w-touch items-center justify-center gap-2 rounded-md py-2 text-center text-body font-medium transition duration-100 ease-out active:scale-98 motion-reduce:transition-none motion-reduce:active:scale-100',
    variantClass[variant],
    className,
  );
}
