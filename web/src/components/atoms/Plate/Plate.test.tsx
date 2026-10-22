import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { Plate } from './Plate';

describe('Plate', () => {
  it('es decorativa sin label', () => {
    const { container } = render(<Plate icon="medications" />);
    expect(container.firstElementChild).toHaveAttribute('aria-hidden', 'true');
  });

  it('se anuncia como imagen con label', () => {
    render(<Plate icon="watch" label="Reloj" />);
    expect(screen.getByRole('img', { name: 'Reloj' })).toBeInTheDocument();
  });

  it('pinta el color del medicamento', () => {
    const { container } = render(<Plate icon="medications" hex="#1565c0" />);
    expect(container.firstElementChild).toHaveAttribute('data-color', '#1565c0');
  });
});
