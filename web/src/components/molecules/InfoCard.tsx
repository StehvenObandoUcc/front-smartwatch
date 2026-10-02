import type { ReactNode } from 'react';

import { Text } from '../atoms/Text/Text';

type Props = {
  title: string;
  children: ReactNode;
};

/** Tarjeta de resumen del panel: título y contenido; el estado (cargando, vacío, error) lo pone quien la usa. */
export function InfoCard({ title, children }: Props) {
  return (
    <section className="flex flex-col gap-2 rounded-md border-2 border-border bg-surface-raised p-4">
      <Text variant="caption" tone="muted" as="h2">
        {title}
      </Text>
      {children}
    </section>
  );
}
