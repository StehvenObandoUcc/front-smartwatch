import type { ReactNode } from 'react';
import { NavLink } from 'react-router';

import { cn } from '../../../lib/cn';
import { Icon, type IconName } from '../../atoms/Icon/Icon';
import { Text } from '../../atoms/Text/Text';

/** `short` es la etiqueta del menú inferior móvil; `mobile: false` deja el acceso solo en el menú lateral. */
export type NavItem = {
  to: string;
  label: string;
  icon: IconName;
  short?: string;
  mobile?: boolean;
};

type Props = {
  appName: string;
  title: string;
  nav: NavItem[];
  /** Acciones de la cabecera (usuario, cerrar sesión…). */
  actions?: ReactNode;
  children: ReactNode;
};

const MAIN_ID = 'contenido';

/**
 * Móvil (< 640): cabecera + barra de navegación inferior.
 * Tablet y escritorio: menú lateral fijo; en tablet solo iconos con etiqueta visible debajo.
 */
export function AppShell({ appName, title, nav, actions, children }: Props) {
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
        className="hidden border-r-2 border-border bg-surface-raised tablet:sticky tablet:top-0 tablet:flex tablet:h-dvh tablet:w-28 tablet:flex-col tablet:gap-1 tablet:p-2 desktop:w-sidebar desktop:p-4"
      >
        <Text as="p" variant="subtitle" className="hidden px-3 pb-4 desktop:block">
          {appName}
        </Text>
        {nav.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end
            className={({ isActive }) =>
              cn(
                'flex min-h-touch flex-col items-center justify-center gap-1 rounded-xl px-3 py-2 text-caption font-semibold desktop:flex-row desktop:justify-start desktop:gap-3 desktop:text-body',
                isActive ? 'bg-primary text-on-primary shadow-key' : 'text-text hover:bg-surface',
              )
            }
          >
            <Icon name={item.icon} />
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="sticky top-0 z-10 flex min-h-16 items-center justify-between gap-4 border-b-2 border-border bg-bg px-4 tablet:px-6">
          <Text variant="title" as="h1" className="min-w-0 truncate">
            {title}
          </Text>
          {actions}
        </header>

        <main
          id={MAIN_ID}
          tabIndex={-1}
          className="mx-auto w-full max-w-content flex-1 p-4 pb-28 tablet:p-6 tablet:pb-6"
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
              end
              className={({ isActive }) =>
                cn(
                  'flex min-h-16 min-w-0 flex-1 flex-col items-center justify-center gap-0.5 px-1 text-center text-caption font-semibold',
                  isActive ? 'text-primary' : 'text-text-muted',
                )
              }
            >
              {({ isActive }) => (
                <>
                  <Icon name={item.icon} />
                  <span className={cn(isActive && 'underline underline-offset-4')}>
                    {item.short ?? item.label}
                  </span>
                </>
              )}
            </NavLink>
          ))}
      </nav>
    </div>
  );
}
