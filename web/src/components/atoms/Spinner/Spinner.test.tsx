import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { Spinner } from './Spinner';

describe('Spinner', () => {
  it('anuncia el estado de carga', () => {
    render(<Spinner label="Cargando medicamentos" />);
    expect(screen.getByRole('status')).toHaveTextContent('Cargando medicamentos');
  });
});
