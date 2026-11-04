import { useTranslation } from 'react-i18next';
import { useMutation } from '@tanstack/react-query';
import { Link, useParams } from 'react-router';

import { useGetReport } from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { saveReportPdf } from '../features/reports/download';
import { ReportView } from '../features/reports/ReportView';
import { errorMessage } from '../lib/errors';

const POLL_MS = 2000;

export function ReportPage() {
  const { t } = useTranslation();
  const { patientId = '', reportId = '' } = useParams();
  const report = useGetReport(patientId, reportId, {
    query: {
      refetchInterval: (query) => (query.state.data?.status === 'pending' ? POLL_MS : false),
    },
  });
  const download = useMutation({
    mutationFn: (periodEnd: string) => saveReportPdf(patientId, reportId, periodEnd),
  });

  if (report.isPending) return <Spinner label={t('reports.loadingOne')} />;
  if (report.isError) {
    return (
      <div role="alert" className="flex flex-col items-start gap-2">
        <Text tone="danger">{errorMessage(report.error)}</Text>
        <Button variant="secondary" onClick={() => void report.refetch()}>
          {t('common.retry')}
        </Button>
      </div>
    );
  }

  const { status, periodStart, periodEnd, summary } = report.data;
  return (
    <section className="flex flex-col gap-4" aria-labelledby="report-title">
      <Text id="report-title" variant="subtitle" as="h2">
        {t('reports.reportTitle', { start: periodStart, end: periodEnd })}
      </Text>
      {status === 'pending' && (
        <div role="status" className="flex items-center gap-2">
          <Spinner label={t('reports.generating')} />
          <Text>{t('reports.generatingText')}</Text>
        </div>
      )}
      {status === 'failed' && <Text tone="danger">{t('reports.failed')}</Text>}
      {status === 'ready' && summary && (
        <>
          <ReportView summary={summary} />
          {download.isError && <Text tone="danger">{errorMessage(download.error)}</Text>}
          <div>
            <Button loading={download.isPending} onClick={() => download.mutate(periodEnd)}>
              {t('reports.download')}
            </Button>
          </div>
        </>
      )}
      <Link to="/reportes" className="underline">
        {t('reports.all')}
      </Link>
    </section>
  );
}
