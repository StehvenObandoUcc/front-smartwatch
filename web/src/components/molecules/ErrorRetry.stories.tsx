import type { Meta, StoryObj } from '@storybook/react-vite';
import { expect, fn, userEvent } from 'storybook/test';

import { ErrorRetry } from './ErrorRetry';

const meta = {
  title: 'Molecules/ErrorRetry',
  component: ErrorRetry,
  args: { message: 'No pudimos conectar con el servidor. Inténtalo de nuevo.', onRetry: fn() },
} satisfies Meta<typeof ErrorRetry>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Normal: Story = {
  play: async ({ args, canvas }) => {
    await userEvent.click(canvas.getByRole('button', { name: 'Reintentar' }));
    await expect(args.onRetry).toHaveBeenCalled();
  },
};
