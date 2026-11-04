import { useTranslation } from 'react-i18next';
import { useQueryClient } from '@tanstack/react-query';
import { Link, useNavigate } from 'react-router';

import {
  getListReportsQueryKey,
  useCreateReport,
  useListReports,
  type ReportStatus,
} from '../api/generated';
import { Badge } from '../components/atoms/Badge/Badge';
import { Button } from '../components/atoms/Button/Button';
import { Skeleton } from '../components/atoms/Skeleton/Skeleton';
import { Text } from '../components/atoms/Text/Text';
import { usePatient } from '../features/patients/PatientGate';
import { percentText } from '../features/reports/ReportView';
import { errorMessage } from '../lib/errors';

const statusTone: Record<ReportStatus, 'success' | 'warning' | 'danger'> = {
  ready: 'success',
  pending: 'warning',
  failed: 'danger',
};

export function ReportsPage() {
  const { t } = useTranslation();
  const { patientId } = usePatient();
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const reports = useListReports(patientId);
  const create = useCreateReport({
    mutation: {
      onSuccess: async (report) => {
        await queryClient.invalidateQueries({ queryKey: getListReportsQueryKey(patientId) });
        navigate(`/patients/${patientId}/reports/${report.id}`);
      },
    },
  });

  return (
    <section className="flex flex-col gap-4" aria-labelledby="reports-title">
      <div className="flex items-center justify-between gap-4">
        <Text id="reports-title" variant="subtitle" as="h2">
          {t('reports.title')}
        </Text>
        <Button loading={create.isPending} onClick={() => create.mutate({ patientId })}>
          {t('reports.generate')}
        </Button>
      </div>
      {create.error && <Text tone="danger">{errorMessage(create.error)}</Text>}
      {reports.isPending && <Skeleton label={t('reports.loading')} />}
      {reports.isError && (
        <div role="alert" className="flex flex-col items-start gap-2">
          <Text tone="danger">{errorMessage(reports.error)}</Text>
          <Button variant="secondary" onClick={() => void reports.refetch()}>
            {t('common.retry')}
          </Button>
        </div>
      )}
      {reports.data?.items.length === 0 && <Text tone="muted">{t('reports.empty')}</Text>}
      <ul className="flex flex-col gap-2">
        {reports.data?.items.map((report) => (
          <li key={report.id}>
            <Link
              to={`/patients/${patientId}/reports/${report.id}`}
              className="flex min-h-touch items-center justify-between gap-4 card p-4"
            >
              <div>
                <Text as="p">
                  {t('reports.period', { start: report.periodStart, end: report.periodEnd })}
                </Text>
                <Text tone="muted" variant="caption">
                  {report.trigger === 'weekly' ? t('reports.weekly') : t('reports.manual')}
                  {report.summary ? ` · ${percentText(report.summary.percentage)}` : ''}
                </Text>
              </div>
              <Badge tone={statusTone[report.status]}>{t(`reports.status.${report.status}`)}</Badge>
            </Link>
          </li>
        ))}
      </ul>
    </section>
  );
}
