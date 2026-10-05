import type { ReactNode } from 'react';

import { cn } from '../../lib/cn';
import { Text } from '../atoms/Text/Text';

type Props = {
  title: string;
  /** Para ocupar varias columnas de la cuadrícula del Inicio. */
  className?: string;
  children: ReactNode;
};

/** Tarjeta de resumen del panel: título y contenido; el estado (cargando, vacío, error) lo pone quien la usa. */
export function InfoCard({ title, className, children }: Props) {
  return (
    <section className={cn('card flex flex-col gap-2 p-4', className)}>
      <Text variant="caption" tone="muted" as="h2">
        {title}
      </Text>
      {children}
    </section>
  );
}
