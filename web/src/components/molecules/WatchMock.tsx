import { cn } from '../../lib/cn';
import { Icon } from '../atoms/Icon/Icon';
import { Ring } from '../atoms/Ring/Ring';

/** El reloj de muestra: el anillo del día como se ve en la muñeca. Ilustración, no datos. */
export function WatchMock({ className }: { className?: string }) {
  return (
    <div
      aria-hidden="true"
      className={cn(
        'flex size-72 shrink-0 flex-col items-center justify-center gap-2 rounded-full border-8 border-on-sign/20 bg-sign text-on-sign',
        className,
      )}
    >
      <Ring value={2} total={3} tone="onSign">
        <span className="text-2xl font-extrabold">2</span>
        <span className="text-caption">de 3 tomadas</span>
      </Ring>
      <span className="inline-flex items-center gap-1 font-bold">
        <Icon name="due" className="size-5" />
        Próxima 13:00
      </span>
    </div>
  );
}
