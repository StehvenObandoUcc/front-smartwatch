import { describe, expect, it } from 'vitest';

import { registerSchema, resetPasswordSchema } from './forms';

describe('resetPasswordSchema', () => {
  it('exige 10 caracteres y que coincidan', () => {
    expect(resetPasswordSchema.safeParse({ newPassword: 'corta', confirm: 'corta' }).success).toBe(
      false,
    );
    expect(
      resetPasswordSchema.safeParse({ newPassword: 'una-clave-larga', confirm: 'otra-clave-larga' })
        .success,
    ).toBe(false);
    expect(
      resetPasswordSchema.safeParse({ newPassword: 'una-clave-larga', confirm: 'una-clave-larga' })
        .success,
    ).toBe(true);
  });
});

describe('registerSchema', () => {
  it('acepta particular o cuidador y rechaza otros roles', () => {
    const base = { displayName: 'Ana', email: 'ana@example.com', password: 'una-clave-larga' };
    expect(registerSchema.safeParse({ ...base, role: 'caregiver' }).success).toBe(true);
    expect(registerSchema.safeParse({ ...base, role: 'admin' }).success).toBe(false);
  });
});
