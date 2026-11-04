import { i18n } from '../../i18n';
import type { ReportSummary } from '../../api/generated';
import { Text } from '../../components/atoms/Text/Text';

/** «1 tomada», «3 tomadas» (en inglés, «1 taken»). */
export function countText(count: number, kind: 'taken' | 'skipped' | 'missed'): string {
  return i18n.t(`doses.count.${kind}`, { count });
}

export function percentText(value: number | null): string {
  return value === null
    ? i18n.t('common.noData')
    : i18n.t('common.percent', { value: Math.round(value) });
}

export function ReportView({ summary }: { summary: ReportSummary }) {
  return (
    <div className="flex flex-col gap-4">
      <div>
        <Text variant="display" as="p">
          {percentText(summary.percentage)}
        </Text>
        <Text tone="muted">
          {countText(summary.taken, 'taken')} · {countText(summary.skipped, 'skipped')} ·{' '}
          {countText(summary.missed, 'missed')}
        </Text>
      </div>
      {summary.byMedication.length > 0 && (
        <table className="w-full text-left">
          <caption className="sr-only">{i18n.t('reports.byMedication')}</caption>
          <thead>
            <tr>
              <th scope="col" className="py-2">
                {i18n.t('reports.medication')}
              </th>
              <th scope="col" className="py-2">
                {i18n.t('reports.adherence')}
              </th>
              <th scope="col" className="py-2">
                {i18n.t('reports.taken')}
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
