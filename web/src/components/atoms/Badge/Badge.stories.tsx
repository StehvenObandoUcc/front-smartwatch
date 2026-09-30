import type { Meta, StoryObj } from '@storybook/react-vite';

import { Badge } from './Badge';

const meta = {
  title: 'Atoms/Badge',
  component: Badge,
  args: { children: 'Pendiente' },
} satisfies Meta<typeof Badge>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Neutral: Story = {};

export const Success: Story = { args: { tone: 'success', children: 'Tomada' } };

export const Warning: Story = { args: { tone: 'warning', children: 'Pospuesta' } };

export const Danger: Story = { args: { tone: 'danger', children: 'Omitida' } };
