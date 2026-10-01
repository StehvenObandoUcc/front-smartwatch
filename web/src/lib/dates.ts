// Las horas del contrato son instantes; se muestran en la zona del paciente.
export function localDate(iso: string | Date, timeZone: string): string {
  return new Date(iso).toLocaleDateString('sv-SE', { timeZone });
}

export function formatTime(iso: string, timeZone: string): string {
  return new Date(iso).toLocaleTimeString('es', { timeZone, hour: '2-digit', minute: '2-digit' });
}

export function formatDay(iso: string, timeZone: string): string {
  return new Date(iso).toLocaleDateString('es', {
    timeZone,
    weekday: 'long',
    day: 'numeric',
    month: 'long',
  });
}

export function daysAgo(days: number, timeZone: string): string {
  return localDate(new Date(Date.now() - days * 86_400_000), timeZone);
}
