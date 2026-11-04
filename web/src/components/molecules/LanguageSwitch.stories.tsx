import type { Meta, StoryObj } from '@storybook/react-vite';
import { useState } from 'react';
import { expect, userEvent } from 'storybook/test';

import type { Language } from '../../i18n';
import { LanguageSwitch } from './LanguageSwitch';

const meta = {
  title: 'Molecules/LanguageSwitch',
  component: LanguageSwitch,
  args: { value: 'es', onChange: () => {} },
  render: function Render(args) {
    const [value, setValue] = useState<Language>(args.value);
    return <LanguageSwitch {...args} value={value} onChange={setValue} />;
  },
} satisfies Meta<typeof LanguageSwitch>;

export default meta;
type Story = StoryObj<typeof meta>;

export const ConNombres: Story = {
  play: async ({ canvas }) => {
    await userEvent.click(canvas.getByRole('radio', { name: 'English' }));
    await expect(canvas.getByRole('radio', { name: 'English' })).toBeChecked();
  },
};

export const Compacto: Story = { args: { compact: true } };
