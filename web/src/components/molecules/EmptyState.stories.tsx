import type { Meta, StoryObj } from '@storybook/react-vite';

import { Button } from '../atoms/Button/Button';
import { EmptyState } from './EmptyState';

const meta = {
  title: 'Molecules/EmptyState',
  component: EmptyState,
  args: {
    icon: 'medications',
    title: 'Aún no hay medicamentos',
    body: 'Agrega el primero y el reloj empezará a avisar a su hora.',
  },
} satisfies Meta<typeof EmptyState>;

export default meta;
type Story = StoryObj<typeof meta>;

export const SinAccion: Story = {};

export const ConAccion: Story = {
  args: { action: <Button icon="add">Agregar medicamento</Button> },
};
