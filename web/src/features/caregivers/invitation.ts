import { z } from 'zod';

/** 10 caracteres sin ambigüedades (sin 0/O, 1/I/L), como define el contrato. */
export const invitationCodeSchema = z
  .string()
  .transform((value) => value.trim().toUpperCase())
  .pipe(z.string().regex(/^[A-HJKMNP-Z2-9]{10}$/, 'El código tiene 10 caracteres'));
