import { Text } from '../components/atoms/Text/Text';
import { ErrorRetry } from '../components/molecules/ErrorRetry';

/** Si una pantalla falla al dibujarse, se dice qué pasó y cómo seguir, sin la pantalla técnica. */
export function RouteError() {
  return (
    <main className="mx-auto flex min-h-dvh max-w-content flex-col justify-center gap-4 p-4">
      <Text variant="title" as="h1">
        Algo no salió bien
      </Text>
      <ErrorRetry
        message="Esta pantalla no se pudo mostrar. Vuelve a intentarlo; tus datos están a salvo."
        onRetry={() => window.location.reload()}
      />
    </main>
  );
}
