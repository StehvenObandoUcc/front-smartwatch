import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';

import { ThemeSwitch } from './ThemeSwitch';

describe('ThemeSwitch', () => {
  it('marca la elección y avisa al cambiarla', async () => {
    const onChange = vi.fn();
    render(<ThemeSwitch value="system" onChange={onChange} compact />);
    expect(screen.getByRole('radio', { name: 'Sistema' })).toBeChecked();
    await userEvent.click(screen.getByRole('radio', { name: 'Oscuro' }));
    expect(onChange).toHaveBeenCalledWith('dark');
  });
});
