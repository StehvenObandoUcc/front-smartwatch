import type { ReactNode } from 'react';
import { Link } from 'react-router';

import { cn } from '../../lib/cn';
import { useTheme } from '../../lib/theme';
import { Plate } from '../atoms/Plate/Plate';
import { Text } from '../atoms/Text/Text';
import { ThemeSwitch } from '../molecules/ThemeSwitch';
import { WatchMock } from '../molecules/WatchMock';

// El panel toma la franja del momento en que se abre la entrada.
function panelNow() {
  const hour = new Date().getHours();
  if (hour >= 5 && hour < 12) return 'panel-morning';
  if (hour >= 12 && hour < 19) return 'panel-afternoon';
  return 'panel-night';
}

/**
 * Entrada, registro y recuperación. Escritorio: pantalla dividida, el panel de la franja (con grano
 * y deriva lenta) y el formulario de 440 px como máximo. Móvil: una banda de 120 px arriba.
 */
export function AuthLayout({ title, children }: { title: string; children: ReactNode }) {
  const theme = useTheme();
  return (
    <div className="grid min-h-dvh bg-bg desktop:grid-cols-2">
      <aside
        className={cn(
          'panel panel-grain flex h-band items-end px-4 pb-4 motion-safe:animate-drift tablet:px-8 desktop:h-auto desktop:flex-col desktop:items-start desktop:justify-between desktop:p-12',
          panelNow(),
        )}
      >
        <Link to="/" className="flex items-center gap-3 text-xl font-medium tracking-tight">
          <Plate icon="medications" tone="ink" size="md" />
          Recuerda
        </Link>
        <div className="hidden w-full flex-col items-center gap-10 desktop:flex">
          <WatchMock className="w-72" />
          <p className="max-w-sm text-center text-xl text-balance">
            El reloj avisa a su hora y aquí ves de un vistazo si se tomó.
          </p>
        </div>
        <p className="hidden text-on-panel-muted desktop:block">
          Datos tratados según la Ley 1581 de 2012.
        </p>
      </aside>
      <div className="flex flex-col px-4 py-6 tablet:px-8">
        <div className="flex justify-end">
          <ThemeSwitch compact value={theme.choice} onChange={theme.setChoice} />
        </div>
        <main className="mx-auto flex w-full max-w-form flex-1 flex-col justify-center gap-6 py-8">
          <Text variant="display" as="h1">
            {title}
          </Text>
          {children}
        </main>
      </div>
    </div>
  );
}
