import { defineConfig } from 'orval';

export default defineConfig({
  api: {
    input: '../.contract/openapi.yaml',
    output: {
      target: './src/api/generated.ts',
      client: 'react-query',
      httpClient: 'axios',
      mode: 'single',
      clean: true,
      mock: { generators: [{ type: 'msw' }], delay: false },
      prettier: true,
      override: {
        mutator: { path: './src/lib/http.ts', name: 'http' },
      },
    },
  },
});
