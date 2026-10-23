import type { IconName } from '../components/atoms/Icon/Icon';
import type { NavItem } from '../components/templates/AppShell/AppShell';

// Móvil: cuatro señales (Hoy, Medicinas, Historial y Más); lo demás se encuentra en «Más».
// Tablet y escritorio: todas las secciones en el riel; «Más» no hace falta.
export const navItems: NavItem[] = [
  { to: '/', label: 'Hoy', icon: 'today' },
  { to: '/medicamentos', label: 'Medicinas', icon: 'medications' },
  { to: '/agenda', label: 'Agenda', icon: 'agenda', mobile: false },
  { to: '/adherencia', label: 'Historial', icon: 'history' },
  { to: '/reportes', label: 'Reportes', icon: 'reports', mobile: false },
  { to: '/asistente', label: 'Asistente', icon: 'assistant', mobile: false },
  { to: '/avisos', label: 'Avisos', icon: 'notifications', mobile: false },
  { to: '/ajustes', label: 'Ajustes', icon: 'settings', mobile: false },
  { to: '/mas', label: 'Más', icon: 'more', desktop: false },
];

// Pantallas fuera del menú: su título y su pictograma en la cabecera.
export const extraTitles: Record<string, { title: string; icon: IconName }> = {
  // Enlace directo desde correo o Telegram.
  '/patients': { title: 'Reporte', icon: 'reports' },
};

/** Título y pictograma de la cabecera para una ruta. */
export function headerFor(pathname: string): { title: string; icon: IconName } {
  const item = navItems.find((nav) => nav.to === pathname);
  if (item) return { title: item.label, icon: item.icon };
  const extra = Object.entries(extraTitles).find(([prefix]) => pathname.startsWith(prefix));
  return extra?.[1] ?? { title: 'Recuerda', icon: 'today' };
}
