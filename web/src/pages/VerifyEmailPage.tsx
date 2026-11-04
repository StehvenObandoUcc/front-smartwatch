import { useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useSearchParams } from 'react-router';

import { useVerifyEmail } from '../api/generated';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { AuthLayout } from '../components/templates/AuthLayout';
import { errorMessage } from '../lib/errors';

export function VerifyEmailPage() {
  const { t } = useTranslation();
  const token = useSearchParams()[0].get('token');
  const verify = useVerifyEmail();
  // El token es de un solo uso: StrictMode ejecuta el efecto dos veces en desarrollo.
  const started = useRef(false);

  useEffect(() => {
    if (token && !started.current) {
      started.current = true;
      verify.mutate({ data: { token } });
    }
  }, [token, verify]);

  return (
    <AuthLayout title={t('auth.verify.title')}>
      {!token && <Text tone="danger">{t('auth.verify.invalidLink')}</Text>}
      {token && verify.isPending && <Spinner label={t('auth.verify.verifying')} />}
      {verify.isSuccess && <Text tone="success">{t('auth.verify.done')}</Text>}
      {verify.isError && (
        <Text tone="danger" as="p">
          {errorMessage(verify.error)} {t('auth.verify.askNew')}
        </Text>
      )}
      <Text>
        <Link to="/" className="underline">
          {t('auth.verify.toPanel')}
        </Link>
      </Text>
    </AuthLayout>
  );
}
