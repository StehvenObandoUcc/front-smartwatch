import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { NavLink } from 'react-router';

import { cn } from '../../../lib/cn';
import { Icon, type IconName } from '../../atoms/Icon/Icon';
import { Plate } from '../../atoms/Plate/Plate';

/**
 * `short` es la palabra de la barra de pestañas del móvil. `mobile: false` deja la opción solo en
 * la barra lateral (en el móvil se llega desde «Más»); `desktop: false`, solo en las pestañas.
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
  /** Segunda línea de la cabecera: quién y qué día. */
  subtitle?: ReactNode;
  nav: NavItem[];
  /** Acciones de la cabecera (tema, salir…). */
  actions?: ReactNode;
  /** Pie de la barra lateral (quién ha entrado). */
  footer?: ReactNode;
  children: ReactNode;
};

const MAIN_ID = 'contenido';

/** La marca: placa de tinta con la cápsula y la palabra. */
function Brand({ appName, wordClass }: { appName: string; wordClass?: string }) {
  return (
    <span className="flex items-center gap-3 text-lg font-medium tracking-tight">
      <Plate icon="medications" tone="ink" size="sm" />
      <span className={wordClass}>{appName}</span>
    </span>
  );
}

/**
 * Móvil (< 640): barra superior con la sección y barra inferior de pestañas. Tablet: barra lateral
 * estrecha (pictograma sobre la palabra). Escritorio: barra lateral de 256 px con pictograma y
 * palabra en línea. La opción activa es una tarjeta elevada, no solo un color.
 */
export function AppShell({ appName, title, subtitle, nav, actions, footer, children }: Props) {
  const { t } = useTranslation();
  return (
    <div className="min-h-dvh bg-bg text-text tablet:flex">
      <a
        href={`#${MAIN_ID}`}
        className="sr-only focus:not-sr-only focus:absolute focus:top-2 focus:left-2 focus:z-50 focus:rounded-md focus:bg-primary focus:p-3 focus:text-on-primary"
      >
        {t('common.skipToContent')}
      </a>

      <nav
        aria-label={t('nav.main')}
        className="hidden border-r border-border tablet:sticky tablet:top-0 tablet:flex tablet:h-dvh tablet:w-28 tablet:shrink-0 tablet:flex-col tablet:gap-1 tablet:overflow-y-auto tablet:p-3 desktop:w-sidebar desktop:p-4"
      >
        <p className="flex justify-center px-2 pt-1 pb-6 desktop:justify-start">
          <Brand appName={appName} wordClass="hidden desktop:inline" />
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
                  'flex min-h-touch flex-col items-center justify-center gap-1 rounded-md px-2 py-2 text-caption font-medium transition-colors motion-reduce:transition-none desktop:flex-row desktop:justify-start desktop:gap-3 desktop:px-3 desktop:text-body',
                  isActive
                    ? 'bg-surface-raised text-text shadow-raised'
                    : 'text-text-muted hover:bg-surface hover:text-text',
                )
              }
            >
              <Icon name={item.icon} className="size-6" />
              {item.label}
            </NavLink>
          ))}
        {footer && <div className="mt-auto hidden pt-6 desktop:block">{footer}</div>}
      </nav>

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="glass sticky top-0 z-10 flex min-h-16 items-center gap-3 border-b px-4 tablet:px-8">
          <div className="min-w-0 flex-1 py-2">
            <h1 className="truncate text-xl leading-tight font-medium tracking-tight">{title}</h1>
            {subtitle && <p className="text-caption leading-snug text-text-muted">{subtitle}</p>}
          </div>
          {actions && <div className="flex shrink-0 items-center gap-2">{actions}</div>}
        </header>

        <main
          id={MAIN_ID}
          tabIndex={-1}
          className="mx-auto w-full max-w-content flex-1 p-4 pb-32 tablet:p-8 tablet:pb-8"
        >
          {children}
        </main>
      </div>

      <nav
        aria-label={t('nav.mainMobile')}
        className="glass fixed inset-x-0 bottom-0 z-10 flex border-t pb-safe-bottom tablet:hidden"
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
                  'flex min-h-18 min-w-0 flex-1 flex-col items-center justify-center gap-1 text-center text-label leading-tight font-medium',
                  isActive ? 'text-text' : 'text-text-muted',
                )
              }
            >
              {({ isActive }) => (
                <>
                  <span
                    className={cn(
                      'grid h-8 w-14 place-items-center rounded-full transition-colors motion-reduce:transition-none',
                      isActive && 'bg-sign text-on-sign',
                    )}
                  >
                    <Icon name={item.icon} className="size-6" />
                  </span>
                  <span>{item.short ?? item.label}</span>
                </>
              )}
            </NavLink>
          ))}
      </nav>
    </div>
  );
}
