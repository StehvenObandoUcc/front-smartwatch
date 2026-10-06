import { useResendVerification } from '../../api/generated';
import { Button } from '../../components/atoms/Button/Button';
import { Text } from '../../components/atoms/Text/Text';
import { errorMessage } from '../../lib/errors';

export function VerifyEmailBanner() {
  const resend = useResendVerification();
  return (
    <div
      role="status"
      className="mb-4 flex flex-wrap items-center gap-2 rounded-md border-2 border-warning bg-surface p-3"
    >
      <Text as="p" className="min-w-48 flex-1">
        {resend.isSuccess
          ? 'Te enviamos un correo nuevo. Revisa tu bandeja.'
          : 'Verifica tu correo para recibir avisos y recuperar tu contraseña.'}
      </Text>
      {resend.error && <Text tone="danger">{errorMessage(resend.error)}</Text>}
      {!resend.isSuccess && (
        <Button variant="secondary" loading={resend.isPending} onClick={() => resend.mutate()}>
          Reenviar correo
        </Button>
      )}
    </div>
  );
}
