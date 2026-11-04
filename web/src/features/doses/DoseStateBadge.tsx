import { useTranslation } from 'react-i18next';
import { Badge } from '../../components/atoms/Badge/Badge';
import { doseStateTone, type DoseState } from './doseState';

/** Placa de estado con su pictograma y su palabra: Tomada, Omitida, No tomada, Pendiente, Más tarde. */
export function DoseStateBadge({ state }: { state: DoseState }) {
  const { t } = useTranslation();
  return (
    <Badge tone={doseStateTone[state]} icon={state}>
      {t(`doses.state.${state}`)}
    </Badge>
  );
}
