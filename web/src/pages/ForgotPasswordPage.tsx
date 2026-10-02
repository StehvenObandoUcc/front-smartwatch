import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
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
  const forgot = useForgotPassword();
  const { register, handleSubmit, formState } = useForm<ForgotPasswordValues>({
    resolver: zodResolver(forgotPasswordSchema),
  });
  return (
    <AuthLayout title="Recuperar contraseña">
      {forgot.isSuccess ? (
        <Text tone="success">
          Si la cuenta existe y su correo está verificado, te enviamos un enlace para elegir una
          contraseña nueva. Caduca en una hora.
        </Text>
      ) : (
        <form
          noValidate
          onSubmit={handleSubmit((data) => forgot.mutate({ data }))}
          className="flex flex-col gap-4"
        >
          <FormField label="Correo de tu cuenta" error={formState.errors.email?.message}>
            {(p) => <Input type="email" autoComplete="email" {...p} {...register('email')} />}
          </FormField>
          {forgot.error && <Text tone="danger">{errorMessage(forgot.error)}</Text>}
          <Button type="submit" loading={forgot.isPending} fullWidth>
            Enviar enlace
          </Button>
        </form>
      )}
      <Text>
        <Link to="/login" className="underline">
          Volver a iniciar sesión
        </Link>
      </Text>
    </AuthLayout>
  );
}
