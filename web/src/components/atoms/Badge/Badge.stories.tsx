import type { Meta, StoryObj } from '@storybook/react-vite';

import { Badge } from './Badge';

const meta = {
  title: 'Atoms/Badge',
  component: Badge,
  args: { children: 'Omitida', icon: 'skipped' },
} satisfies Meta<typeof Badge>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Neutral: Story = {};

export const Success: Story = { args: { tone: 'success', icon: 'taken', children: 'Tomada' } };

export const Warning: Story = { args: { tone: 'warning', icon: 'due', children: 'Pendiente' } };

export const Danger: Story = { args: { tone: 'danger', icon: 'missed', children: 'No tomada' } };

export const Outline: Story = {
  args: { tone: 'outline', icon: undefined, children: 'Correo sin verificar' },
};
