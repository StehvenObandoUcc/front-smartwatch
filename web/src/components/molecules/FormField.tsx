import { useId, type ReactNode } from 'react';

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

export function FormField({ label, error, hint, children }: Props) {
  const id = useId();
  const noteId = error || hint ? `${id}-note` : undefined;
  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={id} className="text-body font-semibold">
        {label}
      </label>
      {children({ id, 'aria-describedby': noteId, invalid: Boolean(error) })}
      {error ? (
        <p id={noteId} role="alert" className="text-caption text-danger">
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
