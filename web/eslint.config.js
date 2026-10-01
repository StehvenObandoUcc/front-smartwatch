import js from '@eslint/js';
import prettier from 'eslint-config-prettier';
import jsxA11y from 'eslint-plugin-jsx-a11y';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import { defineConfig, globalIgnores } from 'eslint/config';
import globals from 'globals';
import tseslint from 'typescript-eslint';

const tokensOnly =
  'Usa los tokens de src/design/tokens: ningún valor visual literal fuera de ellos.';

export default defineConfig([
  globalIgnores(['src/api/generated.ts', 'dist', 'storybook-static', 'coverage']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      js.configs.recommended,
      tseslint.configs.strict,
      reactHooks.configs.flat['recommended-latest'],
      reactRefresh.configs.vite,
      jsxA11y.flatConfigs.strict,
      prettier,
    ],
    languageOptions: { ecmaVersion: 2023, globals: globals.browser },
  },
  {
    // Ningún color, tamaño, espacio o fuente literal fuera de tokens/.
    files: ['src/**/*.{ts,tsx}'],
    ignores: ['src/design/tokens/**'],
    rules: {
      'no-restricted-syntax': [
        'error',
        { selector: 'Literal[value=/[#][0-9a-fA-F]{3,8}/]', message: tokensOnly },
        { selector: 'Literal[value=/(rgb|hsl|oklch)a?[(]/]', message: tokensOnly },
        { selector: 'Literal[value=/[0-9](px|rem|em|vh|vw)/]', message: tokensOnly },
        // Valores arbitrarios de Tailwind: p-[13px], bg-[#fff]…
        { selector: 'Literal[value=/[a-z]-[\[]/]', message: tokensOnly },
        { selector: 'TemplateElement[value.raw=/[a-z]-[\[]/]', message: tokensOnly },
        { selector: 'JSXAttribute[name.name="style"]', message: tokensOnly },
      ],
    },
  },
  {
    files: ['src/**/*.stories.tsx', 'src/**/*.test.tsx', 'src/features/**/*.tsx'],
    rules: { 'react-refresh/only-export-components': 'off' },
  },
]);
