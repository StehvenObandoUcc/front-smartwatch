import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { Link, Navigate } from 'react-router';

import { useRegister } from '../api/generated';
import { Icon } from '../components/atoms/Icon/Icon';
import { Button } from '../components/atoms/Button/Button';
import { Input } from '../components/atoms/Input/Input';
import { PasswordInput } from '../components/molecules/PasswordInput';
import { Text } from '../components/atoms/Text/Text';
import { FormField } from '../components/molecules/FormField';
import { AuthLayout } from '../components/templates/AuthLayout';
import { useAuth } from '../features/auth/AuthContext';
import { registerSchema, type RegisterValues } from '../features/auth/forms';
import { useLanguage } from '../i18n';
import { errorMessage } from '../lib/errors';

// Textos en auth.register.<rol>Title y <rol>Help.
const roles = ['patient', 'caregiver'] as const;

export function RegisterPage() {
  const { t } = useTranslation();
  const { language } = useLanguage();
  const { auth, signIn } = useAuth();
  const registerUser = useRegister();
  const { register, handleSubmit, formState } = useForm<RegisterValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: { role: 'patient' },
  });
  if (auth.status === 'authed') return <Navigate to="/" replace />;
  const { errors } = formState;
  const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
  return (
    <AuthLayout title={t('auth.createAccount')}>
      <form
        noValidate
        onSubmit={handleSubmit((values) =>
          registerUser.mutate(
            // La cuenta nace en el idioma del panel: así llegan sus correos y avisos.
            { data: { ...values, timezone, locale: language } },
            { onSuccess: signIn },
          ),
        )}
        className="flex flex-col gap-4"
      >
        <FormField label={t('auth.register.name')} error={errors.displayName?.message}>
          {(p) => <Input autoComplete="name" {...p} {...register('displayName')} />}
        </FormField>
        <FormField label={t('auth.email')} error={errors.email?.message}>
          {(p) => <Input type="email" autoComplete="email" {...p} {...register('email')} />}
        </FormField>
        <FormField
          label={t('auth.password')}
          error={errors.password?.message}
          hint={t('forms.passwordMin')}
        >
          {(p) => <PasswordInput autoComplete="new-password" {...p} {...register('password')} />}
        </FormField>
        <fieldset className="flex flex-col gap-2">
          <legend className="text-body font-bold">{t('auth.register.roleQuestion')}</legend>
          {roles.map((role) => (
            <label key={role} className="group choice flex-col items-start py-3">
              <input type="radio" value={role} className="sr-only" {...register('role')} />
              <span className="flex items-center gap-2 text-body font-bold">
                <Icon name="taken" className="hidden size-5 group-has-checked:block" />
                {t(`auth.register.${role}Title`)}
              </span>
              <span className="text-caption text-text-muted group-has-checked:text-on-primary">
                {t(`auth.register.${role}Help`)}
              </span>
            </label>
          ))}
        </fieldset>
        {registerUser.error && (
          <div role="alert" className="flex items-start gap-2 text-danger">
            <Icon name="alert" className="mt-0.5" />
            <Text tone="danger" as="p">
              {errorMessage(registerUser.error)}
            </Text>
          </div>
        )}
        <Button type="submit" loading={registerUser.isPending} fullWidth>
          {t('auth.createAccount')}
        </Button>
      </form>
      <Text tone="muted">
        {t('auth.register.haveAccount')}{' '}
        <Link to="/login" className="underline">
          {t('auth.signIn')}
        </Link>
      </Text>
    </AuthLayout>
  );
}
