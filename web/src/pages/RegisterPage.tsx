import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Link, Navigate } from 'react-router';

import { useRegister } from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { Input } from '../components/atoms/Input/Input';
import { Text } from '../components/atoms/Text/Text';
import { FormField } from '../components/molecules/FormField';
import { AuthLayout } from '../components/templates/AuthLayout';
import { useAuth } from '../features/auth/AuthContext';
import { registerSchema, type RegisterValues } from '../features/auth/forms';
import { errorMessage } from '../lib/errors';

export function RegisterPage() {
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
    <AuthLayout title="Crear cuenta">
      <form
        noValidate
        onSubmit={handleSubmit((values) =>
          registerUser.mutate(
            { data: { ...values, timezone, locale: 'es' } },
            { onSuccess: signIn },
          ),
        )}
        className="flex flex-col gap-4"
      >
        <FormField label="Nombre" error={errors.displayName?.message}>
          {(p) => <Input autoComplete="name" {...p} {...register('displayName')} />}
        </FormField>
        <FormField label="Correo" error={errors.email?.message}>
          {(p) => <Input type="email" autoComplete="email" {...p} {...register('email')} />}
        </FormField>
        <FormField label="Contraseña" error={errors.password?.message} hint="Mínimo 10 caracteres">
          {(p) => (
            <Input type="password" autoComplete="new-password" {...p} {...register('password')} />
          )}
        </FormField>
        <FormField label="Soy">
          {({ id }) => (
            <select
              id={id}
              className="min-h-touch w-full rounded-md border-2 border-border bg-surface-raised px-3 text-body"
              {...register('role')}
            >
              <option value="patient">Paciente</option>
              <option value="caregiver">Cuidador</option>
            </select>
          )}
        </FormField>
        {registerUser.error && (
          <Text tone="danger" as="p">
            {errorMessage(registerUser.error)}
          </Text>
        )}
        <Button type="submit" loading={registerUser.isPending} fullWidth>
          Crear cuenta
        </Button>
      </form>
      <Text tone="muted">
        ¿Ya tienes cuenta?{' '}
        <Link to="/login" className="underline">
          Iniciar sesión
        </Link>
      </Text>
    </AuthLayout>
  );
}
