import { expect, test } from '@playwright/test';

import { fakeApi } from './api.ts';

test('se puede cambiar a inglés: la landing y el login', async ({ page }) => {
  await fakeApi(page);
  await page.goto('/');
  await expect(page.locator('html')).toHaveAttribute('lang', 'es');

  await page.locator('label[lang="en"]').click();
  await expect(page.locator('html')).toHaveAttribute('lang', 'en');
  await expect(page.getByRole('heading', { level: 1 })).toContainText('Medication right on time');

  await page.getByRole('link', { name: 'Sign in', exact: true }).click();
  await expect(page.getByRole('heading', { level: 1, name: 'Sign in' })).toBeVisible();
  await page.getByRole('button', { name: 'Sign in' }).click();
  await expect(page.getByText('Enter a valid email address')).toBeVisible();
  await expect(page.getByText('Enter your password')).toBeVisible();

  // La elección se recuerda al volver.
  await page.reload();
  await expect(page.getByRole('button', { name: 'Sign in' })).toBeVisible();
  await expect(page.getByRole('radio', { name: 'English' })).toBeChecked();
});
