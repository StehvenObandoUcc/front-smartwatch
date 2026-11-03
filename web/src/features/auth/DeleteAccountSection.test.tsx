import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { ReactNode } from 'react';
import { MemoryRouter, Route, Routes, useLocation } from 'react-router';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ApiError } from '../../lib/http';
import { DeleteAccountPanel, DeleteAccountSection } from './DeleteAccountSection';

const mocks = vi.hoisted(() => ({
  endSession: vi.fn(),
  deleteMe: vi.fn(),
  role: 'patient' as 'patient' | 'caregiver',
}));

vi.mock('./AuthContext', () => ({
  useAuth: () => ({
    auth: {
      status: 'authed',
      user: { id: 'u1', email: 'ana@example.com', role: mocks.role, emailVerified: true },
    },
    signIn: vi.fn(),
    signOut: vi.fn(),
    endSession: mocks.endSession,
  }),
}));

// Se sustituye solo el transporte HTTP; el hook generado useDeleteMe es el real.
vi.mock('../../lib/http', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../lib/http')>()),
  http: mocks.deleteMe,
}));

function Probe() {
  const { pathname, search } = useLocation();
  return <p data-testid="location">{pathname + search}</p>;
}

function renderSection(ui: ReactNode = <DeleteAccountSection />) {
  const client = new QueryClient({ defaultOptions: { mutations: { retry: false } } });
  return render(
    <QueryClientProvider client={client}>
      <MemoryRouter initialEntries={['/ajustes']}>
        <Routes>
          <Route path="/ajustes" element={ui} />
          <Route path="/login" element={<p>Pantalla de login</p>} />
        </Routes>
        <Probe />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

const open = (user: ReturnType<typeof userEvent.setup>) =>
  user.click(screen.getByRole('button', { name: 'Eliminar mi cuenta' }));

beforeEach(() => {
  mocks.endSession.mockReset();
  mocks.deleteMe.mockReset();
  mocks.role = 'patient';
});

describe('DeleteAccountSection', () => {
  it('empieza plegada y se despliega con el foco en la contraseña', async () => {
    const user = userEvent.setup();
    renderSection();
    expect(screen.queryByLabelText('Tu contraseña actual')).not.toBeInTheDocument();
    await open(user);
    expect(screen.getByLabelText('Tu contraseña actual')).toHaveFocus();
    expect(screen.getByText('Esta acción no se puede deshacer.')).toBeInTheDocument();
  });

  it('muestra las consecuencias según el rol', async () => {
    const user = userEvent.setup();
    renderSection();
    await open(user);
    expect(screen.getByText(/todos tus datos de salud/)).toBeInTheDocument();
  });

  it('muestra las consecuencias del cuidador', async () => {
    mocks.role = 'caregiver';
    const user = userEvent.setup();
    renderSection();
    await open(user);
    expect(screen.getByText(/no tienen otro cuidador activo/)).toBeInTheDocument();
  });

  it('cancelar pliega, limpia el formulario y devuelve el foco al botón', async () => {
    const user = userEvent.setup();
    renderSection();
    await open(user);
    await user.type(screen.getByLabelText('Tu contraseña actual'), 'secreta');
    await user.click(screen.getByRole('button', { name: 'Cancelar' }));
    const opener = screen.getByRole('button', { name: 'Eliminar mi cuenta' });
    expect(opener).toHaveFocus();
    await open(user);
    expect(screen.getByLabelText('Tu contraseña actual')).toHaveValue('');
  });

  it('valida contraseña y confirmación antes de llamar a la API', async () => {
    const user = userEvent.setup();
    renderSection();
    await open(user);
    await user.click(screen.getByRole('button', { name: 'Eliminar definitivamente' }));
    expect(await screen.findByText('Escribe tu contraseña')).toBeInTheDocument();
    expect(screen.getByText('Confirma que entiendes que no se puede deshacer')).toBeInTheDocument();
    expect(mocks.deleteMe).not.toHaveBeenCalled();
  });

  it('403 invalid_password muestra el mensaje y no cierra la sesión', async () => {
    mocks.deleteMe.mockRejectedValue(new ApiError(403, 'invalid_password', 'Forbidden'));
    const user = userEvent.setup();
    renderSection();
    await open(user);
    await user.type(screen.getByLabelText('Tu contraseña actual'), 'mala');
    await user.click(screen.getByRole('checkbox', { name: /no se puede deshacer/ }));
    await user.click(screen.getByRole('button', { name: 'Eliminar definitivamente' }));
    expect(await screen.findByText('La contraseña no es correcta.')).toBeInTheDocument();
    expect(screen.getByRole('alert')).toHaveTextContent('La contraseña no es correcta.');
    expect(mocks.endSession).not.toHaveBeenCalled();
  });

  it('con éxito envía la contraseña, termina la sesión local y va al login', async () => {
    mocks.deleteMe.mockResolvedValue(undefined);
    const user = userEvent.setup();
    renderSection();
    await open(user);
    await user.type(screen.getByLabelText('Tu contraseña actual'), 'una-clave-larga');
    await user.click(screen.getByRole('checkbox', { name: /no se puede deshacer/ }));
    await user.click(screen.getByRole('button', { name: 'Eliminar definitivamente' }));
    await waitFor(() => expect(mocks.endSession).toHaveBeenCalledWith({ accountDeleted: true }));
    expect(mocks.deleteMe).toHaveBeenCalledWith(
      expect.objectContaining({
        url: '/users/me',
        method: 'DELETE',
        data: { password: 'una-clave-larga' },
      }),
      undefined,
    );
    expect(screen.getByTestId('location')).toHaveTextContent('/login?cuentaEliminada=1');
  });
});

describe('DeleteAccountPanel', () => {
  it('con envío en curso deshabilita los campos y marca el botón como ocupado', () => {
    renderSection(
      <DeleteAccountPanel userRole="patient" initialOpen pending onConfirm={vi.fn()} />,
    );
    expect(screen.getByLabelText('Tu contraseña actual')).toBeDisabled();
    expect(screen.getByRole('button', { name: /Eliminar definitivamente/ })).toHaveAttribute(
      'aria-busy',
      'true',
    );
  });
});
