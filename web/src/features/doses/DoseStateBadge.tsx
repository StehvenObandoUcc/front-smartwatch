import { Badge } from '../../components/atoms/Badge/Badge';
import { doseStateLabel, type DoseState } from './doseState';

/** Placa de estado con su pictograma y su palabra: Tomada, Omitida, No tomada, Pendiente. */
export function DoseStateBadge({ state }: { state: DoseState }) {
  if (state === 'upcoming') return null;
  const { text, tone } = doseStateLabel[state];
  return (
    <Badge tone={tone} icon={state === 'due' ? 'due' : state}>
      {text}
    </Badge>
  );
}
