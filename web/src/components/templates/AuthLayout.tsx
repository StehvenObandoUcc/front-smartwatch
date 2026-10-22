import type { ReactNode } from 'react';

import { Plate } from '../atoms/Plate/Plate';
import { Text } from '../atoms/Text/Text';

/** Entrada, registro y recuperación: la marca como señal arriba y el formulario en una placa. */
export function AuthLayout({ title, children }: { title: string; children: ReactNode }) {
  return (
    <main className="mx-auto flex min-h-dvh w-full max-w-md flex-col justify-center gap-6 bg-bg p-4">
      <div className="flex items-center gap-3">
        <Plate icon="medications" size="lg" />
        <div>
          <p className="text-xl leading-tight font-extrabold tracking-tight">Recuerda</p>
          <Text tone="muted">Tus medicamentos, a su hora</Text>
        </div>
      </div>
      <div className="card flex flex-col gap-6 p-6">
        <Text variant="display" as="h1">
          {title}
        </Text>
        {children}
      </div>
    </main>
  );
}
