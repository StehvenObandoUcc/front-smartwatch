import { z } from 'zod';

export const loginSchema = z.object({
  email: z.email('Escribe un correo válido'),
  password: z.string().min(1, 'Escribe tu contraseña'),
});

export const registerSchema = z.object({
  displayName: z.string().trim().min(1, 'Escribe tu nombre').max(80),
  email: z.email('Escribe un correo válido'),
  password: z.string().min(10, 'Mínimo 10 caracteres').max(128),
  role: z.enum(['patient', 'caregiver']),
});

export const forgotPasswordSchema = z.object({
  email: z.email('Escribe un correo válido'),
});

export const resetPasswordSchema = z
  .object({
    newPassword: z.string().min(10, 'Mínimo 10 caracteres').max(128),
    confirm: z.string(),
  })
  .refine((v) => v.newPassword === v.confirm, {
    path: ['confirm'],
    message: 'Las contraseñas no coinciden',
  });

export const deleteAccountSchema = z.object({
  password: z.string().min(1, 'Escribe tu contraseña').max(128, 'Máximo 128 caracteres'),
  confirm: z.boolean().refine((v) => v, 'Confirma que entiendes que no se puede deshacer'),
});

export type DeleteAccountValues = z.infer<typeof deleteAccountSchema>;
export type ForgotPasswordValues = z.infer<typeof forgotPasswordSchema>;
export type ResetPasswordValues = z.infer<typeof resetPasswordSchema>;
export type LoginValues = z.infer<typeof loginSchema>;
export type RegisterValues = z.infer<typeof registerSchema>;
