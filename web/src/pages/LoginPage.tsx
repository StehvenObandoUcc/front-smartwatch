import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Link, Navigate, useSearchParams } from 'react-router';

import { useLogin } from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { Input } from '../components/atoms/Input/Input';
import { Text } from '../components/atoms/Text/Text';
import { FormField } from '../components/molecules/FormField';
import { AuthLayout } from '../components/templates/AuthLayout';
import { useAuth } from '../features/auth/AuthContext';
import { loginSchema, type LoginValues } from '../features/auth/forms';
import { errorMessage } from '../lib/errors';

export function LoginPage() {
  const { auth, signIn } = useAuth();
  const requested = useSearchParams()[0].get('next');
  // Solo rutas internas: evita redirigir a otro sitio.
  const next = requested?.startsWith('/') && !requested.startsWith('//') ? requested : '/';
  const login = useLogin();
  const { register, handleSubmit, formState } = useForm<LoginValues>({
    resolver: zodResolver(loginSchema),
  });
  if (auth.status === 'authed') return <Navigate to={next} replace />;
  const { errors } = formState;
  return (
    <AuthLayout title="Iniciar sesión">
      <form
        noValidate
        onSubmit={handleSubmit((data) => login.mutate({ data }, { onSuccess: signIn }))}
        className="flex flex-col gap-4"
      >
        <FormField label="Correo" error={errors.email?.message}>
          {(p) => <Input type="email" autoComplete="email" {...p} {...register('email')} />}
        </FormField>
        <FormField label="Contraseña" error={errors.password?.message}>
          {(p) => (
            <Input
              type="password"
              autoComplete="current-password"
              {...p}
              {...register('password')}
            />
          )}
        </FormField>
        {login.error && (
          <Text tone="danger" as="p">
            {errorMessage(login.error)}
          </Text>
        )}
        <Button type="submit" loading={login.isPending} fullWidth>
          Entrar
        </Button>
      </form>
      <Text tone="muted">
        <Link to="/recuperar" className="underline">
          ¿Olvidaste tu contraseña?
        </Link>
      </Text>
      <Text tone="muted">
        ¿Sin cuenta?{' '}
        <Link to="/registro" className="underline">
          Crear cuenta
        </Link>
      </Text>
    </AuthLayout>
  );
}
