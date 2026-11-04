import type { TFunction } from 'i18next';

import type { IconName } from '../components/atoms/Icon/Icon';
import type { NavItem } from '../components/templates/AppShell/AppShell';

// Móvil: cuatro señales (Hoy, Medicinas, Historial y Más); lo demás se encuentra en «Más».
// Tablet y escritorio: todas las secciones en el riel; «Más» no hace falta.
export function navItems(t: TFunction): NavItem[] {
  return [
    { to: '/', label: t('nav.today'), icon: 'today' },
    { to: '/medicamentos', label: t('nav.medications'), icon: 'medications' },
    { to: '/agenda', label: t('nav.agenda'), icon: 'agenda', mobile: false },
    { to: '/adherencia', label: t('nav.history'), icon: 'history' },
    { to: '/reportes', label: t('nav.reports'), icon: 'reports', mobile: false },
    { to: '/asistente', label: t('nav.assistant'), icon: 'assistant', mobile: false },
    { to: '/avisos', label: t('nav.notifications'), icon: 'notifications', mobile: false },
    { to: '/ajustes', label: t('nav.settings'), icon: 'settings', mobile: false },
    { to: '/mas', label: t('nav.more'), icon: 'more', desktop: false },
  ];
}

/** Título y pictograma de la cabecera para una ruta. */
export function headerFor(pathname: string, t: TFunction): { title: string; icon: IconName } {
  const item = navItems(t).find((nav) => nav.to === pathname);
  if (item) return { title: item.label, icon: item.icon };
  // Pantalla fuera del menú: enlace directo al reporte desde correo o Telegram.
  if (pathname.startsWith('/patients')) return { title: t('nav.report'), icon: 'reports' };
  return { title: 'Recuerda', icon: 'today' };
}
