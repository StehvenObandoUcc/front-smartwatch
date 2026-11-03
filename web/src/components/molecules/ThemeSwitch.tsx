import { useId } from 'react';

import { cn } from '../../lib/cn';
import { Icon, type IconName } from '../atoms/Icon/Icon';

import type { ThemeChoice } from '../../lib/theme';

const options: { value: ThemeChoice; label: string; icon: IconName }[] = [
  { value: 'system', label: 'Sistema', icon: 'themeSystem' },
  { value: 'light', label: 'Claro', icon: 'themeLight' },
  { value: 'dark', label: 'Oscuro', icon: 'themeDark' },
];

type Props = {
  value: ThemeChoice;
  onChange: (value: ThemeChoice) => void;
  /** Solo pictogramas (la palabra queda para el lector): cabecera del móvil. */
  compact?: boolean;
  className?: string;
};

/**
 * Selector de tema de tres estados como grupo de opciones nativas: se recorre con las flechas y la
 * elegida se rellena. No conoce dónde se guarda la elección.
 */
export function ThemeSwitch({ value, onChange, compact = false, className }: Props) {
  const name = useId();
  return (
    <fieldset className={cn('min-w-0', className)}>
      <legend className="sr-only">Tema</legend>
      <div className="inline-flex rounded-md border border-border bg-surface p-1">
        {options.map((option) => (
          <label
            key={option.value}
            className={cn(
              'flex min-h-touch min-w-touch cursor-pointer items-center justify-center gap-2 rounded-md px-2 text-caption font-medium text-text-muted transition-colors motion-reduce:transition-none',
              'hover:text-text has-checked:bg-surface-raised has-checked:text-text has-checked:shadow-raised',
              'has-focus-visible:outline-3 has-focus-visible:outline-offset-2 has-focus-visible:outline-focus',
              !compact && 'px-3',
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
            <span className={cn(compact && 'sr-only')}>{option.label}</span>
          </label>
        ))}
      </div>
    </fieldset>
  );
}
