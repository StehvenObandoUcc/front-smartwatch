import type { Meta, StoryObj } from '@storybook/react-vite';

import { MemoryRouter } from 'react-router';

import { Text } from '../atoms/Text/Text';
import { AuthLayout } from './AuthLayout';

const meta = {
  title: 'Templates/AuthLayout',
  component: AuthLayout,
  parameters: { layout: 'fullscreen' },
  decorators: [
    (Story) => (
      <MemoryRouter>
        <Story />
      </MemoryRouter>
    ),
  ],
  args: { title: 'Iniciar sesión', children: <Text>Aquí va el formulario.</Text> },
} satisfies Meta<typeof AuthLayout>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Normal: Story = {};

export const Movil: Story = { globals: { viewport: { value: 'mobile', isRotated: false } } };
