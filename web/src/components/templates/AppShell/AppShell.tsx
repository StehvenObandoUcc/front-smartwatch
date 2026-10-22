import type { ReactNode } from 'react';
import { NavLink } from 'react-router';

import { cn } from '../../../lib/cn';
import { Icon, type IconName } from '../../atoms/Icon/Icon';
import { Plate } from '../../atoms/Plate/Plate';

/**
 * `short` es la palabra del menú inferior móvil. `mobile: false` deja la opción solo en el riel
 * (en el móvil se llega desde «Más»); `desktop: false`, solo en el menú móvil.
 */
export type NavItem = {
  to: string;
  label: string;
  icon: IconName;
  short?: string;
  mobile?: boolean;
  desktop?: boolean;
};

type Props = {
  appName: string;
  title: string;
  /** Pictograma de la sección actual, en la placa de la cabecera. */
  icon: IconName;
  nav: NavItem[];
  /** Acciones de la cabecera (cerrar sesión…), sobre el panel de señal. */
  actions?: ReactNode;
  children: ReactNode;
};

const MAIN_ID = 'contenido';

/**
 * Señalética: la cabecera es un panel de señal (placa de la sección y su palabra) y la navegación,
 * una fila de señales (placa y palabra; la activa, encendida).
 * Móvil (< 640): cabecera + menú inferior. Tablet y escritorio: riel lateral de señal.
 */
export function AppShell({ appName, title, icon, nav, actions, children }: Props) {
  return (
    <div className="min-h-dvh bg-bg text-text tablet:flex">
      <a
        href={`#${MAIN_ID}`}
        className="sr-only focus:not-sr-only focus:absolute focus:top-2 focus:left-2 focus:z-50 focus:rounded-md focus:bg-primary focus:p-3 focus:text-on-primary"
      >
        Saltar al contenido
      </a>

      <nav
        aria-label="Principal"
        className="hidden bg-sign text-on-sign tablet:sticky tablet:top-0 tablet:flex tablet:h-dvh tablet:w-28 tablet:flex-col tablet:gap-1 tablet:overflow-y-auto tablet:p-2 desktop:w-sidebar desktop:p-4"
      >
        <p className="flex items-center gap-3 px-2 pt-2 pb-6 text-xl font-extrabold tracking-tight">
          <Plate icon="medications" tone="onSign" size="sm" />
          <span className="hidden desktop:inline">{appName}</span>
        </p>
        {nav
          .filter((item) => item.desktop !== false)
          .map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) =>
                cn(
                  'flex min-h-touch flex-col items-center justify-center gap-1 rounded-md px-2 py-2 text-caption font-bold focus-visible:outline-on-sign desktop:flex-row desktop:justify-start desktop:gap-3 desktop:text-body',
                  isActive ? 'bg-on-sign text-sign' : 'text-on-sign hover:bg-primary-hover',
                )
              }
            >
              <Icon name={item.icon} className="size-7" />
              {item.label}
            </NavLink>
          ))}
      </nav>

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="sticky top-0 z-10 flex min-h-16 items-center gap-3 bg-sign px-4 text-on-sign tablet:px-6">
          <Plate icon={icon} tone="onSign" size="sm" />
          <h1 className="min-w-0 flex-1 truncate text-xl font-extrabold tracking-tight">{title}</h1>
          {actions}
        </header>

        <main
          id={MAIN_ID}
          tabIndex={-1}
          className="mx-auto w-full max-w-content flex-1 p-4 pb-32 tablet:p-6 tablet:pb-6"
        >
          {children}
        </main>
      </div>

      <nav
        aria-label="Principal móvil"
        className="fixed inset-x-0 bottom-0 z-10 flex border-t-2 border-border bg-surface-raised pb-safe-bottom tablet:hidden"
      >
        {nav
          .filter((item) => item.mobile !== false)
          .map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) =>
                cn(
                  'flex min-h-20 min-w-0 flex-1 flex-col items-center justify-center gap-1 text-center text-caption leading-tight font-bold tracking-tight',
                  isActive ? 'text-text' : 'text-text-muted',
                )
              }
            >
              {({ isActive }) => (
                <>
                  <Plate icon={item.icon} tone={isActive ? 'sign' : 'neutral'} size="sm" />
                  <span>{item.short ?? item.label}</span>
                </>
              )}
            </NavLink>
          ))}
      </nav>
    </div>
  );
}
