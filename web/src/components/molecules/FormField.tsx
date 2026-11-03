import { useId, type ReactNode } from 'react';

import { Icon } from '../atoms/Icon/Icon';
import { Text } from '../atoms/Text/Text';

type Props = {
  label: string;
  error?: string | undefined;
  hint?: string;
  children: (props: {
    id: string;
    'aria-describedby': string | undefined;
    invalid: boolean;
  }) => ReactNode;
};

/** Etiqueta siempre visible encima del campo; debajo, la pista o el error con icono y texto. */
export function FormField({ label, error, hint, children }: Props) {
  const id = useId();
  const noteId = error || hint ? `${id}-note` : undefined;
  return (
    <div className="flex flex-col gap-2">
      <label htmlFor={id} className="text-body font-medium">
        {label}
      </label>
      {children({ id, 'aria-describedby': noteId, invalid: Boolean(error) })}
      {error ? (
        <p id={noteId} role="alert" className="flex items-start gap-2 text-caption text-danger">
          <Icon name="alert" className="mt-0.5 size-4" />
          {error}
        </p>
      ) : hint ? (
        <Text as="p" variant="caption" tone="muted" id={noteId}>
          {hint}
        </Text>
      ) : null}
    </div>
  );
}
