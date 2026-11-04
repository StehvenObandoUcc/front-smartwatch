import { useTranslation } from 'react-i18next';

import { useResendVerification } from '../../api/generated';
import { Button } from '../../components/atoms/Button/Button';
import { Icon } from '../../components/atoms/Icon/Icon';
import { Text } from '../../components/atoms/Text/Text';
import { errorMessage } from '../../lib/errors';

export function VerifyEmailBanner() {
  const { t } = useTranslation();
  const resend = useResendVerification();
  return (
    <div
      role="status"
      className="mb-5 flex flex-wrap items-center gap-x-3 gap-y-2 rounded-md bg-surface px-3 py-2"
    >
      <Icon name="email" className="text-text-muted" />
      <Text as="p" className="min-w-0 flex-1 basis-48 font-bold">
        {resend.isSuccess ? t('auth.verify.resent') : t('auth.verify.pending')}
      </Text>
      {resend.error && <Text tone="danger">{errorMessage(resend.error)}</Text>}
      {!resend.isSuccess && (
        <Button variant="ghost" loading={resend.isPending} onClick={() => resend.mutate()}>
          {t('auth.verify.resend')}
        </Button>
      )}
    </div>
  );
}
