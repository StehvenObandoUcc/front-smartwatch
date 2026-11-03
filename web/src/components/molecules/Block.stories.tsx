import type { Meta, StoryObj } from '@storybook/react-vite';

import { Skeleton } from '../atoms/Skeleton/Skeleton';
import { Text } from '../atoms/Text/Text';
import { Block } from './Block';
import { EmptyState } from './EmptyState';
import { ErrorRetry } from './ErrorRetry';

const meta = {
  title: 'Molecules/Block',
  component: Block,
  args: {
    icon: 'today',
    title: 'Hoy',
    question: '¿Cómo va el día?',
    children: <Text>2 de 3 dosis tomadas</Text>,
  },
} satisfies Meta<typeof Block>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Normal: Story = {};

export const Cargando: Story = { args: { children: <Skeleton label="Cargando dosis" /> } };

export const Vacio: Story = {
  args: {
    children: (
      <EmptyState icon="today" title="Hoy no hay dosis" body="Agrega un medicamento con horario." />
    ),
  },
};

export const ConError: Story = {
  args: { children: <ErrorRetry message="No pudimos cargar las dosis." onRetry={() => {}} /> },
};
