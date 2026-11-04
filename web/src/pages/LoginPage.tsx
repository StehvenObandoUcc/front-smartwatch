import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { Link, Navigate, useSearchParams } from 'react-router';

import { useLogin } from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { Icon } from '../components/atoms/Icon/Icon';
import { Input } from '../components/atoms/Input/Input';
import { PasswordInput } from '../components/molecules/PasswordInput';
import { Text } from '../components/atoms/Text/Text';
import { FormField } from '../components/molecules/FormField';
import { AuthLayout } from '../components/templates/AuthLayout';
import { useAuth } from '../features/auth/AuthContext';
import { loginSchema, type LoginValues } from '../features/auth/forms';
import { errorMessage } from '../lib/errors';

export function LoginPage() {
  const { t } = useTranslation();
  const { auth, signIn } = useAuth();
  const params = useSearchParams()[0];
  const requested = params.get('next');
  const accountDeleted = params.get('cuentaEliminada') === '1';
  // Solo rutas internas: evita redirigir a otro sitio.
  const next = requested?.startsWith('/') && !requested.startsWith('//') ? requested : '/';
  // «Vincular un reloj» entra igual y luego lleva a Ajustes, donde se escribe el código del reloj.
  const [target, setTarget] = useState(next);
  const login = useLogin();
  const { register, handleSubmit, formState } = useForm<LoginValues>({
    resolver: zodResolver(loginSchema),
  });
  if (auth.status === 'authed') return <Navigate to={target} replace />;
  const { errors } = formState;
  return (
    <AuthLayout title={t('auth.signIn')}>
      {accountDeleted && (
        <div role="status">
          <Text tone="success" as="p">
            {t('auth.login.accountDeleted')}
          </Text>
        </div>
      )}
      <form
        noValidate
        onSubmit={handleSubmit((data) => login.mutate({ data }, { onSuccess: signIn }))}
        className="flex flex-col gap-4"
      >
        <FormField label={t('auth.email')} error={errors.email?.message}>
          {(p) => <Input type="email" autoComplete="email" {...p} {...register('email')} />}
        </FormField>
        <FormField label={t('auth.password')} error={errors.password?.message}>
          {(p) => (
            <PasswordInput autoComplete="current-password" {...p} {...register('password')} />
          )}
        </FormField>
        {login.error && (
          <div role="alert" className="flex items-start gap-2 text-danger">
            <Icon name="alert" className="mt-0.5" />
            <Text tone="danger" as="p">
              {errorMessage(login.error)}
            </Text>
          </div>
        )}
        <Button
          type="submit"
          loading={login.isPending && target === next}
          fullWidth
          onClick={() => setTarget(next)}
        >
          {t('auth.login.submit')}
        </Button>
        <Button
          type="submit"
          variant="secondary"
          icon="watch"
          loading={login.isPending && target !== next}
          fullWidth
          onClick={() => setTarget('/ajustes')}
        >
          {t('auth.login.pairWatch')}
        </Button>
      </form>
      <Text tone="muted">
        <Link to="/recuperar" className="underline">
          {t('auth.login.forgot')}
        </Link>
      </Text>
      <Text tone="muted">
        {t('auth.login.noAccount')}{' '}
        <Link to="/registro" className="underline">
          {t('auth.createAccount')}
        </Link>
      </Text>
    </AuthLayout>
  );
}
