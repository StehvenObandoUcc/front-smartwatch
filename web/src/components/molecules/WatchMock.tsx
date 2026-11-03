import { cn } from '../../lib/cn';
import { Icon } from '../atoms/Icon/Icon';
import { Ring } from '../atoms/Ring/Ring';

type Props = {
  /** `today`: el anillo del día; `alarm`: la alerta de una dosis con Tomada. */
  screen?: 'today' | 'alarm';
  className?: string;
};

/**
 * El reloj de muestra, siempre con la esfera petróleo del reloj real (en los dos temas). Ilustración
 * con datos de ejemplo, no datos: quien lo usa la describe en palabras.
 */
export function WatchMock({ screen = 'today', className }: Props) {
  return (
    <div
      aria-hidden="true"
      className={cn(
        'grid aspect-square w-64 max-w-full shrink-0 place-items-center rounded-full border-10 border-ink/85 shadow-raised',
        screen === 'alarm'
          ? 'bg-linear-to-b from-watch-alarm to-watch-bg text-watch-fg'
          : 'bg-linear-to-b from-watch-bg to-watch-bg-2 text-watch-fg',
        className,
      )}
    >
      {screen === 'today' ? (
        <div className="flex flex-col items-center gap-2">
          <Ring value={5} total={8} tone="watch" size="sm">
            <span className="text-2xl leading-none font-medium">5</span>
            <span className="text-caption">de 8</span>
          </Ring>
          <span className="inline-flex items-center gap-1 text-caption font-medium">
            <Icon name="due" className="size-4 text-watch-accent" />
            Próxima 14:00
          </span>
        </div>
      ) : (
        <div className="flex flex-col items-center gap-2 px-8 text-center">
          <Icon name="medications" className="size-7" />
          <p className="text-xl leading-tight font-medium">Losartán</p>
          <p className="text-caption">50 mg · 14:00</p>
          <span className="inline-flex min-h-touch items-center gap-1 rounded-full bg-watch-fg px-5 font-medium text-ink">
            <Icon name="taken" className="size-5" />
            Tomada
          </span>
        </div>
      )}
    </div>
  );
}
