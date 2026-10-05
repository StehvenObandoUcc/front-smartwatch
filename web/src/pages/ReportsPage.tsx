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
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { usePatient } from '../features/patients/PatientGate';
import { percentText } from '../features/reports/ReportView';
import { errorMessage } from '../lib/errors';

const statusView: Record<ReportStatus, { label: string; tone: 'success' | 'warning' | 'danger' }> =
  {
    ready: { label: 'Listo', tone: 'success' },
    pending: { label: 'Generando', tone: 'warning' },
    failed: { label: 'Falló', tone: 'danger' },
  };

export function ReportsPage() {
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
          Reportes semanales
        </Text>
        <Button loading={create.isPending} onClick={() => create.mutate({ patientId })}>
          Generar reporte
        </Button>
      </div>
      {create.error && <Text tone="danger">{errorMessage(create.error)}</Text>}
      {reports.isPending && <Spinner label="Cargando reportes" />}
      {reports.isError && (
        <div role="alert" className="flex flex-col items-start gap-2">
          <Text tone="danger">{errorMessage(reports.error)}</Text>
          <Button variant="secondary" onClick={() => void reports.refetch()}>
            Reintentar
          </Button>
        </div>
      )}
      {reports.data?.items.length === 0 && (
        <Text tone="muted">
          Aún no hay reportes. Se genera uno cada lunes, o puedes pedir uno ahora.
        </Text>
      )}
      <ul className="flex flex-col gap-2">
        {reports.data?.items.map((report) => (
          <li key={report.id}>
            <Link
              to={`/patients/${patientId}/reports/${report.id}`}
              className="flex min-h-touch items-center justify-between gap-4 card p-4"
            >
              <div>
                <Text as="p">
                  {report.periodStart} al {report.periodEnd}
                </Text>
                <Text tone="muted" variant="caption">
                  {report.trigger === 'weekly' ? 'Automático' : 'Pedido a mano'}
                  {report.summary ? ` · ${percentText(report.summary.percentage)}` : ''}
                </Text>
              </div>
              <Badge tone={statusView[report.status].tone}>{statusView[report.status].label}</Badge>
            </Link>
          </li>
        ))}
      </ul>
    </section>
  );
}
