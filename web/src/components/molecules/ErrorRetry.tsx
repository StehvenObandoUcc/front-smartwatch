import { Button } from '../atoms/Button/Button';
import { Text } from '../atoms/Text/Text';

type Props = { message: string; onRetry: () => void };

/** Error de carga con botón de reintento. */
export function ErrorRetry({ message, onRetry }: Props) {
  return (
    <div role="alert" className="flex flex-col items-start gap-2">
      <Text tone="danger">{message}</Text>
      <Button variant="secondary" onClick={onRetry}>
        Reintentar
      </Button>
    </div>
  );
}
