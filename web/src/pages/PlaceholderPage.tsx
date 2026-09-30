import { Text } from '../components/atoms/Text/Text';

export function PlaceholderPage({ name }: { name: string }) {
  return <Text tone="muted">La sección «{name}» llega en una fase posterior.</Text>;
}
