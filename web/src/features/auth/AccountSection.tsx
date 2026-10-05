import { useResendVerification } from '../../api/generated';
import { Badge } from '../../components/atoms/Badge/Badge';
import { Button } from '../../components/atoms/Button/Button';
import { Text } from '../../components/atoms/Text/Text';
import { errorMessage } from '../../lib/errors';
import { useAuth } from './AuthContext';

/** Correo de la cuenta con su estado de verificación (emailVerified de GET /users/me). */
export function AccountSection() {
  const { auth } = useAuth();
  const resend = useResendVerification();
  if (auth.status !== 'authed') return null;
  const { email, emailVerified } = auth.user;
  return (
    <section aria-labelledby="account-title" className="flex flex-col gap-3">
      <Text id="account-title" variant="subtitle" as="h2">
        Tu cuenta
      </Text>
      <div className="flex flex-wrap items-center gap-2">
        <Text as="p">{email}</Text>
        <Badge tone={emailVerified ? 'success' : 'warning'}>
          {emailVerified ? 'Correo verificado' : 'Correo sin verificar'}
        </Badge>
      </div>
      {!emailVerified && (
        <>
          <Text tone="muted">
            Sin verificar no recibirás avisos por correo ni podrás recuperar tu contraseña.
          </Text>
          {resend.isSuccess ? (
            <Text tone="success">Te enviamos un correo nuevo. Revisa tu bandeja.</Text>
          ) : (
            <div>
              <Button
                variant="secondary"
                loading={resend.isPending}
                onClick={() => resend.mutate()}
              >
                Reenviar correo de verificación
              </Button>
            </div>
          )}
          {resend.error && <Text tone="danger">{errorMessage(resend.error)}</Text>}
        </>
      )}
    </section>
  );
}
