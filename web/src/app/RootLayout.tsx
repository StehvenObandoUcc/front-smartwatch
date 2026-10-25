import { lazy, Suspense } from 'react';
import { Navigate, Outlet, useLocation } from 'react-router';

import { Button } from '../components/atoms/Button/Button';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { AppShell } from '../components/templates/AppShell/AppShell';
import { useAuth } from '../features/auth/AuthContext';
import { PatientGate } from '../features/patients/PatientGate';
import { VerifyEmailBanner } from '../features/auth/VerifyEmailBanner';
import { headerFor, navItems } from './navigation';

// La landing solo la descarga quien llega sin sesión.
const LandingPage = lazy(() =>
  import('../pages/LandingPage').then((m) => ({ default: m.LandingPage })),
);

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
    // En la raíz, quien no ha entrado ve la landing; en cualquier otra ruta, la entrada.
    if (pathname === '/') {
      return (
        <Suspense fallback={null}>
          <LandingPage />
        </Suspense>
      );
    }
    const next = pathname === '/' ? '' : `?next=${encodeURIComponent(pathname + search)}`;
    return <Navigate to={`/login${next}`} replace />;
  }
  const header = headerFor(pathname);
  const now = new Date();
  const longDay = now.toLocaleDateString('es', { weekday: 'long', day: 'numeric', month: 'long' });
  const shortDay = now.toLocaleDateString('es', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
  });
  return (
    <AppShell
      appName="Recuerda"
      title={header.title}
      icon={header.icon}
      subtitle={
        // En el móvil, quién y el día corto; desde tablet, también el papel y el día largo.
        <>
          <span className="hidden tablet:inline">
            {auth.user.role === 'caregiver' ? 'Cuidador' : 'Paciente'} ·{' '}
          </span>
          {auth.user.displayName} ·{' '}
          <span className="whitespace-nowrap tablet:hidden">{shortDay}</span>
          <span className="hidden whitespace-nowrap tablet:inline">{longDay}</span>
        </>
      }
      nav={navItems}
      actions={
        // En el móvil solo el pictograma (la palabra queda para el lector): el título no se corta.
        <Button variant="onSign" icon="logout" className="px-3" onClick={() => void signOut()}>
          <span className="sr-only tablet:not-sr-only">Salir</span>
        </Button>
      }
    >
      {auth.user.emailVerified === false && <VerifyEmailBanner />}
      <PatientGate>
        <Outlet />
      </PatientGate>
    </AppShell>
  );
}
