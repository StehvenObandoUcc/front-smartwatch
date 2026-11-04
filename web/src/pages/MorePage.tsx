import { useTranslation } from 'react-i18next';
import { Link } from 'react-router';

import { Icon, type IconName } from '../components/atoms/Icon/Icon';
import { Plate } from '../components/atoms/Plate/Plate';
import { LanguageSwitch } from '../components/molecules/LanguageSwitch';
import { ThemeSwitch } from '../components/molecules/ThemeSwitch';
import { useLanguage } from '../i18n';
import { useTheme } from '../lib/theme';

// Cada entrada usa nav.<sección> y nav.<sección>Help.
const entries: {
  to: string;
  key: 'agenda' | 'reports' | 'assistant' | 'notifications' | 'settings';
  icon: IconName;
}[] = [
  { to: '/agenda', key: 'agenda', icon: 'agenda' },
  { to: '/reportes', key: 'reports', icon: 'reports' },
  { to: '/asistente', key: 'assistant', icon: 'assistant' },
  { to: '/avisos', key: 'notifications', icon: 'notifications' },
  { to: '/ajustes', key: 'settings', icon: 'settings' },
];

/** En el móvil, las secciones que no caben en el menú de abajo: una señal por sección. */
export function MorePage() {
  const { t } = useTranslation();
  const theme = useTheme();
  const language = useLanguage();
  return (
    <div className="flex flex-col gap-8">
      <nav aria-label={t('nav.moreSections')}>
        <ul className="rows border-y border-border">
          {entries.map((entry) => (
            <li key={entry.to}>
              <Link
                to={entry.to}
                className="flex min-h-touch items-center gap-4 rounded-md py-1 transition-colors hover:bg-surface motion-reduce:transition-none"
              >
                <Plate icon={entry.icon} size="lg" />
                <span className="flex min-w-0 flex-1 flex-col">
                  <span className="text-lg font-medium">{t(`nav.${entry.key}`)}</span>
                  <span className="text-caption text-text-muted">{t(`nav.${entry.key}Help`)}</span>
                </span>
                <Icon name="forward" className="text-text-muted" />
              </Link>
            </li>
          ))}
        </ul>
      </nav>
      <section aria-labelledby="tema" className="flex flex-col gap-3">
        <h2 id="tema" className="text-lg font-medium">
          {t('theme.label')}
        </h2>
        <ThemeSwitch value={theme.choice} onChange={theme.setChoice} />
      </section>
      <section aria-labelledby="idioma" className="flex flex-col gap-3">
        <h2 id="idioma" className="text-lg font-medium">
          {t('language.label')}
        </h2>
        <LanguageSwitch value={language.language} onChange={language.setLanguage} />
      </section>
    </div>
  );
}
