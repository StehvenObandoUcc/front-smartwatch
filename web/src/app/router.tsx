import { createBrowserRouter } from 'react-router';

import { HomePage } from '../pages/HomePage';
import { PlaceholderPage } from '../pages/PlaceholderPage';
import { navItems } from './navigation';
import { RootLayout } from './RootLayout';

export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: [
      { index: true, element: <HomePage /> },
      ...navItems
        .filter((item) => item.to !== '/')
        .map((item) => ({ path: item.to, element: <PlaceholderPage name={item.label} /> })),
    ],
  },
]);
