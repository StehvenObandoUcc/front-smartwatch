import { createBrowserRouter, Navigate } from 'react-router';

import { AdherencePage } from '../pages/AdherencePage';
import { AgendaPage } from '../pages/AgendaPage';
import { ForgotPasswordPage } from '../pages/ForgotPasswordPage';
import { ChatPage } from '../pages/ChatPage';
import { HomePage } from '../pages/HomePage';
import { LoginPage } from '../pages/LoginPage';
import { MedicationsPage } from '../pages/MedicationsPage';
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
      // Los avisos viven ahora en Ajustes; se conserva la ruta vieja para enlaces guardados.
      { path: 'notificaciones', element: <Navigate to="/ajustes" replace /> },
      { path: 'reportes', element: <ReportsPage /> },
      { path: 'asistente', element: <ChatPage /> },
      { path: 'patients/:patientId/reports/:reportId', element: <ReportPage /> },
    ],
  },
]);
