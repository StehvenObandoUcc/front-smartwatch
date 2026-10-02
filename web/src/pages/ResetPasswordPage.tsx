import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Link, useSearchParams } from 'react-router';

import { useResetPassword } from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { Input } from '../components/atoms/Input/Input';
import { Text } from '../components/atoms/Text/Text';
import { FormField } from '../components/molecules/FormField';
import { AuthLayout } from '../components/templates/AuthLayout';
import { resetPasswordSchema, type ResetPasswordValues } from '../features/auth/forms';
import { errorMessage } from '../lib/errors';

export function ResetPasswordPage() {
  const token = useSearchParams()[0].get('token');
  const reset = useResetPassword();
  const { register, handleSubmit, formState } = useForm<ResetPasswordValues>({
    resolver: zodResolver(resetPasswordSchema),
  });
  const { errors } = formState;

  if (!token) {
    return (
      <AuthLayout title="Nueva contraseña">
        <Text tone="danger">El enlace no es válido.</Text>
        <Link to="/recuperar" className="underline">
          Pedir un enlace nuevo
        </Link>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout title="Nueva contraseña">
      {reset.isSuccess ? (
        <>
          <Text tone="success">Contraseña cambiada.</Text>
          <Link to="/login" className="underline">
            Iniciar sesión
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
            label="Contraseña nueva"
            error={errors.newPassword?.message}
            hint="Mínimo 10 caracteres"
          >
            {(p) => (
              <Input
                type="password"
                autoComplete="new-password"
                {...p}
                {...register('newPassword')}
              />
            )}
          </FormField>
          <FormField label="Repite la contraseña" error={errors.confirm?.message}>
            {(p) => (
              <Input type="password" autoComplete="new-password" {...p} {...register('confirm')} />
            )}
          </FormField>
          {reset.error && (
            <Text tone="danger" as="p">
              {errorMessage(reset.error)}{' '}
              <Link to="/recuperar" className="underline">
                Pedir otro enlace
              </Link>
            </Text>
          )}
          <Button type="submit" loading={reset.isPending} fullWidth>
            Cambiar contraseña
          </Button>
        </form>
      )}
    </AuthLayout>
  );
}
