import { useState, type ComponentProps } from 'react';
import { useTranslation } from 'react-i18next';

import { cn } from '../../lib/cn';
import { Icon } from '../atoms/Icon/Icon';
import { Input } from '../atoms/Input/Input';

type Props = Omit<ComponentProps<typeof Input>, 'type'>;

/**
 * Campo de contraseña con «Mostrar» / «Ocultar» dentro del campo. El botón dice la palabra (no solo
 * el ojo) y anuncia su estado con `aria-pressed`; no lleva «contraseña» en el nombre para no
 * competir con la etiqueta del campo.
 */
export function PasswordInput({ className, ...rest }: Props) {
  const { t } = useTranslation();
  const [visible, setVisible] = useState(false);
  return (
    <div className="relative">
      <Input {...rest} type={visible ? 'text' : 'password'} className={cn('pr-28', className)} />
      <button
        type="button"
        aria-pressed={visible}
        aria-controls={rest.id}
        onClick={() => setVisible((v) => !v)}
        className="absolute inset-y-1 right-1 inline-flex min-w-touch items-center gap-2 rounded-md px-3 text-caption font-medium text-text-muted hover:bg-surface hover:text-text"
      >
        <Icon name={visible ? 'hide' : 'show'} className="size-5" />
        {visible ? t('common.hide') : t('common.show')}
      </button>
    </div>
  );
}
