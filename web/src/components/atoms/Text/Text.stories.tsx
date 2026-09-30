import type { Meta, StoryObj } from '@storybook/react-vite';

import { Text } from './Text';

const meta = {
  title: 'Atoms/Text',
  component: Text,
  args: { children: 'Paracetamol 500 mg a las 8:00' },
} satisfies Meta<typeof Text>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Body: Story = {};

export const Scale: Story = {
  render: () => (
    <div className="flex flex-col gap-3">
      <Text variant="display">Display</Text>
      <Text variant="title">Título</Text>
      <Text variant="subtitle">Subtítulo</Text>
      <Text>Texto base de 17 px, pensado para lectura cómoda.</Text>
      <Text variant="caption" tone="muted">
        Nota secundaria
      </Text>
    </div>
  ),
};

export const Tones: Story = {
  render: () => (
    <div className="flex flex-col gap-2">
      <Text>Normal</Text>
      <Text tone="muted">Secundario</Text>
      <Text tone="success">Dosis tomada</Text>
      <Text tone="danger">No se pudo guardar</Text>
    </div>
  ),
};
