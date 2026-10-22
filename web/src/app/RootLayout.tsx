import { Navigate, Outlet, useLocation } from 'react-router';

import { Button } from '../components/atoms/Button/Button';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { AppShell } from '../components/templates/AppShell/AppShell';
import { useAuth } from '../features/auth/AuthContext';
import { PatientGate } from '../features/patients/PatientGate';
import { VerifyEmailBanner } from '../features/auth/VerifyEmailBanner';
import { headerFor, navItems } from './navigation';

export function RootLayout() {
  const { pathname, search } = useLocation();
  const { auth, signOut } = useAuth();
  if (auth.status === 'loading') {
    // El backend puede tardar en despertar: se dice qué pasa, centrado, en lugar de un spinner suelto.
    return (
      <div className="flex min-h-dvh flex-col items-center justify-center gap-3">
        <Spinner label="Abriendo tu sesión" />
        {/* El lector de pantalla ya lo oye por el Spinner (role=status). */}
        <span aria-hidden="true">
          <Text tone="muted">Abriendo tu sesión…</Text>
        </span>
      </div>
    );
  }
  if (auth.status === 'anon') {
    const next = pathname === '/' ? '' : `?next=${encodeURIComponent(pathname + search)}`;
    return <Navigate to={`/login${next}`} replace />;
  }
  const header = headerFor(pathname);
  return (
    <AppShell
      appName="Recuerda"
      title={header.title}
      icon={header.icon}
      nav={navItems}
      actions={
        <div className="flex items-center gap-3">
          {/* Quién entra, en palabras claras (no «Particular»); en móvil no cabe junto al título. */}
          <span className="hidden text-caption font-bold text-sign-muted tablet:inline">
            {auth.user.role === 'caregiver' ? 'Cuidador' : 'Paciente'} · {auth.user.displayName}
          </span>
          {/* En el móvil solo el pictograma (la palabra queda para el lector): el título no se corta. */}
          <Button variant="onSign" icon="logout" className="px-3" onClick={() => void signOut()}>
            <span className="sr-only tablet:not-sr-only">Salir</span>
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
