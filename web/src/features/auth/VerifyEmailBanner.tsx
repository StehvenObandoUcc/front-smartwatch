import { useResendVerification } from '../../api/generated';
import { Button } from '../../components/atoms/Button/Button';
import { Icon } from '../../components/atoms/Icon/Icon';
import { Text } from '../../components/atoms/Text/Text';
import { errorMessage } from '../../lib/errors';

export function VerifyEmailBanner() {
  const resend = useResendVerification();
  return (
    <div
      role="status"
      className="mb-5 flex flex-wrap items-center gap-x-3 gap-y-2 rounded-md bg-surface px-3 py-2"
    >
      <Icon name="email" className="text-text-muted" />
      <Text as="p" className="min-w-48 flex-1 font-bold">
        {resend.isSuccess
          ? 'Te enviamos un correo nuevo. Revisa tu bandeja.'
          : 'Falta verificar tu correo'}
      </Text>
      {resend.error && <Text tone="danger">{errorMessage(resend.error)}</Text>}
      {!resend.isSuccess && (
        <Button variant="ghost" loading={resend.isPending} onClick={() => resend.mutate()}>
          Reenviar correo
        </Button>
      )}
    </div>
  );
}
