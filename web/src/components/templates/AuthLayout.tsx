import type { ReactNode } from 'react';

import { Icon } from '../atoms/Icon/Icon';
import { Plate } from '../atoms/Plate/Plate';
import { Ring } from '../atoms/Ring/Ring';
import { Text } from '../atoms/Text/Text';

/** El reloj de muestra: el anillo del día como se ve en la muñeca. Ilustración, no datos. */
function WatchMock() {
  return (
    <div
      aria-hidden="true"
      className="flex size-72 flex-col items-center justify-center gap-2 rounded-full border-8 border-on-sign/20 bg-sign"
    >
      <Ring value={2} total={3} tone="onSign">
        <span className="text-2xl font-extrabold">2</span>
        <span className="text-caption">de 3 tomadas</span>
      </Ring>
      <span className="inline-flex items-center gap-1 font-bold">
        <Icon name="due" className="size-5" />
        Próxima 13:00
      </span>
    </div>
  );
}

/**
 * Entrada, registro y recuperación en pantalla dividida: a la izquierda el formulario; a la derecha
 * el panel de señal con degradado y el reloj de muestra. En el móvil el panel es una cabecera de 160 px.
 */
export function AuthLayout({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="grid min-h-dvh bg-bg desktop:grid-cols-2">
      <aside className="order-first flex h-40 flex-col justify-center gap-2 bg-sign-gradient px-6 text-on-sign desktop:order-last desktop:h-auto desktop:items-center desktop:gap-10 desktop:p-12">
        <div className="flex items-center gap-3">
          <Plate icon="medications" tone="onSign" size="lg" />
          <div>
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
