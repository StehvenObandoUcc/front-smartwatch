import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';

import { Button } from './Button';

describe('Button', () => {
  it('llama onClick al pulsarlo', async () => {
    const onClick = vi.fn();
    render(<Button onClick={onClick}>Guardar</Button>);
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(onClick).toHaveBeenCalledOnce();
  });

  it('no responde deshabilitado', async () => {
    const onClick = vi.fn();
    render(
      <Button onClick={onClick} disabled>
        Guardar
      </Button>,
    );
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(onClick).not.toHaveBeenCalled();
  });

  it('cargando: se deshabilita, marca aria-busy y anuncia el estado', () => {
    render(<Button loading>Guardar</Button>);
    const button = screen.getByRole('button', { name: /Guardar/ });
    expect(button).toBeDisabled();
    expect(button).toHaveAttribute('aria-busy', 'true');
    expect(screen.getByRole('status')).toHaveTextContent('Procesando');
  });

  it('es type="button" por defecto para no enviar formularios', () => {
    render(<Button>Guardar</Button>);
    expect(screen.getByRole('button')).toHaveAttribute('type', 'button');
  });
});
