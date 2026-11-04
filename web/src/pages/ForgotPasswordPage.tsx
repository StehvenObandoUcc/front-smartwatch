import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router';

import { useForgotPassword } from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { Input } from '../components/atoms/Input/Input';
import { Text } from '../components/atoms/Text/Text';
import { FormField } from '../components/molecules/FormField';
import { AuthLayout } from '../components/templates/AuthLayout';
import { forgotPasswordSchema, type ForgotPasswordValues } from '../features/auth/forms';
import { errorMessage } from '../lib/errors';

export function ForgotPasswordPage() {
  const { t } = useTranslation();
  const forgot = useForgotPassword();
  const { register, handleSubmit, formState } = useForm<ForgotPasswordValues>({
    resolver: zodResolver(forgotPasswordSchema),
  });
  return (
    <AuthLayout title={t('auth.forgot.title')}>
      {forgot.isSuccess ? (
        <div role="status" className="flex flex-col gap-2">
          <Text tone="success">{t('auth.forgot.sent')}</Text>
          <Text tone="muted">{t('auth.forgot.sentHelp')}</Text>
        </div>
      ) : (
        <form
          noValidate
          onSubmit={handleSubmit((data) => forgot.mutate({ data }))}
          className="flex flex-col gap-4"
        >
          <Text tone="muted">{t('auth.forgot.intro')}</Text>
          <FormField label={t('auth.forgot.email')} error={formState.errors.email?.message}>
            {(p) => <Input type="email" autoComplete="email" {...p} {...register('email')} />}
          </FormField>
          {forgot.error && <Text tone="danger">{errorMessage(forgot.error)}</Text>}
          <Button type="submit" loading={forgot.isPending} fullWidth>
            {t('auth.forgot.submit')}
          </Button>
        </form>
      )}
      <Text>
        <Link to="/login" className="underline">
          {t('auth.forgot.back')}
        </Link>
      </Text>
    </AuthLayout>
  );
}
