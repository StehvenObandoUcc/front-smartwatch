import type { Meta, StoryObj } from '@storybook/react-vite';

import { Text } from '../Text/Text';
import { Icon, type IconName } from './Icon';

const names: IconName[] = [
  'home',
  'pill',
  'calendar',
  'chart',
  'bell',
  'watch',
  'settings',
  'alert',
];

const meta = {
  title: 'Atoms/Icon',
  component: Icon,
  args: { name: 'pill' },
  argTypes: { name: { control: 'select', options: names } },
} satisfies Meta<typeof Icon>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Decorative: Story = {};

export const Labelled: Story = { args: { name: 'alert', label: 'Aviso' } };

export const All: Story = {
  render: () => (
    <ul className="grid grid-cols-4 gap-4">
      {names.map((name) => (
        <li key={name} className="flex flex-col items-center gap-1">
          <Icon name={name} />
          <Text variant="caption">{name}</Text>
        </li>
      ))}
    </ul>
  ),
};
