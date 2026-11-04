import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { Link, useSearchParams } from 'react-router';

import { useResetPassword } from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { PasswordInput } from '../components/molecules/PasswordInput';
import { Text } from '../components/atoms/Text/Text';
import { FormField } from '../components/molecules/FormField';
import { AuthLayout } from '../components/templates/AuthLayout';
import { resetPasswordSchema, type ResetPasswordValues } from '../features/auth/forms';
import { errorMessage } from '../lib/errors';

export function ResetPasswordPage() {
  const { t } = useTranslation();
  const token = useSearchParams()[0].get('token');
  const reset = useResetPassword();
  const { register, handleSubmit, formState } = useForm<ResetPasswordValues>({
    resolver: zodResolver(resetPasswordSchema),
  });
  const { errors } = formState;

  if (!token) {
    return (
      <AuthLayout title={t('auth.reset.title')}>
        <Text tone="danger">{t('auth.reset.invalidLink')}</Text>
        <Link to="/recuperar" className="underline">
          {t('auth.reset.newLink')}
        </Link>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout title={t('auth.reset.title')}>
      {reset.isSuccess ? (
        <>
          <Text tone="success">{t('auth.reset.done')}</Text>
          <Link to="/login" className="underline">
            {t('auth.signIn')}
          </Link>
        </>
      ) : (
        <form
          noValidate
          onSubmit={handleSubmit(({ newPassword }) =>
            reset.mutate({ data: { token, newPassword } }),
          )}
          className="flex flex-col gap-4"
        >
          <FormField
            label={t('auth.reset.newPassword')}
            error={errors.newPassword?.message}
            hint={t('forms.passwordMin')}
          >
            {(p) => (
              <PasswordInput autoComplete="new-password" {...p} {...register('newPassword')} />
            )}
          </FormField>
          <FormField label={t('auth.reset.repeat')} error={errors.confirm?.message}>
            {(p) => <PasswordInput autoComplete="new-password" {...p} {...register('confirm')} />}
          </FormField>
          {reset.error && (
            <Text tone="danger" as="p">
              {errorMessage(reset.error)}{' '}
              <Link to="/recuperar" className="underline">
                {t('auth.reset.anotherLink')}
              </Link>
            </Text>
          )}
          <Button type="submit" loading={reset.isPending} fullWidth>
            {t('auth.reset.submit')}
          </Button>
        </form>
      )}
    </AuthLayout>
  );
}
