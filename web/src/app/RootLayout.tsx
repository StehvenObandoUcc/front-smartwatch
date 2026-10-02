import { Navigate, Outlet, useLocation } from 'react-router';

import { Badge } from '../components/atoms/Badge/Badge';
import { Button } from '../components/atoms/Button/Button';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { AppShell } from '../components/templates/AppShell/AppShell';
import { useAuth } from '../features/auth/AuthContext';
import { PatientGate } from '../features/patients/PatientGate';
import { VerifyEmailBanner } from '../features/auth/VerifyEmailBanner';
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
        <div className="flex items-center gap-2">
          <Badge>{auth.user.role === 'caregiver' ? 'Cuidador' : 'Particular'}</Badge>
          <Button variant="ghost" onClick={() => void signOut()}>
            Salir
          </Button>
        </div>
      }
    >
      {auth.user.emailVerified === false && <VerifyEmailBanner />}
      <PatientGate>
        <Outlet />
      </PatientGate>
    </AppShell>
  );
}
