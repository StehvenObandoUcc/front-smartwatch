import type { Meta, StoryObj } from '@storybook/react-vite';
import { expect, userEvent } from 'storybook/test';

import { Input } from './Input';

const meta = {
  title: 'Atoms/Input',
  component: Input,
  args: { 'aria-label': 'Nombre del medicamento', placeholder: 'Ej.: Losartán' },
} satisfies Meta<typeof Input>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Empty: Story = {
  play: async ({ canvas }) => {
    const input = canvas.getByRole('textbox');
    await userEvent.type(input, 'Losartán');
    await expect(input).toHaveValue('Losartán');
  },
};

export const Filled: Story = { args: { defaultValue: 'Metformina' } };

export const Invalid: Story = { args: { invalid: true, defaultValue: '' } };

export const Disabled: Story = { args: { disabled: true, defaultValue: 'Metformina' } };
