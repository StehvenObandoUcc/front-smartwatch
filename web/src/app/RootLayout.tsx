import { Outlet, useLocation } from 'react-router';

import { AppShell } from '../components/templates/AppShell/AppShell';
import { navItems } from './navigation';

export function RootLayout() {
  const { pathname } = useLocation();
  const title = navItems.find((item) => item.to === pathname)?.label ?? 'Recordatorios';
  return (
    <AppShell appName="Recordatorios" title={title} nav={navItems}>
      <Outlet />
    </AppShell>
  );
}
