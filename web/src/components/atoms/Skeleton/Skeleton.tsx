import { cn } from '../../../lib/cn';

type Props = {
  /** Texto para lectores de pantalla mientras carga. */
  label?: string;
  /** Forma de lo que va a aparecer: filas de una lista o un bloque. */
  rows?: number;
  className?: string;
};

/**
 * Esqueleto de carga con la forma del contenido real (una fila por elemento), en lugar de un
 * indicador suelto: así la página no salta al llegar los datos. Sin animación si se pidió menos
 * movimiento.
 */
export function Skeleton({ label = 'Cargando', rows = 3, className }: Props) {
  return (
    <div role="status" className={cn('flex flex-col gap-3', className)}>
      {Array.from({ length: rows }, (_, i) => (
        <div
          key={i}
          aria-hidden="true"
          className="flex min-h-touch items-center gap-3 rounded-lg bg-surface p-3 motion-safe:animate-pulse"
        >
          <span className="size-plate-md shrink-0 rounded-md bg-border" />
          <span className="flex flex-1 flex-col gap-2">
            <span className="h-4 w-1/2 rounded-sm bg-border" />
            <span className="h-4 w-1/3 rounded-sm bg-border" />
          </span>
        </div>
      ))}
      <span className="sr-only">{label}</span>
    </div>
  );
}
