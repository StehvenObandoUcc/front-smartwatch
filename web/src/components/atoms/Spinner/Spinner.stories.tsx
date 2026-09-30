import type { Meta, StoryObj } from '@storybook/react-vite';

import { Spinner } from './Spinner';

const meta = {
  title: 'Atoms/Spinner',
  component: Spinner,
} satisfies Meta<typeof Spinner>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Medium: Story = {};

export const Small: Story = { args: { size: 'sm' } };
