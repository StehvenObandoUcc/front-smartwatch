import { useTranslation } from 'react-i18next';

import { Text } from '../components/atoms/Text/Text';
import { ErrorRetry } from '../components/molecules/ErrorRetry';

/** Si una pantalla falla al dibujarse, se dice qué pasó y cómo seguir, sin la pantalla técnica. */
export function RouteError() {
  const { t } = useTranslation();
  return (
    <main className="mx-auto flex min-h-dvh max-w-content flex-col justify-center gap-4 p-4">
      <Text variant="title" as="h1">
        {t('routeError.title')}
      </Text>
      <ErrorRetry message={t('routeError.body')} onRetry={() => window.location.reload()} />
    </main>
  );
}
