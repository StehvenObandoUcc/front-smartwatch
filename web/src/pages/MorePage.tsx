import { Link } from 'react-router';

import { Icon, type IconName } from '../components/atoms/Icon/Icon';
import { Plate } from '../components/atoms/Plate/Plate';
import { ThemeSwitch } from '../components/molecules/ThemeSwitch';
import { useTheme } from '../lib/theme';

const entries: { to: string; label: string; help: string; icon: IconName }[] = [
  { to: '/agenda', label: 'Agenda', help: 'Las dosis de los próximos días', icon: 'agenda' },
  { to: '/reportes', label: 'Reportes', help: 'Resumen de cada semana, en PDF', icon: 'reports' },
  { to: '/asistente', label: 'Asistente', help: 'Pregunta por el tratamiento', icon: 'assistant' },
  { to: '/avisos', label: 'Avisos', help: 'Telegram y correo', icon: 'notifications' },
  { to: '/ajustes', label: 'Ajustes', help: 'Reloj, cuidadores y cuenta', icon: 'settings' },
];

/** En el móvil, las secciones que no caben en el menú de abajo: una señal por sección. */
export function MorePage() {
  const theme = useTheme();
  return (
    <div className="flex flex-col gap-8">
      <nav aria-label="Más secciones">
        <ul className="rows border-y border-border">
          {entries.map((entry) => (
            <li key={entry.to}>
              <Link
                to={entry.to}
                className="flex min-h-touch items-center gap-4 rounded-md py-1 transition-colors hover:bg-surface motion-reduce:transition-none"
              >
                <Plate icon={entry.icon} size="lg" />
                <span className="flex min-w-0 flex-1 flex-col">
                  <span className="text-lg font-medium">{entry.label}</span>
                  <span className="text-caption text-text-muted">{entry.help}</span>
                </span>
                <Icon name="forward" className="text-text-muted" />
              </Link>
            </li>
          ))}
        </ul>
      </nav>
      <section aria-labelledby="tema" className="flex flex-col gap-3">
        <h2 id="tema" className="text-lg font-medium">
          Tema
        </h2>
        <ThemeSwitch value={theme.choice} onChange={theme.setChoice} />
      </section>
    </div>
  );
}
