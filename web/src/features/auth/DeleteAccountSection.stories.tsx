import type { Meta, StoryObj } from '@storybook/react-vite';
import { expect, fn, userEvent } from 'storybook/test';

import { DeleteAccountPanel } from './DeleteAccountSection';

const meta = {
  title: 'Features/Auth/DeleteAccountSection',
  component: DeleteAccountPanel,
  args: { userRole: 'patient', onConfirm: fn(), onCancel: fn() },
  argTypes: { userRole: { control: 'inline-radio', options: ['patient', 'caregiver'] } },
} satisfies Meta<typeof DeleteAccountPanel>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Colapsado: Story = {
  play: async ({ canvas }) => {
    await userEvent.click(canvas.getByRole('button', { name: 'Eliminar mi cuenta' }));
    await expect(canvas.getByLabelText('Tu contraseña actual')).toHaveFocus();
  },
};

export const ExpandidoPaciente: Story = { args: { initialOpen: true } };

export const ExpandidoCuidador: Story = { args: { initialOpen: true, userRole: 'caregiver' } };

export const ErrorContrasena: Story = {
  args: { initialOpen: true, error: 'La contraseña no es correcta.' },
};

export const Enviando: Story = { args: { initialOpen: true, pending: true } };

export const ValidacionVacia: Story = {
  args: { initialOpen: true },
  play: async ({ args, canvas }) => {
    await userEvent.click(canvas.getByRole('button', { name: 'Eliminar definitivamente' }));
    await expect(await canvas.findByText('Escribe tu contraseña')).toBeInTheDocument();
    await expect(args.onConfirm).not.toHaveBeenCalled();
  },
};
