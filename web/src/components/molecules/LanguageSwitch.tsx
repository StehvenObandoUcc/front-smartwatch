import { useId } from 'react';
import { useTranslation } from 'react-i18next';

import { cn } from '../../lib/cn';

import type { Language } from '../../i18n';

// Cada idioma se nombra en su propia lengua: quien no entiende el actual reconoce el suyo.
const options: { value: Language; label: string; short: string }[] = [
  { value: 'es', label: 'Español', short: 'ES' },
  { value: 'en', label: 'English', short: 'EN' },
];

type Props = {
  value: Language;
  onChange: (value: Language) => void;
  /** Solo «ES» / «EN» hasta escritorio (el nombre completo queda para el lector): las cabeceras. */
  compact?: boolean;
  className?: string;
};

/** Selector de idioma como grupo de opciones nativas, con el mismo aspecto que el del tema. */
export function LanguageSwitch({ value, onChange, compact = false, className }: Props) {
  const { t } = useTranslation();
  const name = useId();
  return (
    <fieldset className={cn('min-w-0', className)}>
      <legend className="sr-only">{t('language.label')}</legend>
      <div className="inline-flex rounded-md border border-border bg-surface p-1">
        {options.map((option) => (
          <label
            key={option.value}
            lang={option.value}
            className={cn(
              'flex min-h-touch min-w-touch cursor-pointer items-center justify-center rounded-md px-3 text-caption font-medium text-text-muted transition-colors motion-reduce:transition-none',
              'hover:text-text has-checked:bg-surface-raised has-checked:text-text has-checked:shadow-raised',
              'has-focus-visible:outline-3 has-focus-visible:outline-offset-2 has-focus-visible:outline-focus',
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
            {compact && (
              <span aria-hidden="true" className="desktop:hidden">
                {option.short}
              </span>
            )}
            <span className={cn(compact && 'sr-only desktop:not-sr-only')}>{option.label}</span>
          </label>
        ))}
      </div>
    </fieldset>
  );
}
