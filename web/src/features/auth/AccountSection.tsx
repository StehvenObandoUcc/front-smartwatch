import { useTranslation } from 'react-i18next';

import { useResendVerification } from '../../api/generated';
import { Badge } from '../../components/atoms/Badge/Badge';
import { Button } from '../../components/atoms/Button/Button';
import { Text } from '../../components/atoms/Text/Text';
import { errorMessage } from '../../lib/errors';
import { useAuth } from './AuthContext';

/** Correo de la cuenta con su estado de verificación (emailVerified de GET /users/me). */
export function AccountSection() {
  const { auth } = useAuth();
  const { t } = useTranslation();
  const resend = useResendVerification();
  if (auth.status !== 'authed') return null;
  const { email, emailVerified } = auth.user;
  return (
    <section aria-labelledby="account-title" className="flex flex-col gap-3">
      <Text id="account-title" variant="subtitle" as="h2">
        {t('auth.account.title')}
      </Text>
      <div className="flex flex-wrap items-center gap-2">
        <Text as="p" className="break-all">
          {email}
        </Text>
        <Badge tone={emailVerified ? 'success' : 'warning'}>
          {emailVerified ? t('auth.account.verified') : t('auth.account.unverified')}
        </Badge>
      </div>
      {!emailVerified && (
        <>
          <Text tone="muted">{t('auth.account.unverifiedHelp')}</Text>
          {resend.isSuccess ? (
            <Text tone="success">{t('auth.verify.resent')}</Text>
          ) : (
            <div>
              <Button
                variant="secondary"
                loading={resend.isPending}
                onClick={() => resend.mutate()}
              >
                {t('auth.account.resend')}
              </Button>
            </div>
          )}
          {resend.error && <Text tone="danger">{errorMessage(resend.error)}</Text>}
        </>
      )}
    </section>
  );
}
