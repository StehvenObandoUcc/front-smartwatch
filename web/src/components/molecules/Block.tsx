import { useId, type ReactNode } from 'react';

import { cn } from '../../lib/cn';
import type { IconName } from '../atoms/Icon/Icon';
import { Plate } from '../atoms/Plate/Plate';

type Props = {
  icon: IconName;
  /** Una o dos palabras. */
  title: string;
  /** La pregunta que responde el bloque. */
  question: string;
  /** Enlace a la sección, a la derecha del título. */
  action?: ReactNode;
  children: ReactNode;
  className?: string;
};

/**
 * Bloque del panel: pictograma y título, la pregunta que responde y debajo el dato. El estado
 * (cargando, vacío, error) lo pone quien lo usa.
 */
export function Block({ icon, title, question, action, children, className }: Props) {
  const id = useId();
  return (
    <section
      aria-labelledby={id}
      className={cn('raised flex min-w-0 flex-col gap-4 p-5 motion-safe:animate-rise', className)}
    >
      <header className="flex flex-wrap items-start gap-x-3 gap-y-2">
        <Plate icon={icon} size="sm" />
        <div className="min-w-0 flex-1 basis-40">
          <h2 id={id} className="text-lg leading-tight font-extrabold tracking-tight">
            {title}
          </h2>
          <p className="text-text-muted">{question}</p>
        </div>
        {action}
      </header>
      {children}
    </section>
  );
}
