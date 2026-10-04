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
  const { patientId = '', reportId = '' } = useParams();
  const report = useGetReport(patientId, reportId, {
    query: {
      refetchInterval: (query) => (query.state.data?.status === 'pending' ? POLL_MS : false),
    },
  });
  const download = useMutation({
    mutationFn: (periodEnd: string) => saveReportPdf(patientId, reportId, periodEnd),
  });

  if (report.isPending) return <Spinner label="Cargando reporte" />;
  if (report.isError) {
    return (
      <div role="alert" className="flex flex-col items-start gap-2">
        <Text tone="danger">{errorMessage(report.error)}</Text>
        <Button variant="secondary" onClick={() => void report.refetch()}>
          Reintentar
        </Button>
      </div>
    );
  }

  const { status, periodStart, periodEnd, summary } = report.data;
  return (
    <section className="flex flex-col gap-4" aria-labelledby="report-title">
      <Text id="report-title" variant="subtitle" as="h2">
        Reporte del {periodStart} al {periodEnd}
      </Text>
      {status === 'pending' && (
        <div role="status" className="flex items-center gap-2">
          <Spinner label="Generando reporte" />
          <Text>Generando el reporte…</Text>
        </div>
      )}
      {status === 'failed' && (
        <Text tone="danger">No se pudo generar el reporte. Vuelve a pedirlo desde Reportes.</Text>
      )}
      {status === 'ready' && summary && (
        <>
          <ReportView summary={summary} />
          {download.isError && <Text tone="danger">{errorMessage(download.error)}</Text>}
          <div>
            <Button loading={download.isPending} onClick={() => download.mutate(periodEnd)}>
              Descargar PDF
            </Button>
          </div>
        </>
      )}
      <Link to="/reportes" className="underline">
        Todos los reportes
      </Link>
    </section>
  );
}
