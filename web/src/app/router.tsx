import type { ComponentType } from 'react';
import { createBrowserRouter, Navigate } from 'react-router';

import { ForgotPasswordPage } from '../pages/ForgotPasswordPage';
import { HomePage } from '../pages/HomePage';
import { LoginPage } from '../pages/LoginPage';
import { RegisterPage } from '../pages/RegisterPage';
import { ResetPasswordPage } from '../pages/ResetPasswordPage';
import { VerifyEmailPage } from '../pages/VerifyEmailPage';
import { RootLayout } from './RootLayout';
import { RouteError } from './RouteError';

// Las pantallas internas se cargan al visitarlas: la landing y Hoy no esperan por las demás.
function page<K extends string>(load: () => Promise<Record<K, ComponentType>>, name: K) {
  return async () => ({ Component: (await load())[name] });
}

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  { path: '/registro', element: <RegisterPage /> },
  { path: '/recuperar', element: <ForgotPasswordPage /> },
  { path: '/reset-password', element: <ResetPasswordPage /> },
  { path: '/verify-email', element: <VerifyEmailPage /> },
  {
    element: <RootLayout />,
    errorElement: <RouteError />,
    children: [
      { index: true, element: <HomePage /> },
      {
        path: 'medicamentos',
        lazy: page(() => import('../pages/MedicationsPage'), 'MedicationsPage'),
      },
      { path: 'agenda', lazy: page(() => import('../pages/AgendaPage'), 'AgendaPage') },
      { path: 'adherencia', lazy: page(() => import('../pages/AdherencePage'), 'AdherencePage') },
      { path: 'ajustes', lazy: page(() => import('../pages/SettingsPage'), 'SettingsPage') },
      {
        path: 'avisos',
        lazy: page(() => import('../pages/NotificationsPage'), 'NotificationsPage'),
      },
      // Ruta antigua: enlaces guardados.
      { path: 'notificaciones', element: <Navigate to="/avisos" replace /> },
      { path: 'reportes', lazy: page(() => import('../pages/ReportsPage'), 'ReportsPage') },
      { path: 'asistente', lazy: page(() => import('../pages/ChatPage'), 'ChatPage') },
      { path: 'mas', lazy: page(() => import('../pages/MorePage'), 'MorePage') },
      {
        path: 'patients/:patientId/reports/:reportId',
        lazy: page(() => import('../pages/ReportPage'), 'ReportPage'),
      },
    ],
  },
]);
