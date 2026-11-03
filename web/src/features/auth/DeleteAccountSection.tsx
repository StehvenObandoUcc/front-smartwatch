import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect, useRef, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';

import { useDeleteMe, type User } from '../../api/generated';
import { Button } from '../../components/atoms/Button/Button';
import { Input } from '../../components/atoms/Input/Input';
import { Text } from '../../components/atoms/Text/Text';
import { FormField } from '../../components/molecules/FormField';
import { errorMessage } from '../../lib/errors';
import { useAuth } from './AuthContext';
import { deleteAccountSchema, type DeleteAccountValues } from './forms';

const consequences: Record<User['role'], string> = {
  patient:
    'Se borrarán tu cuenta y todos tus datos de salud: medicamentos, horarios, tomas, reportes y consentimientos. Tu reloj quedará desvinculado.',
  caregiver:
    'Se borrará tu cuenta. Los pacientes que gestionas y no tienen otro cuidador activo se borrarán con todos sus datos; los que tienen otros cuidadores se conservan para ellos.',
};

type PanelProps = {
  userRole: User['role'];
  /** Envío en curso: bloquea el formulario y muestra el estado de carga. */
  pending?: boolean;
  /** Texto del error del último intento (contraseña incorrecta, límite de intentos…). */
  error?: string | undefined;
  /** Se llama con la contraseña ya validada. */
  onConfirm: (password: string) => void;
  /** Se llama al cancelar, para descartar el error del intento anterior. */
  onCancel?: () => void;
  /** Para mostrar el formulario ya desplegado (historias y pruebas). */
  initialOpen?: boolean;
};

/** Zona de peligro de Ajustes: formulario desplegable en línea (sin modal) para eliminar la cuenta. */
export function DeleteAccountPanel({
  userRole,
  pending = false,
  error,
  onConfirm,
  onCancel,
  initialOpen = false,
}: PanelProps) {
  const [open, setOpen] = useState(initialOpen);
  const openerRef = useRef<HTMLDivElement>(null);
  const restoreFocus = useRef(false);
  const { register, handleSubmit, formState, reset, setFocus } = useForm<DeleteAccountValues>({
    resolver: zodResolver(deleteAccountSchema),
    defaultValues: { password: '', confirm: false },
  });
  const { errors } = formState;

  // Al desplegar, el foco va a la contraseña; al cancelar, vuelve al botón que abrió el formulario.
  useEffect(() => {
    if (open) {
      if (restoreFocus.current) setFocus('password');
    } else if (restoreFocus.current) {
      restoreFocus.current = false;
      openerRef.current?.querySelector('button')?.focus();
    }
  }, [open, setFocus]);

  const cancel = () => {
    reset();
    onCancel?.();
    restoreFocus.current = true;
    setOpen(false);
  };

  return (
    <section aria-labelledby="delete-title" className="flex flex-col gap-2">
      <Text id="delete-title" variant="subtitle" as="h2">
        Eliminar cuenta
      </Text>
      {!open ? (
        <>
          <Text tone="muted">
            Si eliminas tu cuenta se borran tus datos de forma permanente. Antes de continuar te
            mostraremos qué se borrará.
          </Text>
          <div ref={openerRef}>
            <Button
              variant="danger"
              onClick={() => {
                restoreFocus.current = true;
                setOpen(true);
              }}
            >
              Eliminar mi cuenta
            </Button>
          </div>
        </>
      ) : (
        <form
          noValidate
          aria-label="Eliminar mi cuenta"
          onSubmit={handleSubmit((values) => onConfirm(values.password))}
          className="flex flex-col gap-4"
        >
          <Text as="p">{consequences[userRole]}</Text>
          <Text as="p" className="font-semibold">
            Esta acción no se puede deshacer.
          </Text>
          <FormField label="Tu contraseña actual" error={errors.password?.message}>
            {(p) => (
              <Input
                type="password"
                autoComplete="current-password"
                disabled={pending}
                {...p}
                {...register('password')}
              />
            )}
          </FormField>
          <div className="flex flex-col gap-1">
            <label className="flex min-h-touch items-center gap-3 py-2">
              <input
                type="checkbox"
                className="size-6 shrink-0 accent-primary"
                disabled={pending}
                aria-invalid={errors.confirm ? true : undefined}
                aria-describedby={errors.confirm ? 'delete-confirm-error' : undefined}
                {...register('confirm')}
              />
              <span className="text-body">Entiendo que no se puede deshacer</span>
            </label>
            {errors.confirm && (
              <p id="delete-confirm-error" role="alert" className="text-caption text-danger">
                {errors.confirm.message}
              </p>
            )}
          </div>
          {error && (
            <div role="alert">
              <Text tone="danger" as="p">
                {error}
              </Text>
            </div>
          )}
          <div className="flex flex-wrap gap-3">
            <Button variant="secondary" onClick={cancel} disabled={pending}>
              Cancelar
            </Button>
            <Button variant="danger" type="submit" loading={pending}>
              Eliminar definitivamente
            </Button>
          </div>
        </form>
      )}
    </section>
  );
}

/** Conecta el panel con la API: al borrar la cuenta termina la sesión local y vuelve al login. */
export function DeleteAccountSection() {
  const { auth, endSession } = useAuth();
  const navigate = useNavigate();
  const deleteMe = useDeleteMe({
    mutation: {
      onSuccess: () => {
        // El backend ya borró la cookie de sesión: no se llama a /auth/logout.
        navigate('/login?cuentaEliminada=1', { replace: true });
        endSession({ accountDeleted: true });
      },
    },
  });
  if (auth.status !== 'authed') return null;
  return (
    <DeleteAccountPanel
      userRole={auth.user.role}
      pending={deleteMe.isPending}
      error={deleteMe.error ? errorMessage(deleteMe.error) : undefined}
      onConfirm={(password) => deleteMe.mutate({ data: { password } })}
      onCancel={() => deleteMe.reset()}
    />
  );
}
