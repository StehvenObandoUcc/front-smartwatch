import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { Skeleton } from './Skeleton';

describe('Skeleton', () => {
  it('anuncia que carga y dibuja una fila por elemento', () => {
    const { container } = render(<Skeleton rows={2} label="Cargando agenda" />);
    expect(screen.getByRole('status')).toHaveTextContent('Cargando agenda');
    expect(container.querySelectorAll('[aria-hidden="true"]')).toHaveLength(2);
  });
});
