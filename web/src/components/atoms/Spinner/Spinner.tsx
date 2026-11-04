import { useTranslation } from 'react-i18next';

import { cn } from '../../../lib/cn';

type Props = {
  /** Texto para lectores de pantalla. */
  label?: string;
  size?: 'sm' | 'md';
  className?: string;
};

export function Spinner({ label, size = 'md', className }: Props) {
  const { t } = useTranslation();
  return (
    <span role="status" className={cn('inline-flex items-center', className)}>
      <span
        aria-hidden="true"
        className={cn(
          'animate-spin rounded-full border-current border-t-transparent motion-reduce:animate-none',
          size === 'sm' ? 'size-4 border-2' : 'size-6 border-3',
        )}
      />
      <span className="sr-only">{label ?? t('common.loading')}</span>
    </span>
  );
}
