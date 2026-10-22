import { Link } from 'react-router';

import { Icon, type IconName } from '../components/atoms/Icon/Icon';
import { Plate } from '../components/atoms/Plate/Plate';

const entries: { to: string; label: string; help: string; icon: IconName }[] = [
  { to: '/reportes', label: 'Reportes', help: 'Resumen de cada semana, en PDF', icon: 'reports' },
  { to: '/asistente', label: 'Asistente', help: 'Pregunta por el tratamiento', icon: 'assistant' },
  { to: '/avisos', label: 'Avisos', help: 'Telegram y correo', icon: 'notifications' },
  { to: '/ajustes', label: 'Ajustes', help: 'Reloj, cuidadores y cuenta', icon: 'settings' },
];

/** En el móvil, las secciones que no caben en el menú de abajo: una señal por sección. */
export function MorePage() {
  return (
    <nav aria-label="Más secciones">
      <ul className="flex flex-col gap-3">
        {entries.map((entry) => (
          <li key={entry.to}>
            <Link
              to={entry.to}
              className="flex min-h-touch items-center gap-4 card p-4 transition-colors hover:bg-surface motion-reduce:transition-none"
            >
              <Plate icon={entry.icon} size="lg" />
              <span className="flex min-w-0 flex-1 flex-col">
                <span className="text-lg font-extrabold">{entry.label}</span>
                <span className="text-caption text-text-muted">{entry.help}</span>
              </span>
              <Icon name="forward" className="text-text-muted" />
            </Link>
          </li>
        ))}
      </ul>
    </nav>
  );
}
