import { downloadReportPdf } from '../../api/generated';

/** Pide el PDF con la sesión (cabecera Authorization) y lo descarga como archivo. */
export async function saveReportPdf(patientId: string, reportId: string, periodEnd: string) {
  const blob = await downloadReportPdf(patientId, reportId);
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `reporte-${periodEnd}.pdf`;
  link.click();
  URL.revokeObjectURL(url);
}
