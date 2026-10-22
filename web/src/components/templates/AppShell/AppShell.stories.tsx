import type { Meta, StoryObj } from '@storybook/react-vite';
import { MemoryRouter } from 'react-router';

import { Button } from '../../atoms/Button/Button';
import { Text } from '../../atoms/Text/Text';
import { AppShell, type NavItem } from './AppShell';

const nav: NavItem[] = [
  { to: '/', label: 'Hoy', icon: 'today' },
  { to: '/medicamentos', label: 'Medicamentos', icon: 'medications' },
  { to: '/agenda', label: 'Agenda', icon: 'agenda' },
  { to: '/adherencia', label: 'Historial', icon: 'history' },
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
    appName: 'Recuerda',
    title: 'Hoy',
    icon: 'today',
    nav,
    actions: (
      <Button variant="ghost" icon="logout" className="text-on-sign">
        Salir
      </Button>
    ),
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
