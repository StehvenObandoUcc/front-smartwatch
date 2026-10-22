import type { ReactNode } from 'react';

import { cn } from '../../lib/cn';
import type { IconName } from '../atoms/Icon/Icon';
import { Plate } from '../atoms/Plate/Plate';

type Props = {
  title: string;
  /** Pictograma de la categoría (reloj, avisos, historial…). */
  icon: IconName;
  /** Para ocupar varias columnas de la cuadrícula del Inicio. */
  className?: string;
  children: ReactNode;
};

/**
 * Resumen del panel como una señal pequeña: placa de la categoría y su nombre, y debajo el dato.
 * El estado (cargando, vacío, error) lo pone quien la usa.
 */
export function InfoCard({ title, icon, className, children }: Props) {
  return (
    <section className={cn('card flex flex-col gap-3 p-4', className)}>
      <div className="flex items-center gap-3">
        <Plate icon={icon} size="sm" />
        <h2 className="text-body font-extrabold">{title}</h2>
      </div>
      {children}
    </section>
  );
}
