import { createBrowserRouter } from 'react-router';

import { AdherencePage } from '../pages/AdherencePage';
import { AgendaPage } from '../pages/AgendaPage';
import { ForgotPasswordPage } from '../pages/ForgotPasswordPage';
import { HomePage } from '../pages/HomePage';
import { LoginPage } from '../pages/LoginPage';
import { MedicationsPage } from '../pages/MedicationsPage';
import { NotificationsPage } from '../pages/NotificationsPage';
import { RegisterPage } from '../pages/RegisterPage';
import { ReportPage } from '../pages/ReportPage';
import { ReportsPage } from '../pages/ReportsPage';
import { ResetPasswordPage } from '../pages/ResetPasswordPage';
import { SettingsPage } from '../pages/SettingsPage';
import { VerifyEmailPage } from '../pages/VerifyEmailPage';
import { RootLayout } from './RootLayout';

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  { path: '/registro', element: <RegisterPage /> },
  { path: '/recuperar', element: <ForgotPasswordPage /> },
  { path: '/reset-password', element: <ResetPasswordPage /> },
  { path: '/verify-email', element: <VerifyEmailPage /> },
  {
    element: <RootLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: 'medicamentos', element: <MedicationsPage /> },
      { path: 'agenda', element: <AgendaPage /> },
      { path: 'adherencia', element: <AdherencePage /> },
      { path: 'ajustes', element: <SettingsPage /> },
      { path: 'notificaciones', element: <NotificationsPage /> },
      { path: 'reportes', element: <ReportsPage /> },
      { path: 'patients/:patientId/reports/:reportId', element: <ReportPage /> },
    ],
  },
]);
