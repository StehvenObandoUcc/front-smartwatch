import type { Meta, StoryObj } from '@storybook/react-vite';
import { useState } from 'react';
import { expect, userEvent } from 'storybook/test';

import type { ThemeChoice } from '../../lib/theme';
import { ThemeSwitch } from './ThemeSwitch';

const meta = {
  title: 'Molecules/ThemeSwitch',
  component: ThemeSwitch,
  args: { value: 'system', onChange: () => {} },
  render: function Render(args) {
    const [value, setValue] = useState<ThemeChoice>(args.value);
    return <ThemeSwitch {...args} value={value} onChange={setValue} />;
  },
} satisfies Meta<typeof ThemeSwitch>;

export default meta;
type Story = StoryObj<typeof meta>;

export const ConPalabras: Story = {
  play: async ({ canvas }) => {
    await userEvent.click(canvas.getByRole('radio', { name: 'Oscuro' }));
    await expect(canvas.getByRole('radio', { name: 'Oscuro' })).toBeChecked();
  },
};

export const Compacto: Story = { args: { compact: true, value: 'light' } };
