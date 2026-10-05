import type { NavItem } from '../components/templates/AppShell/AppShell';

export const navItems: NavItem[] = [
  { to: '/', label: 'Inicio', icon: 'home' },
  { to: '/medicamentos', label: 'Medicamentos', icon: 'pill' },
  { to: '/agenda', label: 'Agenda', icon: 'calendar' },
  { to: '/adherencia', label: 'Adherencia', icon: 'chart' },
  { to: '/ajustes', label: 'Ajustes', icon: 'settings' },
];

// Pantallas fuera del menú principal: solo aportan el título de la cabecera.
export const extraTitles: Record<string, string> = {
  '/asistente': 'Asistente',
  '/reportes': 'Reportes',
  // Enlace directo desde correo o Telegram.
  '/patients': 'Reporte',
};
