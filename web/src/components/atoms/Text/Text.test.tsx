import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { Text } from './Text';

describe('Text', () => {
  it('usa el elemento semántico de la variante', () => {
    render(<Text variant="display">Hola</Text>);
    expect(screen.getByRole('heading', { level: 1, name: 'Hola' })).toBeInTheDocument();
  });

  it('permite cambiar el elemento con as', () => {
    render(
      <Text variant="title" as="h3">
        Sección
      </Text>,
    );
    expect(screen.getByRole('heading', { level: 3, name: 'Sección' })).toBeInTheDocument();
  });
});
