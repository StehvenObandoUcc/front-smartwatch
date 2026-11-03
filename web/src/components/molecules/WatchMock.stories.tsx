import type { Meta, StoryObj } from '@storybook/react-vite';

import { WatchMock } from './WatchMock';

const meta = {
  title: 'Molecules/WatchMock',
  component: WatchMock,
} satisfies Meta<typeof WatchMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Normal: Story = {};

/** La alerta de una dosis, como suena en la muñeca. */
export const Alarma: Story = { args: { screen: 'alarm' } };
