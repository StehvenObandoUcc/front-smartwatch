import type { ReactNode } from 'react';
import { Link } from 'react-router';

import { Badge } from '../components/atoms/Badge/Badge';
import { buttonClass } from '../components/atoms/Button/buttonClass';
import { Icon, type IconName } from '../components/atoms/Icon/Icon';
import { Plate } from '../components/atoms/Plate/Plate';
import { Ring } from '../components/atoms/Ring/Ring';
import { LanguageSwitch } from '../components/molecules/LanguageSwitch';
import { SignBar } from '../components/molecules/SignBar';
import { ThemeSwitch } from '../components/molecules/ThemeSwitch';
import { WatchMock } from '../components/molecules/WatchMock';
import { medicationHex } from '../design/tokens';
import { useCycle } from '../features/landing/useCycle';
import { useLanguage } from '../i18n';
import { cn } from '../lib/cn';
import { useTheme } from '../lib/theme';

const wrap = 'mx-auto w-full max-w-content px-4 tablet:px-8';

// El héroe recorre el día: cada palabra con el panel de su franja.
const DAY = [
  { word: 'mañana', panel: 'panel-morning' },
  { word: 'tarde', panel: 'panel-afternoon' },
  { word: 'noche', panel: 'panel-night' },
] as const;

const facts = [
  { big: '7 días', small: 'sin internet: el plan vive en el reloj' },
  { big: '3 veces', small: 'se puede posponer, 10 minutos cada una' },
  { big: 'Un código', small: 'corto en el reloj para vincularlo' },
];

const steps: { icon: IconName; title: string; body: string }[] = [
  {
    icon: 'medications',
    title: 'Crea los medicamentos',
    body: 'En el panel, con su dosis, su color y sus horas. Sirve el móvil.',
  },
  {
    icon: 'link',
    title: 'Vincula el reloj',
    body: 'El reloj muestra un código corto; lo escribes en Ajustes y descarga el plan.',
  },
  {
    icon: 'notifications',
    title: 'Mira si se tomó',
    body: 'El reloj suena a su hora. Aquí ves cada dosis y, si falta una, te avisamos.',
  },
];

const watchPoints = [
  'Suena y vibra a la hora: Tomada, Posponer u Omitir, con un toque.',
  'Posponer da 10 minutos, hasta 3 veces; Omitir pide confirmación.',
  'Va por su cuenta, sin teléfono; sin internet avisa igual durante 7 días.',
  'Texto y botones grandes; el color del medicamento siempre con su nombre.',
];

const privacy: { icon: IconName; title: string; body: string }[] = [
  {
    icon: 'consent',
    title: 'Permiso por finalidad',
    body: 'Datos de salud, asistente y avisos se aceptan por separado, y se pueden retirar.',
  },
  {
    icon: 'patient',
    title: 'Solo quien cuida',
    body: 'El tratamiento lo ven el paciente y los cuidadores que invita.',
  },
  {
    icon: 'remove',
    title: 'Borrar la cuenta',
    body: 'Desde Ajustes, con tu contraseña. Se eliminan tus datos.',
  },
];

const faqs = [
  {
    q: '¿Hace falta un teléfono?',
    a: 'No. El reloj Wear OS funciona por su cuenta. El panel se abre desde cualquier navegador, también en el móvil.',
  },
  {
    q: '¿Qué pasa si el reloj se queda sin internet?',
    a: 'Sigue avisando: guarda el plan de los próximos 7 días y las alarmas son del propio reloj. Las tomas se suben cuando vuelve la conexión.',
  },
  {
    q: '¿Qué reloj necesito?',
    a: 'Un reloj con Wear OS 4 o superior, redondo o cuadrado.',
  },
  {
    q: '¿Cómo me entero si no se tomó una dosis?',
    a: 'En el panel la verás como «No tomada». También puedes recibir el aviso por Telegram o por correo.',
  },
  {
    q: '¿Puedo cuidar a más de una persona?',
    a: 'Sí. Desde el panel eliges a quién gestionas y cada persona tiene su plan.',
  },
];

function SectionTitle({ id, children }: { id: string; children: ReactNode }) {
  return (
    <h2
      id={id}
      className="max-w-2xl text-2xl font-medium tracking-display text-balance desktop:text-3xl"
    >
      {children}
    </h2>
  );
}

/** Muestra del panel con datos de ejemplo; el lector oye su descripción. */
function PanelSample() {
  return (
    <div aria-hidden="true" className="grid grid-cols-1 gap-4 tablet:grid-cols-2">
      <div className="panel panel-afternoon flex flex-col items-center gap-4 rounded-lg p-6 shadow-raised">
        <Ring value={5} total={8}>
          <span className="text-2xl leading-none font-medium">5 de 8</span>
          <span className="mt-1 text-caption text-on-panel-muted">dosis de hoy</span>
        </Ring>
        <p className="text-lg">
          <span className="text-on-panel-muted">Próxima</span>{' '}
          <span className="font-medium">14:00 · Losartán</span>
        </p>
      </div>
      <div className="raised flex flex-col gap-2 p-5">
        <SignBar icon="morning" tone="sun" title="Mañana" meta="5:00 a 12:00" as="p" />
        <ul className="rows">
          {[
            { name: 'Losartán', dose: '50 mg', time: '07:00', hex: medicationHex.blue },
            { name: 'Metformina', dose: '850 mg', time: '08:00', hex: medicationHex.teal },
          ].map((d) => (
            <li key={d.name} className="flex items-center gap-3">
              <Plate icon="medications" hex={d.hex} />
              <span className="min-w-0 flex-1">
                <span className="block text-lg leading-tight font-medium">{d.name}</span>
                <span className="text-caption text-text-muted">{d.dose}</span>
              </span>
              <span className="flex flex-col items-end gap-1">
                <span className="font-medium">{d.time}</span>
                <Badge tone="success" icon="taken">
                  Tomada
                </Badge>
              </span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

/** Página pública de `/` para quien no ha entrado: el producto se muestra a sí mismo. */
export function LandingPage() {
  const theme = useTheme();
  const language = useLanguage();
  const cycle = useCycle(DAY.length, 3500);
  const part = DAY[cycle.index] ?? DAY[0];
  return (
    <div className="min-h-dvh bg-bg">
      <a
        href="#contenido"
        className="sr-only focus:not-sr-only focus:absolute focus:top-2 focus:left-2 focus:z-50 focus:rounded-md focus:bg-primary focus:p-3 focus:text-on-primary"
      >
        Saltar al contenido
      </a>
      <header className={cn(wrap, 'flex min-h-20 flex-wrap items-center gap-x-6 gap-y-2 py-3')}>
        <Link to="/" className="flex items-center gap-3 text-lg font-medium tracking-tight">
          <Plate icon="medications" tone="ink" size="sm" />
          Recuerda
        </Link>
        <nav aria-label="Secciones" className="hidden gap-6 desktop:flex">
          {[
            ['#como-funciona', 'Cómo funciona'],
            ['#reloj', 'El reloj'],
            ['#panel', 'El panel'],
            ['#privacidad', 'Privacidad'],
            ['#preguntas', 'Preguntas'],
          ].map(([href, label]) => (
            <a key={href} href={href} className="text-text-muted hover:text-text hover:underline">
              {label}
            </a>
          ))}
        </nav>
        <div className="ml-auto flex flex-wrap items-center justify-end gap-2">
          <LanguageSwitch compact value={language.language} onChange={language.setLanguage} />
          <ThemeSwitch compact value={theme.choice} onChange={theme.setChoice} />
          <Link to="/login" className={buttonClass('quiet')}>
            Entrar
          </Link>
        </div>
      </header>

      <main id="contenido">
        <section aria-labelledby="hero-title" className="px-2 tablet:px-4">
          <div
            className={cn(
              'panel panel-grain mx-auto flex max-w-7xl flex-col items-center gap-10 rounded-lg px-4 py-12 tablet:px-10 tablet:py-16 desktop:flex-row desktop:gap-16 desktop:px-16 desktop:py-24',
              part.panel,
              !cycle.still && !cycle.paused && 'motion-safe:animate-drift',
            )}
          >
            <div className="flex min-w-0 flex-1 flex-col items-start gap-6">
              <h1
                id="hero-title"
                className="text-3xl font-medium tracking-display text-balance desktop:text-4xl"
              >
                Su medicamento a su hora, por la{' '}
                <span
                  key={part.word}
                  aria-live={cycle.still || cycle.paused ? 'off' : 'polite'}
                  className="inline-block underline decoration-2 underline-offset-8 motion-safe:animate-word"
                >
                  {part.word}
                </span>
                .
              </h1>
              <p className="max-w-measure text-lg text-on-panel-muted">
                Un reloj Wear OS que avisa en cada dosis, sin teléfono y sin internet, y un panel
                donde quien cuida ve si se tomó.
              </p>
              <div className="flex w-full flex-col gap-3 tablet:w-auto tablet:flex-row">
                <Link to="/registro" className={buttonClass('primary')}>
                  Crear cuenta
                </Link>
                <Link to="/login" className={buttonClass('secondary', 'min-h-control')}>
                  Ya tengo cuenta
                </Link>
              </div>
              {!cycle.still && (
                <button
                  type="button"
                  onClick={cycle.toggle}
                  className={buttonClass('quiet', '-ml-3 text-caption')}
                >
                  <Icon name={cycle.paused ? 'play' : 'pause'} className="size-5" />
                  {cycle.paused ? 'Reanudar el cambio de palabra' : 'Pausar el cambio de palabra'}
                </button>
              )}
            </div>
            <p className="sr-only">
              Ilustración del reloj: 5 de 8 dosis tomadas, próxima a las 14:00.
            </p>
            <WatchMock className="w-60 tablet:w-72" />
          </div>
        </section>

        <section aria-label="En pocas palabras" className={cn(wrap, 'py-12')}>
          <ul className="grid grid-cols-1 divide-y divide-border tablet:grid-cols-3 tablet:divide-x tablet:divide-y-0">
            {facts.map((f) => (
              <li key={f.big} className="flex flex-col gap-1 py-5 tablet:px-6 tablet:first:pl-0">
                <span className="text-2xl font-medium tracking-display">{f.big}</span>
                <span className="text-text-muted">{f.small}</span>
              </li>
            ))}
          </ul>
        </section>

        <section
          id="como-funciona"
          aria-labelledby="pasos-title"
          className={cn(wrap, 'flex scroll-mt-4 flex-col gap-10 py-16 desktop:py-24')}
        >
          <SectionTitle id="pasos-title">Listo en tres pasos, desde el móvil.</SectionTitle>
          <ol className="grid grid-cols-1 gap-8 desktop:grid-cols-3">
            {steps.map((step, i) => (
              <li key={step.title} className="flex flex-col gap-3 border-t border-border pt-6">
                <span className="flex items-center gap-3">
                  <span className="inline-grid size-plate-md place-items-center rounded-md bg-primary text-lg font-medium text-on-primary">
                    {i + 1}
                  </span>
                  <Icon name={step.icon} className="text-accent" />
                </span>
                <h3 className="text-xl font-medium tracking-tight">{step.title}</h3>
                <p className="max-w-measure text-text-muted">{step.body}</p>
              </li>
            ))}
          </ol>
        </section>

        <section
          id="reloj"
          aria-labelledby="reloj-title"
          className={cn(
            wrap,
            'flex scroll-mt-4 flex-col items-center gap-12 py-16 desktop:flex-row desktop:justify-between desktop:py-24',
          )}
        >
          <div className="flex w-full max-w-xl flex-col items-start gap-6">
            <SectionTitle id="reloj-title">
              El reloj hace el trabajo; el paciente solo toca «Tomada».
            </SectionTitle>
            <ul className="flex flex-col gap-4">
              {watchPoints.map((point) => (
                <li key={point} className="flex gap-3">
                  <span className="inline-grid size-8 shrink-0 place-items-center rounded-full bg-state-taken text-on-state">
                    <Icon name="taken" className="size-5" />
                  </span>
                  <span className="text-lg">{point}</span>
                </li>
              ))}
            </ul>
          </div>
          <p className="sr-only">
            Ilustración de la alerta en el reloj: Losartán, 50 mg, 14:00, con el botón Tomada.
          </p>
          <WatchMock screen="alarm" className="w-64 tablet:w-80" />
        </section>

        <section
          id="panel"
          aria-labelledby="panel-title"
          className={cn(wrap, 'flex scroll-mt-4 flex-col gap-10 py-16 desktop:py-24')}
        >
          <div className="flex flex-col gap-4">
            <SectionTitle id="panel-title">En el panel, de un vistazo: ¿se lo tomó?</SectionTitle>
            <p className="max-w-measure text-lg text-text-muted">
              Cuántas dosis van, cuál es la próxima y cada una en su franja, con su estado. Si hizo
              falta, la marcas desde aquí.
            </p>
          </div>
          <p className="sr-only">
            Ejemplo del panel: 5 de 8 dosis tomadas hoy, la próxima a las 14:00, y la franja de la
            mañana con Losartán y Metformina tomadas.
          </p>
          <PanelSample />
        </section>

        <section
          id="privacidad"
          aria-labelledby="privacidad-title"
          className={cn(wrap, 'flex scroll-mt-4 flex-col gap-10 py-16 desktop:py-24')}
        >
          <div className="flex flex-col gap-4">
            <SectionTitle id="privacidad-title">Datos de salud, tratados con cuidado.</SectionTitle>
            <p className="max-w-measure text-lg text-text-muted">
              Seguimos la Ley 1581 de 2012 de protección de datos personales de Colombia. Los datos
              viajan y se guardan cifrados.
            </p>
          </div>
          <ul className="grid grid-cols-1 gap-8 desktop:grid-cols-3">
            {privacy.map((item) => (
              <li key={item.title} className="flex gap-4">
                <Plate icon={item.icon} />
                <span className="flex flex-col gap-1">
                  <span className="text-lg font-medium">{item.title}</span>
                  <span className="text-text-muted">{item.body}</span>
                </span>
              </li>
            ))}
          </ul>
        </section>

        <section
          id="preguntas"
          aria-labelledby="preguntas-title"
          className={cn(wrap, 'flex scroll-mt-4 flex-col gap-8 py-16 desktop:py-24')}
        >
          <SectionTitle id="preguntas-title">Preguntas frecuentes</SectionTitle>
          <div className="max-w-3xl divide-y divide-border border-y border-border">
            {faqs.map((faq) => (
              <details key={faq.q} className="group">
                <summary className="flex min-h-touch cursor-pointer list-none items-center justify-between gap-4 py-4 text-lg font-medium [&::-webkit-details-marker]:hidden">
                  {faq.q}
                  <Icon
                    name="forward"
                    className="text-text-muted transition-transform group-open:rotate-90 motion-reduce:transition-none"
                  />
                </summary>
                <p className="max-w-measure pb-5 text-text-muted">{faq.a}</p>
              </details>
            ))}
          </div>
        </section>

        <section aria-labelledby="cta-title" className="px-2 pb-8 tablet:px-4">
          <div className="panel panel-grain panel-complete mx-auto flex max-w-7xl flex-col items-start gap-6 rounded-lg px-6 py-16 tablet:px-10 desktop:items-center desktop:px-16 desktop:text-center">
            <h2
              id="cta-title"
              className="text-2xl font-medium tracking-display text-balance desktop:text-3xl"
            >
              Empieza hoy: la primera alerta suena a su hora.
            </h2>
            <div className="flex w-full flex-col gap-3 tablet:w-auto tablet:flex-row">
              <Link to="/registro" className={buttonClass('primary')}>
                Crear cuenta
              </Link>
              <Link to="/login" className={buttonClass('secondary', 'min-h-control')}>
                Ya tengo cuenta
              </Link>
            </div>
          </div>
        </section>
      </main>

      <footer
        className={cn(
          wrap,
          'flex flex-col gap-2 py-8 text-caption text-text-muted tablet:flex-row tablet:justify-between',
        )}
      >
        <p>Recuerda · recordatorios de medicamentos en el reloj.</p>
        <p>Datos tratados según la Ley 1581 de 2012.</p>
      </footer>
    </div>
  );
}
