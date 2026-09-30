import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router';
import { describe, expect, it } from 'vitest';

import { AppShell, type NavItem } from './AppShell';

const nav: NavItem[] = [
  { to: '/', label: 'Inicio', icon: 'home' },
  { to: '/medicamentos', label: 'Medicamentos', icon: 'pill' },
];

function renderShell(path = '/') {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route
          path="*"
          element={
            <AppShell appName="Recordatorios" title="Inicio" nav={nav}>
              <p>Contenido</p>
            </AppShell>
          }
        />
      </Routes>
    </MemoryRouter>,
  );
}

describe('AppShell', () => {
  it('tiene cabecera, principal y dos navegaciones (lateral y móvil)', () => {
    renderShell();
    expect(screen.getByRole('banner')).toHaveTextContent('Inicio');
    expect(screen.getByRole('main')).toHaveTextContent('Contenido');
    expect(screen.getByRole('navigation', { name: 'Principal' })).toBeInTheDocument();
    expect(screen.getByRole('navigation', { name: 'Principal móvil' })).toBeInTheDocument();
  });

  it('marca la ruta activa con aria-current', () => {
    renderShell('/medicamentos');
    const side = screen.getByRole('navigation', { name: 'Principal' });
    expect(within(side).getByRole('link', { name: 'Medicamentos' })).toHaveAttribute(
      'aria-current',
      'page',
    );
    expect(within(side).getByRole('link', { name: 'Inicio' })).not.toHaveAttribute('aria-current');
  });

  it('el primer tabulador lleva a "Saltar al contenido", que apunta al main', async () => {
    renderShell();
    await userEvent.tab();
    const skip = screen.getByRole('link', { name: 'Saltar al contenido' });
    expect(skip).toHaveFocus();
    expect(skip).toHaveAttribute('href', `#${screen.getByRole('main').id}`);
  });
});
