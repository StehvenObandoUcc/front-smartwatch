import type { Meta, StoryObj } from '@storybook/react-vite';

import { Ring } from './Ring';

const meta = {
  title: 'Atoms/Ring',
  component: Ring,
  args: { value: 2, total: 3, children: <strong className="text-2xl">2</strong> },
  argTypes: { tone: { control: 'inline-radio', options: ['default', 'watch'] } },
} satisfies Meta<typeof Ring>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Parcial: Story = {};

export const Completo: Story = {
  args: { value: 3, children: <strong className="text-2xl">3</strong> },
};

export const Vacio: Story = { args: { value: 0, total: 0, children: <span>Libre</span> } };

export const EnElReloj: Story = {
  args: { tone: 'watch' },
  decorators: [
    (Story) => (
      <div className="bg-watch-bg p-4 text-watch-fg">
        <Story />
      </div>
    ),
  ],
};
