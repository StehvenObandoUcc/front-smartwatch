import { useListDoseHistory } from '../../api/generated';
import { Spinner } from '../../components/atoms/Spinner/Spinner';
import { Text } from '../../components/atoms/Text/Text';
import { ErrorRetry } from '../../components/molecules/ErrorRetry';
import { daysAgo } from '../../lib/dates';
import { errorMessage } from '../../lib/errors';
import { usePatient } from '../patients/PatientGate';
import { dailyAdherence, type DayAdherence } from './dailyAdherence';

const DAYS = 7;

// El día llega como YYYY-MM-DD local del paciente; a mediodía UTC no cambia de fecha al formatear.
function weekday(day: string, style: 'short' | 'long') {
  return new Date(`${day}T12:00:00Z`).toLocaleDateString('es', {
    timeZone: 'UTC',
    weekday: style,
    ...(style === 'long' && { day: 'numeric', month: 'long' }),
  });
}

function valueText(d: DayAdherence) {
  return d.percentage === null
    ? 'Sin dosis'
    : `${Math.round(d.percentage)} % (${d.taken} de ${d.total})`;
}

/** Porcentaje de dosis tomadas por día en los últimos 7 días, a partir de GET …/dose-history. */
export function AdherenceChart() {
  const { patientId, timezone } = usePatient();
  const days = Array.from({ length: DAYS }, (_, i) => daysAgo(DAYS - 1 - i, timezone));
  // ponytail: una sola página de 100 dosis cubre ~14 tomas diarias; si se supera, paginar con nextCursor.
  const history = useListDoseHistory(patientId, {
    from: days[0],
    to: days[DAYS - 1],
    limit: 100,
  });

  if (history.isPending) return <Spinner label="Cargando gráfico" />;
  if (history.isError) {
    return (
      <ErrorRetry message={errorMessage(history.error)} onRetry={() => void history.refetch()} />
    );
  }

  const data = dailyAdherence(history.data.items, days, timezone);
  if (data.every((d) => d.total === 0)) {
    return <Text tone="muted">Aún no hay dosis vencidas en los últimos 7 días.</Text>;
  }

  return (
    <figure className="flex flex-col gap-3">
      <figcaption className="text-caption text-text-muted">
        Porcentaje de dosis tomadas cada día. Pasa el cursor por una barra para ver el detalle.
      </figcaption>
      {/* Las barras son solo visuales; la tabla de abajo da los mismos datos a lectores de pantalla. */}
      <div aria-hidden="true" className="flex h-40 items-end gap-2">
        {data.map((d) => (
          <div
            key={d.day}
            className="group relative flex h-full min-w-0 flex-1 flex-col items-center justify-end gap-1"
          >
            <span className="pointer-events-none absolute -top-7 z-10 hidden rounded-sm bg-text px-2 py-1 text-caption whitespace-nowrap text-bg group-hover:block">
              {valueText(d)}
            </span>
            {/* Barra en SVG: la altura sale del dato sin estilos en línea (regla de tokens). */}
            <svg
              viewBox="0 0 10 100"
              preserveAspectRatio="none"
              className="w-full flex-1 border-b-2 border-border"
            >
              {d.percentage !== null && (
                <rect
                  x="0"
                  width="10"
                  y={100 - Math.max(d.percentage, 2)}
                  height={Math.max(d.percentage, 2)}
                  className="fill-primary"
                />
              )}
            </svg>
            <span className="text-caption text-text-muted">{weekday(d.day, 'short')}</span>
          </div>
        ))}
      </div>
      <details>
        <summary className="min-h-touch cursor-pointer py-2 underline">Ver como tabla</summary>
        <table className="w-full text-left">
          <thead>
            <tr>
              <th scope="col" className="py-2">
                Día
              </th>
              <th scope="col" className="py-2">
                Tomadas
              </th>
            </tr>
          </thead>
          <tbody>
            {data.map((d) => (
              <tr key={d.day} className="border-t-2 border-border">
                <th scope="row" className="py-2 font-normal">
                  {weekday(d.day, 'long')}
                </th>
                <td className="py-2">{valueText(d)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </details>
    </figure>
  );
}
