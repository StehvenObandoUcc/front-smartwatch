import { Badge } from '../../components/atoms/Badge/Badge';
import { doseStateLabel, type DoseState } from './doseState';

/** Tomada / Omitida / No tomada / Pendiente; nada para las que aún no tocan. */
export function DoseStateBadge({ state }: { state: DoseState }) {
  if (state === 'upcoming') return null;
  const { text, tone } = doseStateLabel[state];
  return <Badge tone={tone}>{text}</Badge>;
}
