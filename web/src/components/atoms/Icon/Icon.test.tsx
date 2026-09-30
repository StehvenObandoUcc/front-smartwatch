import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { Icon } from './Icon';

describe('Icon', () => {
  it('es decorativo sin label', () => {
    const { container } = render(<Icon name="pill" />);
    expect(container.querySelector('svg')).toHaveAttribute('aria-hidden', 'true');
  });

  it('se anuncia como imagen con label', () => {
    render(<Icon name="alert" label="Aviso" />);
    expect(screen.getByRole('img', { name: 'Aviso' })).toBeInTheDocument();
  });
});
