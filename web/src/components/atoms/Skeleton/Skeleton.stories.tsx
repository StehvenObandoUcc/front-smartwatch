import type { Meta, StoryObj } from '@storybook/react-vite';

import { Skeleton } from './Skeleton';

const meta = {
  title: 'Atoms/Skeleton',
  component: Skeleton,
} satisfies Meta<typeof Skeleton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const List: Story = {};

export const OneRow: Story = { args: { rows: 1, label: 'Cargando tu día' } };
