import { expect, test } from '@playwright/test';

import { fakeApi } from './api.ts';

test('sin sesión, la raíz muestra la landing', async ({ page }) => {
  await fakeApi(page);
  await page.goto('/');
  await expect(page.getByRole('heading', { level: 1 })).toContainText('Su medicamento a su hora');
});

test('el login valida antes de enviar', async ({ page }) => {
  await fakeApi(page);
  await page.goto('/login');
  await page.getByRole('button', { name: 'Entrar' }).click();
  await expect(page.getByText('Escribe un correo válido')).toBeVisible();
  await expect(page.getByText('Escribe tu contraseña')).toBeVisible();
});

test('entrar lleva al panel', async ({ page }) => {
  await fakeApi(page);
  await page.goto('/login');
  await page.getByLabel('Correo').fill('ana@example.com');
  await page.getByLabel('Contraseña').fill('una-clave-larga');
  await page.getByRole('button', { name: 'Entrar' }).click();
  await expect(page.getByRole('heading', { name: 'Hoy', exact: true })).toBeVisible();
});

test('eliminar la cuenta vuelve al login con el aviso', async ({ page }) => {
  await fakeApi(page, { signedIn: true });
  await page.goto('/ajustes');
  await page.getByRole('button', { name: 'Eliminar mi cuenta' }).click();
  await page.getByLabel('Tu contraseña actual').fill('una-clave-larga');
  await page.getByRole('checkbox', { name: /no se puede deshacer/ }).check();
  await page.getByRole('button', { name: 'Eliminar definitivamente' }).click();
  await expect(page).toHaveURL(/\/login\?cuentaEliminada=1/);
  await expect(page.getByText('Tu cuenta se eliminó correctamente.')).toBeVisible();
});

test('con la contraseña mal no se elimina la cuenta', async ({ page }) => {
  await fakeApi(page, { signedIn: true, deleteMeStatus: 403 });
  await page.goto('/ajustes');
  await page.getByRole('button', { name: 'Eliminar mi cuenta' }).click();
  await page.getByLabel('Tu contraseña actual').fill('mala');
  await page.getByRole('checkbox', { name: /no se puede deshacer/ }).check();
  await page.getByRole('button', { name: 'Eliminar definitivamente' }).click();
  await expect(page.getByText('La contraseña no es correcta.')).toBeVisible();
  await expect(page).toHaveURL(/\/ajustes/);
});
