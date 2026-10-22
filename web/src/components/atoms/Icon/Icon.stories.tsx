import type { Meta, StoryObj } from '@storybook/react-vite';

import { Text } from '../Text/Text';
import { Icon } from './Icon';
import { iconNames } from './icons';

const meta = {
  title: 'Atoms/Icon',
  component: Icon,
  args: { name: 'medications' },
  argTypes: { name: { control: 'select', options: iconNames } },
} satisfies Meta<typeof Icon>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Decorative: Story = {};

export const Labelled: Story = { args: { name: 'alert', label: 'Aviso' } };

/** El vocabulario completo: una opción o categoría por pictograma. */
export const Vocabulary: Story = {
  render: () => (
    <ul className="grid grid-cols-4 gap-4">
      {iconNames.map((name) => (
        <li key={name} className="flex flex-col items-center gap-1">
          <Icon name={name} />
          <Text variant="caption">{name}</Text>
        </li>
      ))}
    </ul>
  ),
};
