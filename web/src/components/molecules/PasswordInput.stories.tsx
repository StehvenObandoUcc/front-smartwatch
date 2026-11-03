import type { Meta, StoryObj } from '@storybook/react-vite';
import { expect, userEvent } from 'storybook/test';

import { FormField } from './FormField';
import { PasswordInput } from './PasswordInput';

const meta = {
  title: 'Molecules/PasswordInput',
  component: PasswordInput,
  render: (args) => (
    <FormField label="Contraseña">{(p) => <PasswordInput {...p} {...args} />}</FormField>
  ),
} satisfies Meta<typeof PasswordInput>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Normal: Story = {
  args: { defaultValue: 'una-clave-larga' },
  play: async ({ canvas }) => {
    await userEvent.click(canvas.getByRole('button', { name: 'Mostrar' }));
    await expect(canvas.getByLabelText('Contraseña')).toHaveAttribute('type', 'text');
  },
};

export const ConError: Story = {
  render: (args) => (
    <FormField label="Contraseña" error="Escribe tu contraseña">
      {(p) => <PasswordInput {...p} {...args} />}
    </FormField>
  ),
};

export const Deshabilitado: Story = { args: { disabled: true, defaultValue: 'secreta' } };
