import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';

import { Input } from './Input';

describe('Input', () => {
  it('acepta texto', async () => {
    render(<Input aria-label="Nombre" />);
    await userEvent.type(screen.getByRole('textbox', { name: 'Nombre' }), 'Ana');
    expect(screen.getByRole('textbox')).toHaveValue('Ana');
  });

  it('marca aria-invalid cuando es inválido', () => {
    render(<Input aria-label="Nombre" invalid />);
    expect(screen.getByRole('textbox')).toHaveAttribute('aria-invalid', 'true');
  });

  it('no marca aria-invalid cuando es válido', () => {
    render(<Input aria-label="Nombre" />);
    expect(screen.getByRole('textbox')).not.toHaveAttribute('aria-invalid');
  });
});
