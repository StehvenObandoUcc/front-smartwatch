import type { ReactNode } from 'react';

import type { IconName } from '../atoms/Icon/Icon';
import { Plate } from '../atoms/Plate/Plate';

type Props = {
  title: string;
  /** Pictograma de la categoría (reloj, avisos, historial…). */
  icon: IconName;
  /** El dato, en la misma línea (o debajo si no cabe). */
  children: ReactNode;
  /** Enlace a la sección, a la derecha. */
  action?: ReactNode;
};

/**
 * Resumen como una señal de una línea, para una lista `rows`: placa de la categoría, su nombre, el
 * dato y el enlace. El estado (cargando, vacío, error) lo pone quien la usa.
 */
export function SummaryRow({ title, icon, children, action }: Props) {
  return (
    <li className="flex flex-wrap items-center gap-x-3 gap-y-1">
      <Plate icon={icon} size="sm" />
      {/* En el móvil, el nombre encima del dato; desde tablet, en la misma línea. */}
      <div className="flex min-w-0 flex-1 basis-56 flex-col gap-1 tablet:flex-row tablet:items-center tablet:gap-3">
        <h2 className="shrink-0 text-body font-extrabold">{title}</h2>
        <div className="min-w-0">{children}</div>
      </div>
      {action && <div className="ml-auto">{action}</div>}
    </li>
  );
}
