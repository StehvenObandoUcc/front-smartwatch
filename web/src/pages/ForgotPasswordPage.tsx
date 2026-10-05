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
        <div role="status" className="flex flex-col gap-2">
          <Text tone="success">
            Si el correo existe, te enviamos instrucciones para elegir una contraseña nueva. El
            enlace caduca en una hora.
          </Text>
          <Text tone="muted">
            Solo llega si la cuenta tiene el correo verificado. Si no te llega, revisa la carpeta de
            spam o verifica tu correo desde el panel.
          </Text>
        </div>
      ) : (
        <form
          noValidate
          onSubmit={handleSubmit((data) => forgot.mutate({ data }))}
          className="flex flex-col gap-4"
        >
          <Text tone="muted">
            Te enviaremos un enlace para elegir una contraseña nueva. La cuenta debe tener el correo
            verificado.
          </Text>
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
