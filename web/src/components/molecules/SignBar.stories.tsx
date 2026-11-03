import type { Meta, StoryObj } from '@storybook/react-vite';

import { SignBar } from './SignBar';

const meta = {
  title: 'Molecules/SignBar',
  component: SignBar,
  args: { icon: 'morning', title: 'Mañana', meta: '5:00 a 12:00' },
  argTypes: {
    tone: { control: 'inline-radio', options: ['sign', 'sun', 'dusk', 'night', 'neutral'] },
  },
} satisfies Meta<typeof SignBar>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Senal: Story = {};

export const Manana: Story = { args: { tone: 'sun' } };

export const Tarde: Story = { args: { tone: 'dusk', icon: 'afternoon', title: 'Tarde' } };

export const Noche: Story = { args: { tone: 'night', icon: 'night', title: 'Noche' } };

export const SinDato: Story = { args: { tone: 'neutral', meta: undefined } };
