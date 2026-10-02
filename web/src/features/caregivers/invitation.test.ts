import { describe, expect, it } from 'vitest';

import { invitationCodeSchema } from './invitation';

describe('invitationCodeSchema', () => {
  it('normaliza a mayúsculas y rechaza caracteres ambiguos o largo incorrecto', () => {
    expect(invitationCodeSchema.parse(' abcdefghjk ')).toBe('ABCDEFGHJK');
    expect(invitationCodeSchema.safeParse('ABCDEFGHJ0').success).toBe(false);
    expect(invitationCodeSchema.safeParse('ABC').success).toBe(false);
  });
});
