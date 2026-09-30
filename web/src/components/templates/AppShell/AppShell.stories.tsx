import type { Meta, StoryObj } from '@storybook/react-vite';
import { MemoryRouter } from 'react-router';

import { Button } from '../../atoms/Button/Button';
import { Text } from '../../atoms/Text/Text';
import { AppShell, type NavItem } from './AppShell';

const nav: NavItem[] = [
  { to: '/', label: 'Inicio', icon: 'home' },
  { to: '/medicamentos', label: 'Medicamentos', icon: 'pill' },
  { to: '/agenda', label: 'Agenda', icon: 'calendar' },
  { to: '/adherencia', label: 'Adherencia', icon: 'chart' },
  { to: '/ajustes', label: 'Ajustes', icon: 'settings' },
];

const meta = {
  title: 'Templates/AppShell',
  component: AppShell,
  parameters: { layout: 'fullscreen' },
  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={['/']}>
        <Story />
      </MemoryRouter>
    ),
  ],
  args: {
    appName: 'Recordatorios',
    title: 'Inicio',
    nav,
    actions: <Button variant="ghost">Salir</Button>,
    children: (
      <div className="flex flex-col gap-4">
        <Text variant="subtitle" as="h2">
          Contenido de la página
        </Text>
        <Text tone="muted">El menú cambia de posición según el tamaño de pantalla.</Text>
      </div>
    ),
  },
} satisfies Meta<typeof AppShell>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Mobile: Story = { globals: { viewport: { value: 'mobile', isRotated: false } } };

export const Tablet: Story = { globals: { viewport: { value: 'tablet', isRotated: false } } };

export const Desktop: Story = { globals: { viewport: { value: 'desktop', isRotated: false } } };
