import { useId } from 'react';
import { useTranslation } from 'react-i18next';

import { cn } from '../../lib/cn';
import { Icon, type IconName } from '../atoms/Icon/Icon';

import type { ThemeChoice } from '../../lib/theme';

const options: { value: ThemeChoice; icon: IconName }[] = [
  { value: 'system', icon: 'themeSystem' },
  { value: 'light', icon: 'themeLight' },
  { value: 'dark', icon: 'themeDark' },
];

type Props = {
  value: ThemeChoice;
  onChange: (value: ThemeChoice) => void;
  /** Solo pictogramas hasta escritorio (la palabra queda para el lector): la cabecera. */
  compact?: boolean;
  className?: string;
};

/**
 * Selector de tema de tres estados como grupo de opciones nativas: se recorre con las flechas y la
 * elegida se rellena. No conoce dónde se guarda la elección.
 */
export function ThemeSwitch({ value, onChange, compact = false, className }: Props) {
  const { t } = useTranslation();
  const name = useId();
  return (
    <fieldset className={cn('min-w-0', className)}>
      <legend className="sr-only">{t('theme.label')}</legend>
      <div className="inline-flex rounded-md border border-border bg-surface p-1">
        {options.map((option) => (
          <label
            key={option.value}
            className={cn(
              'flex min-h-touch min-w-touch cursor-pointer items-center justify-center gap-2 rounded-md px-2 text-caption font-medium text-text-muted transition-colors motion-reduce:transition-none',
              'hover:text-text has-checked:bg-surface-raised has-checked:text-text has-checked:shadow-raised',
              'has-focus-visible:outline-3 has-focus-visible:outline-offset-2 has-focus-visible:outline-focus',
              compact ? 'desktop:px-3' : 'px-3',
            )}
          >
            <input
              type="radio"
              name={name}
              value={option.value}
              checked={value === option.value}
              onChange={() => onChange(option.value)}
              className="sr-only"
            />
            <Icon name={option.icon} className="size-5" />
            <span className={cn(compact && 'sr-only desktop:not-sr-only')}>
              {t(`theme.${option.value}`)}
            </span>
          </label>
        ))}
      </div>
    </fieldset>
  );
}
