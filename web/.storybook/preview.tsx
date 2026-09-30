import type { Decorator, Preview } from '@storybook/react-vite';

import { viewports } from '../src/design/tokens';
import '../src/index.css';

const withTheme: Decorator = (Story, context) => {
  const theme = context.globals['theme'] as 'light' | 'dark';
  document.documentElement.dataset['theme'] = theme;
  return <Story />;
};

const preview: Preview = {
  decorators: [withTheme],
  globalTypes: {
    theme: {
      description: 'Tema',
      toolbar: {
        title: 'Tema',
        icon: 'mirror',
        items: [
          { value: 'light', title: 'Claro' },
          { value: 'dark', title: 'Oscuro' },
        ],
        dynamicTitle: true,
      },
    },
  },
  initialGlobals: { theme: 'light' },
  parameters: {
    // Los tres tamaños del proyecto: móvil < 640, tablet 640–1024, escritorio > 1024.
    viewport: { options: viewports },
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
    // Las violaciones de accesibilidad hacen fallar las pruebas de historias.
    a11y: { test: 'error' },
  },
};

export default preview;
