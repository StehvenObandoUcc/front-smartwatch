import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { Badge } from './Badge';

describe('Badge', () => {
  it('muestra su texto', () => {
    render(<Badge tone="danger">Omitida</Badge>);
    expect(screen.getByText('Omitida')).toBeInTheDocument();
  });
});
