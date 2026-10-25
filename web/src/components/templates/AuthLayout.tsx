import type { ReactNode } from 'react';

import { Plate } from '../atoms/Plate/Plate';
import { Text } from '../atoms/Text/Text';
import { WatchMock } from '../molecules/WatchMock';

/**
 * Entrada, registro y recuperación en pantalla dividida: a la izquierda el formulario; a la derecha
 * el panel de señal con degradado y el reloj de muestra. En el móvil el panel es una cabecera de 160 px.
 */
export function AuthLayout({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="grid min-h-dvh bg-bg desktop:grid-cols-2">
      <aside className="order-first flex min-h-40 flex-col justify-center gap-2 bg-sign-gradient px-6 py-4 text-on-sign desktop:order-last desktop:items-center desktop:gap-10 desktop:p-12">
        <div className="flex flex-wrap items-center gap-3">
          <Plate icon="medications" tone="onSign" size="lg" />
          <div className="min-w-0">
            <p className="text-xl leading-tight font-extrabold tracking-tight">Recuerda</p>
            <p>Tus medicamentos, a su hora</p>
          </div>
        </div>
        <div className="hidden desktop:block">
          <WatchMock />
        </div>
        <p className="hidden max-w-sm text-center text-lg text-balance desktop:block">
          El reloj avisa a su hora y aquí ves de un vistazo si se tomó.
        </p>
      </aside>
      <main className="mx-auto flex w-full max-w-md flex-col justify-center gap-6 px-4 py-8">
        <Text variant="display" as="h1">
          {title}
        </Text>
        {children}
      </main>
    </div>
  );
}
