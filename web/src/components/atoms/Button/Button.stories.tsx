import type { Meta, StoryObj } from '@storybook/react-vite';
import { expect, fn, userEvent } from 'storybook/test';

import { Button } from './Button';

const meta = {
  title: 'Atoms/Button',
  component: Button,
  args: { children: 'Guardar', onClick: fn() },
  argTypes: { variant: { control: 'inline-radio' } },
} satisfies Meta<typeof Button>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Primary: Story = {
  play: async ({ args, canvas }) => {
    await userEvent.click(canvas.getByRole('button', { name: 'Guardar' }));
    await expect(args.onClick).toHaveBeenCalled();
  },
};

export const Secondary: Story = { args: { variant: 'secondary' } };

export const Danger: Story = { args: { variant: 'danger', children: 'Eliminar' } };

export const Ghost: Story = { args: { variant: 'ghost', children: 'Cancelar' } };

export const WithIcon: Story = {
  args: { icon: 'add', children: 'Agregar medicamento' },
};

export const Loading: Story = { args: { loading: true } };

export const Disabled: Story = { args: { disabled: true } };

export const FullWidth: Story = { args: { fullWidth: true } };
