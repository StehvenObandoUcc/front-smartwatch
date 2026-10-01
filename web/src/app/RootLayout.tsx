import { Navigate, Outlet, useLocation } from 'react-router';

import { Button } from '../components/atoms/Button/Button';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { AppShell } from '../components/templates/AppShell/AppShell';
import { useAuth } from '../features/auth/AuthContext';
import { PatientGate } from '../features/patients/PatientGate';
import { navItems } from './navigation';

export function RootLayout() {
  const { pathname } = useLocation();
  const { auth, signOut } = useAuth();
  if (auth.status === 'loading') return <Spinner label="Abriendo sesión" className="p-4" />;
  if (auth.status === 'anon') return <Navigate to="/login" replace />;
  const title = navItems.find((item) => item.to === pathname)?.label ?? 'Recordatorios';
  return (
    <AppShell
      appName="Recordatorios"
      title={title}
      nav={navItems}
      actions={
        <Button variant="ghost" onClick={() => void signOut()}>
          Salir
        </Button>
      }
    >
      <PatientGate>
        <Outlet />
      </PatientGate>
    </AppShell>
  );
}
