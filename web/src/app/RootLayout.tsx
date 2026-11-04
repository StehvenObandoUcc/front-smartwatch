import { lazy, Suspense } from 'react';
import { useTranslation } from 'react-i18next';
import { Navigate, Outlet, useLocation } from 'react-router';

import { Button } from '../components/atoms/Button/Button';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { ThemeSwitch } from '../components/molecules/ThemeSwitch';
import { AppShell } from '../components/templates/AppShell/AppShell';
import { useAuth } from '../features/auth/AuthContext';
import { PatientGate } from '../features/patients/PatientGate';
import { useAccountLanguage } from '../features/auth/useAccountLanguage';
import { VerifyEmailBanner } from '../features/auth/VerifyEmailBanner';
import { useTheme } from '../lib/theme';
import { headerFor, navItems } from './navigation';

// La landing solo la descarga quien llega sin sesión.
const LandingPage = lazy(() =>
  import('../pages/LandingPage').then((m) => ({ default: m.LandingPage })),
);

export function RootLayout() {
  const { pathname, search } = useLocation();
  const { t, i18n } = useTranslation();
  const { auth, signOut } = useAuth();
  useAccountLanguage(auth.status === 'authed' ? auth.user : null);
  // El tema se aplica también en la landing (sin sesión).
  const theme = useTheme();
  if (auth.status === 'loading') {
    // El backend puede tardar en despertar: se dice qué pasa, centrado, en lugar de un spinner suelto.
    return (
      <div className="flex min-h-dvh flex-col items-center justify-center gap-3">
        <Spinner label={t('shell.openingSession')} />
        {/* El lector de pantalla ya lo oye por el Spinner (role=status). */}
        <span aria-hidden="true">
          <Text tone="muted">{t('shell.openingSession')}…</Text>
        </span>
      </div>
    );
  }
  if (auth.status === 'anon') {
    // Tras eliminar la cuenta no hay a dónde volver: el login muestra el aviso.
    if (auth.accountDeleted) return <Navigate to="/login?cuentaEliminada=1" replace />;
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
  const header = headerFor(pathname, t);
  const now = new Date();
  const longDay = now.toLocaleDateString(i18n.language, {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
  });
  const shortDay = now.toLocaleDateString(i18n.language, {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
  });
  return (
    <AppShell
      appName="Recuerda"
      title={header.title}
      subtitle={
        // En el móvil, quién y el día corto; desde tablet, también el papel y el día largo.
        <>
          <span className="hidden tablet:inline">
            {auth.user.role === 'caregiver' ? t('shell.caregiver') : t('shell.patient')} ·{' '}
          </span>
          {auth.user.displayName} ·{' '}
          <span className="whitespace-nowrap tablet:hidden">{shortDay}</span>
          <span className="hidden whitespace-nowrap tablet:inline">{longDay}</span>
        </>
      }
      nav={navItems(t)}
      actions={
        // En el móvil, el tema compacto y solo el pictograma de salir: el título no se corta.
        <>
          <ThemeSwitch compact value={theme.choice} onChange={theme.setChoice} />
          <Button variant="quiet" icon="logout" className="px-3" onClick={() => void signOut()}>
            <span className="sr-only tablet:not-sr-only">{t('shell.signOut')}</span>
          </Button>
        </>
      }
    >
      {auth.user.emailVerified === false && <VerifyEmailBanner />}
      <PatientGate>
        <Outlet />
      </PatientGate>
    </AppShell>
  );
}
