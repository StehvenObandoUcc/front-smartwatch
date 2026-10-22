import type { ReportSummary } from '../../api/generated';
import { Text } from '../../components/atoms/Text/Text';

/** «1 tomada», «3 tomadas». */
export function countText(n: number, one: string, many: string): string {
  return `${n} ${n === 1 ? one : many}`;
}

export function percentText(value: number | null): string {
  return value === null ? 'Sin datos' : `${Math.round(value)} %`;
}

export function ReportView({ summary }: { summary: ReportSummary }) {
  return (
    <div className="flex flex-col gap-4">
      <div>
        <Text variant="display" as="p">
          {percentText(summary.percentage)}
        </Text>
        <Text tone="muted">
          {countText(summary.taken, 'tomada', 'tomadas')} ·{' '}
          {countText(summary.skipped, 'omitida', 'omitidas')} ·{' '}
          {countText(summary.missed, 'no tomada', 'no tomadas')}
        </Text>
      </div>
      {summary.byMedication.length > 0 && (
        <table className="w-full text-left">
          <caption className="sr-only">Adherencia por medicamento</caption>
          <thead>
            <tr>
              <th scope="col" className="py-2">
                Medicamento
              </th>
              <th scope="col" className="py-2">
                Adherencia
              </th>
              <th scope="col" className="py-2">
                Tomadas
              </th>
            </tr>
          </thead>
          <tbody>
            {summary.byMedication.map((med) => (
              <tr key={med.medicationId} className="border-t-2 border-border">
                <th scope="row" className="py-3 font-normal">
                  {med.medicationName}
                </th>
                <td className="py-3">{percentText(med.percentage)}</td>
                <td className="py-3">
                  {med.taken}/{med.taken + med.skipped + med.missed}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
