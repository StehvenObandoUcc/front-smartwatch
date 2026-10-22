import {
  Archive,
  CalendarDays,
  ChartColumn,
  Check,
  ChevronLeft,
  ChevronRight,
  CircleMinus,
  Clock,
  Download,
  FileText,
  LayoutGrid,
  Link,
  LogOut,
  Mail,
  MessageCircle,
  Mic,
  Moon,
  Pencil,
  Pill,
  Plus,
  Send,
  Settings,
  ShieldCheck,
  Sun,
  Sunset,
  TriangleAlert,
  Undo2,
  User,
  Users,
  Watch,
  X,
  Bell,
  type LucideIcon,
} from 'lucide-react';

/**
 * Vocabulario de pictogramas de Recuerda: uno por opción y por categoría, el mismo en la web y en el
 * reloj (Lucide, trazo grueso). El nombre dice qué significa, no qué dibuja.
 */
export const icons = {
  // Secciones
  today: Clock,
  medications: Pill,
  agenda: CalendarDays,
  history: ChartColumn,
  more: LayoutGrid,
  notifications: Bell,
  settings: Settings,
  reports: FileText,
  assistant: MessageCircle,
  watch: Watch,
  caregivers: Users,
  patient: User,
  consent: ShieldCheck,
  // Franjas del día
  morning: Sun,
  afternoon: Sunset,
  night: Moon,
  // Estados de una dosis
  taken: Check,
  due: Clock,
  missed: X,
  skipped: CircleMinus,
  // Acciones
  add: Plus,
  edit: Pencil,
  remove: Archive,
  back: ChevronLeft,
  forward: ChevronRight,
  undo: Undo2,
  link: Link,
  email: Mail,
  telegram: Send,
  voice: Mic,
  download: Download,
  logout: LogOut,
  alert: TriangleAlert,
  close: X,
} satisfies Record<string, LucideIcon>;

export type IconName = keyof typeof icons;

export const iconNames = Object.keys(icons) as IconName[];
