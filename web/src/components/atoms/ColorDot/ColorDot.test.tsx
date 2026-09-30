import { render } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { ColorDot } from './ColorDot';

describe('ColorDot', () => {
  it('es decorativo y aplica la clase del token de color', () => {
    const { container } = render(<ColorDot color="teal" />);
    const dot = container.firstElementChild;
    expect(dot).toHaveAttribute('aria-hidden', 'true');
    expect(dot).toHaveClass('bg-med-teal');
  });
});
