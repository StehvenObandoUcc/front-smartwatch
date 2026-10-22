import type { Meta, StoryObj } from '@storybook/react-vite';

import { medicationHex } from '../../../design/tokens';
import { Plate } from './Plate';

const meta = {
  title: 'Atoms/Plate',
  component: Plate,
  args: { icon: 'medications' },
} satisfies Meta<typeof Plate>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Sign: Story = {};

export const Sizes: Story = {
  render: () => (
    <div className="flex items-end gap-3">
      <Plate icon="today" size="sm" />
      <Plate icon="today" size="md" />
      <Plate icon="today" size="lg" />
    </div>
  ),
};

/** Las franjas del día: sol, tarde y noche. */
export const DayParts: Story = {
  render: () => (
    <div className="flex gap-3">
      <Plate icon="morning" tone="sun" />
      <Plate icon="afternoon" tone="dusk" />
      <Plate icon="night" tone="night" />
    </div>
  ),
};

/** Un medicamento con el color que eligió la persona (el nombre va siempre al lado). */
export const Medication: Story = { args: { icon: 'medications', hex: medicationHex.blue } };

export const Labelled: Story = { args: { icon: 'watch', label: 'Reloj' } };
