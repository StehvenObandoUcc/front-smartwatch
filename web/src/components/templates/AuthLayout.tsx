import type { ReactNode } from 'react';

import { Text } from '../atoms/Text/Text';

export function AuthLayout({ title, children }: { title: string; children: ReactNode }) {
  return (
    <main className="mx-auto flex min-h-dvh w-full max-w-md flex-col justify-center gap-6 bg-bg p-4">
      <Text variant="caption" tone="muted" as="p">
        Recordatorios de medicamentos
      </Text>
      <div className="card flex flex-col gap-6 p-6">
        <Text variant="display" as="h1">
          {title}
        </Text>
        {children}
      </div>
    </main>
  );
}
