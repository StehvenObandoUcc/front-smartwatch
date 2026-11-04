import type { ParseKeys } from 'i18next';
import { z } from 'zod';

import { i18n } from '../../i18n';

// El mensaje se traduce al validar, en el idioma activo.
const msg = (key: ParseKeys) => ({ error: () => i18n.t(key) });

export const loginSchema = z.object({
  email: z.email(msg('forms.emailInvalid')),
  password: z.string().min(1, msg('forms.passwordRequired')),
});

export const registerSchema = z.object({
  displayName: z.string().trim().min(1, msg('forms.nameRequired')).max(80),
  email: z.email(msg('forms.emailInvalid')),
  password: z.string().min(10, msg('forms.passwordMin')).max(128),
  role: z.enum(['patient', 'caregiver']),
});

export const forgotPasswordSchema = z.object({
  email: z.email(msg('forms.emailInvalid')),
});

export const resetPasswordSchema = z
  .object({
    newPassword: z.string().min(10, msg('forms.passwordMin')).max(128),
    confirm: z.string(),
  })
  .refine((v) => v.newPassword === v.confirm, {
    path: ['confirm'],
    ...msg('forms.passwordsMismatch'),
  });

export const deleteAccountSchema = z.object({
  password: z.string().min(1, msg('forms.passwordRequired')).max(128, msg('forms.passwordMax')),
  confirm: z.boolean().refine((v) => v, msg('forms.confirmIrreversible')),
});

export type DeleteAccountValues = z.infer<typeof deleteAccountSchema>;
export type ForgotPasswordValues = z.infer<typeof forgotPasswordSchema>;
export type ResetPasswordValues = z.infer<typeof resetPasswordSchema>;
export type LoginValues = z.infer<typeof loginSchema>;
export type RegisterValues = z.infer<typeof registerSchema>;
