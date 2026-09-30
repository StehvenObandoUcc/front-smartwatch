import type { Meta, StoryObj } from '@storybook/react-vite';

import { medicationColors } from '../../../design/tokens';
import { Text } from '../Text/Text';
import { ColorDot } from './ColorDot';

const meta = {
  title: 'Atoms/ColorDot',
  component: ColorDot,
  args: { color: 'blue' },
  argTypes: { color: { control: 'select', options: medicationColors } },
} satisfies Meta<typeof ColorDot>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {};

export const Small: Story = { args: { size: 'sm' } };

// El color siempre va acompañado del nombre.
export const Palette: Story = {
  render: () => (
    <ul className="flex flex-col gap-2">
      {medicationColors.map((color) => (
        <li key={color} className="flex items-center gap-2">
          <ColorDot color={color} />
          <Text as="span">{color}</Text>
        </li>
      ))}
    </ul>
  ),
};
