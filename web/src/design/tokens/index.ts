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
