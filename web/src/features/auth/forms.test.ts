import { describe, expect, it } from 'vitest';

import { deleteAccountSchema, registerSchema, resetPasswordSchema } from './forms';

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

describe('deleteAccountSchema', () => {
  it('exige contraseña', () => {
    const result = deleteAccountSchema.safeParse({ password: '', confirm: true });
    expect(result.success).toBe(false);
    expect(result.error?.issues[0]?.message).toBe('Escribe tu contraseña');
  });

  it('limita la contraseña a 128 caracteres', () => {
    expect(
      deleteAccountSchema.safeParse({ password: 'a'.repeat(128), confirm: true }).success,
    ).toBe(true);
    expect(
      deleteAccountSchema.safeParse({ password: 'a'.repeat(129), confirm: true }).success,
    ).toBe(false);
  });

  it('exige confirmar que no se puede deshacer', () => {
    const result = deleteAccountSchema.safeParse({ password: 'una-clave', confirm: false });
    expect(result.success).toBe(false);
    expect(result.error?.issues[0]?.message).toBe(
      'Confirma que entiendes que no se puede deshacer',
    );
  });

  it('acepta contraseña y confirmación', () => {
    expect(deleteAccountSchema.safeParse({ password: 'una-clave', confirm: true }).success).toBe(
      true,
    );
  });
});
