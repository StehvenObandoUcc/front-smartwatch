import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';

import { PasswordInput } from './PasswordInput';

describe('PasswordInput', () => {
  it('muestra y oculta la contraseña', async () => {
    render(
      <>
        <label htmlFor="clave">Contraseña</label>
        <PasswordInput id="clave" defaultValue="secreta" />
      </>,
    );
    const field = screen.getByLabelText('Contraseña');
    expect(field).toHaveAttribute('type', 'password');
    await userEvent.click(screen.getByRole('button', { name: 'Mostrar' }));
    expect(field).toHaveAttribute('type', 'text');
    expect(screen.getByRole('button', { name: 'Ocultar' })).toHaveAttribute('aria-pressed', 'true');
  });
});
