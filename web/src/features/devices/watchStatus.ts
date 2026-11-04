import { i18n } from '../../i18n';
import type { Device } from '../../api/generated';

const OFFLINE_MS = 3 * 60 * 60 * 1000;
const LOW_BATTERY = 20;

export type WatchAlert = 'never' | 'offline' | 'battery';

/**
 * Lo que el cuidador debe saber del reloj: que nunca se conectó, que lleva más de 3 h sin conexión
 * (no sube las tomas) o que tiene menos del 20 % de batería. `null` si está al día.
 */
export function watchAlert(device: Device, now: number): WatchAlert | null {
  if (!device.lastSeenAt) return 'never';
  if (now - new Date(device.lastSeenAt).getTime() > OFFLINE_MS) return 'offline';
  if (device.batteryLevel !== null && device.batteryLevel < LOW_BATTERY) return 'battery';
  return null;
}

/** «hace 25 min», «hace 3 horas», «hace 2 días». */
export function timeAgo(iso: string, now: number): string {
  const minutes = Math.max(0, Math.round((now - new Date(iso).getTime()) / 60_000));
  const rtf = new Intl.RelativeTimeFormat(i18n.language, { numeric: 'auto', style: 'long' });
  if (minutes < 60) return minutes < 1 ? i18n.t('watchCard.now') : rtf.format(-minutes, 'minute');
  const hours = Math.round(minutes / 60);
  if (hours < 24) return rtf.format(-hours, 'hour');
  return rtf.format(-Math.round(hours / 24), 'day');
}
