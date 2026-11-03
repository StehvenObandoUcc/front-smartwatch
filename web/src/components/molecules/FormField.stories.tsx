import type { Meta, StoryObj } from '@storybook/react-vite';

import { Input } from '../atoms/Input/Input';
import { FormField } from './FormField';

const meta = {
  title: 'Molecules/FormField',
  component: FormField,
  args: { label: 'Nombre del medicamento', children: (p) => <Input {...p} /> },
} satisfies Meta<typeof FormField>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Normal: Story = {};

export const ConAyuda: Story = { args: { hint: 'Como aparece en la caja, p. ej. «Losartán».' } };

export const ConError: Story = { args: { error: 'Escribe el nombre' } };

export const Deshabilitado: Story = {
  args: { children: (p) => <Input {...p} disabled defaultValue="Metformina" /> },
};
