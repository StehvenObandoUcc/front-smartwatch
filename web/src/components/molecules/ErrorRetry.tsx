import { Button } from '../atoms/Button/Button';
import { Icon } from '../atoms/Icon/Icon';
import { Text } from '../atoms/Text/Text';

type Props = { message: string; onRetry: () => void };

/** Error de carga: aviso con pictograma, qué pasó y el botón para reintentar. */
export function ErrorRetry({ message, onRetry }: Props) {
  return (
    <div
      role="alert"
      className="flex flex-col items-start gap-3 rounded-lg border-2 border-danger p-4"
    >
      <div className="flex items-start gap-2 text-danger">
        <Icon name="alert" className="mt-0.5" />
        <Text tone="danger">{message}</Text>
      </div>
      <Button variant="secondary" icon="undo" onClick={onRetry}>
        Reintentar
      </Button>
    </div>
  );
}
