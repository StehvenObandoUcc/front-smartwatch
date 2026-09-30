import { cn } from '../../../lib/cn';

type Props = {
  /** Texto para lectores de pantalla. */
  label?: string;
  size?: 'sm' | 'md';
  className?: string;
};

export function Spinner({ label = 'Cargando', size = 'md', className }: Props) {
  return (
    <span role="status" className={cn('inline-flex items-center', className)}>
      <span
        aria-hidden="true"
        className={cn(
          'animate-spin rounded-full border-current border-t-transparent motion-reduce:animate-none',
          size === 'sm' ? 'size-4 border-2' : 'size-6 border-3',
        )}
      />
      <span className="sr-only">{label}</span>
    </span>
  );
}
