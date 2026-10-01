import { Link } from 'react-router';

import { Text } from '../components/atoms/Text/Text';
import { useAuth } from '../features/auth/AuthContext';

export function HomePage() {
  const { auth } = useAuth();
  const name = auth.status === 'authed' ? auth.user.displayName : '';
  return (
    <section aria-labelledby="home-title" className="flex flex-col gap-4">
      <Text id="home-title" variant="subtitle" as="h2">
        Hola, {name}
      </Text>
      <Text tone="muted">Carga medicamentos y horarios para el reloj y consulta el historial.</Text>
      <ul className="flex flex-col gap-2 underline">
        <li>
          <Link to="/medicamentos">Medicamentos</Link>
        </li>
        <li>
          <Link to="/agenda">Agenda</Link>
        </li>
        <li>
          <Link to="/adherencia">Adherencia</Link>
        </li>
      </ul>
    </section>
  );
}
