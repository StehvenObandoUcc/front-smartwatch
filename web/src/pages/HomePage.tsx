import { Badge } from '../components/atoms/Badge/Badge';
import { Text } from '../components/atoms/Text/Text';

export function HomePage() {
  return (
    <section aria-labelledby="home-title" className="flex flex-col gap-4">
      <Text id="home-title" variant="subtitle" as="h2">
        Bienvenido
      </Text>
      <Text tone="muted">
        Aquí podrás cargar medicamentos y horarios para el reloj y consultar reportes.
      </Text>
      <div>
        <Badge>Fase 0 · fundaciones</Badge>
      </div>
    </section>
  );
}
