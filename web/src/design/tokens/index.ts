// Espejo en TS de los tokens que se necesitan fuera de CSS (Storybook, lógica responsive).
export const breakpoints = { tablet: 640, desktop: 1024 } as const;

export const medicationColors = [
  'red',
  'orange',
  'yellow',
  'green',
  'teal',
  'blue',
  'purple',
  'pink',
] as const;

export type MedicationColor = (typeof medicationColors)[number];

// Clases completas para que Tailwind las detecte al compilar.
export const medicationColorClass: Record<MedicationColor, string> = {
  red: 'bg-med-red',
  orange: 'bg-med-orange',
  yellow: 'bg-med-yellow',
  green: 'bg-med-green',
  teal: 'bg-med-teal',
  blue: 'bg-med-blue',
  purple: 'bg-med-purple',
  pink: 'bg-med-pink',
};

export const viewports = {
  mobile: { name: 'Móvil (390)', styles: { width: '390px', height: '844px' } },
  tablet: { name: 'Tablet (820)', styles: { width: '820px', height: '1180px' } },
  desktop: { name: 'Escritorio (1440)', styles: { width: '1440px', height: '900px' } },
} as const;

// Valor hexadecimal que viaja en el contrato (`Color`); espejo de tokens.css.
export const medicationHex: Record<MedicationColor, string> = {
  red: '#c62828',
  orange: '#d4661a',
  yellow: '#b08900',
  green: '#2e7d32',
  teal: '#00796b',
  blue: '#1565c0',
  purple: '#6a1b9a',
  pink: '#ad1457',
};

/** Tono (0-360) de un hex: lo que distingue un color de medicamento de otro. */
function hue(hex: string): number {
  const n = Number.parseInt(hex.replace('#', '').slice(0, 6), 16);
  const r = ((n >> 16) & 255) / 255;
  const g = ((n >> 8) & 255) / 255;
  const b = (n & 255) / 255;
  const max = Math.max(r, g, b);
  const d = max - Math.min(r, g, b);
  if (d === 0) return 0;
  const h = max === r ? ((g - b) / d) % 6 : max === g ? (b - r) / d + 2 : (r - g) / d + 4;
  return (h * 60 + 360) % 360;
}

/**
 * El color de la paleta de tono más cercano a un hex cualquiera (la API guarda el que eligió la
 * persona, a veces un 500 de Tailwind): la placa usa siempre el tono profundo, con el blanco legible.
 */
export function medicationColorFor(hex: string): MedicationColor {
  const h = hue(hex);
  const gap = (c: MedicationColor) => {
    const diff = Math.abs(hue(medicationHex[c]) - h);
    return Math.min(diff, 360 - diff);
  };
  return medicationColors.reduce((best, c) => (gap(c) < gap(best) ? c : best));
}
