import { createBrowserRouter } from 'react-router';

import { AdherencePage } from '../pages/AdherencePage';
import { AgendaPage } from '../pages/AgendaPage';
import { HomePage } from '../pages/HomePage';
import { LoginPage } from '../pages/LoginPage';
import { MedicationsPage } from '../pages/MedicationsPage';
import { RegisterPage } from '../pages/RegisterPage';
import { SettingsPage } from '../pages/SettingsPage';
import { RootLayout } from './RootLayout';

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  { path: '/registro', element: <RegisterPage /> },
  {
    element: <RootLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: 'medicamentos', element: <MedicationsPage /> },
      { path: 'agenda', element: <AgendaPage /> },
      { path: 'adherencia', element: <AdherencePage /> },
      { path: 'ajustes', element: <SettingsPage /> },
    ],
  },
]);
