import type { ReactNode } from 'react';

import type { IconName } from '../atoms/Icon/Icon';
import { Plate } from '../atoms/Plate/Plate';
import { Text } from '../atoms/Text/Text';

type Props = {
  icon: IconName;
  title: string;
  /** Qué hacer ahora: nunca un vacío sin salida. */
  body: string;
  action?: ReactNode;
};

/** Estado vacío: placa, qué pasa, el siguiente paso y, si hay, el botón para darlo. */
export function EmptyState({ icon, title, body, action }: Props) {
  return (
    <div className="flex flex-col items-start gap-3 rounded-lg border border-dashed border-border-strong p-6">
      <Plate icon={icon} tone="neutral" size="lg" />
      <Text variant="subtitle" as="p">
        {title}
      </Text>
      <Text tone="muted">{body}</Text>
      {action}
    </div>
  );
}
