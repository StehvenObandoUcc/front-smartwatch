import { useEffect, useRef } from 'react';
import { Link, useSearchParams } from 'react-router';

import { useVerifyEmail } from '../api/generated';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { AuthLayout } from '../components/templates/AuthLayout';
import { errorMessage } from '../lib/errors';

export function VerifyEmailPage() {
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
    <AuthLayout title="Verificar correo">
      {!token && <Text tone="danger">El enlace no es válido. Pide uno nuevo desde el panel.</Text>}
      {token && verify.isPending && <Spinner label="Verificando" />}
      {verify.isSuccess && (
        <Text tone="success">Correo verificado. Ya puedes seguir usando la app.</Text>
      )}
      {verify.isError && (
        <Text tone="danger" as="p">
          {errorMessage(verify.error)} Pide un enlace nuevo desde el panel.
        </Text>
      )}
      <Text>
        <Link to="/" className="underline">
          Ir al panel
        </Link>
      </Text>
    </AuthLayout>
  );
}
