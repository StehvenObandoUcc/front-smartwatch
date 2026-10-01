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

export type LoginValues = z.infer<typeof loginSchema>;
export type RegisterValues = z.infer<typeof registerSchema>;
