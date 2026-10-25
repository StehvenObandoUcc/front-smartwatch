import type { ReactNode } from 'react';
import { Link } from 'react-router';

import { Badge } from '../components/atoms/Badge/Badge';
import { buttonClass } from '../components/atoms/Button/buttonClass';
import { Icon, type IconName } from '../components/atoms/Icon/Icon';
import { Plate } from '../components/atoms/Plate/Plate';
import { Ring } from '../components/atoms/Ring/Ring';
import { SignBar } from '../components/molecules/SignBar';
import { WatchMock } from '../components/molecules/WatchMock';
import { medicationColorClass, type MedicationColor } from '../design/tokens';
import { cn } from '../lib/cn';

// Las muestras de la interfaz son ilustraciones con datos de ejemplo; el lector oye su descripción.
const SAMPLE_WEEK = [100, 100, 50, 100, 100, 0, 100];
const WEEKDAYS = ['lun', 'mar', 'mié', 'jue', 'vie', 'sáb', 'dom'];

const wrap = 'mx-auto w-full max-w-content px-4 tablet:px-6';

/** Insignia de sección: una placa del sistema, pictograma y palabra. Solo en la landing. */
function Insignia({ icon, children }: { icon: IconName; children: ReactNode }) {
  return (
    <span className="inline-flex items-center gap-2 rounded-md bg-surface py-1 pr-3 pl-1 font-bold">
      <Plate icon={icon} size="sm" />
      {children}
    </span>
  );
}

/** Muestra de una dosis, como en el panel: placa del medicamento, nombre, hora y estado. */
function SampleDose({
  name,
  dosage,
  time,
  color,
  state,
}: {
  name: string;
  dosage: string;
  time: string;
  color: MedicationColor;
  state: 'taken' | 'due' | 'upcoming';
}) {
  const badge = {
    taken: { tone: 'success', text: 'Tomada' },
    due: { tone: 'warning', text: 'Pendiente' },
    upcoming: { tone: 'outline', text: 'Más tarde' },
  } as const;
  return (
    <li className="flex flex-wrap items-center gap-x-3 gap-y-2">
      <span
        className={cn(
          'inline-grid size-plate-md shrink-0 place-items-center rounded-md text-on-state',
          medicationColorClass[color],
        )}
      >
        <Icon name="medications" />
      </span>
      <div className="min-w-0 flex-1 basis-28">
        <p className="leading-tight font-extrabold">{name}</p>
        <p className="text-text-muted">{dosage}</p>
      </div>
      <span className="font-extrabold">{time}</span>
      <Badge tone={badge[state].tone} icon={state}>
        {badge[state].text}
      </Badge>
    </li>
  );
}

function Feature({
  icon,
  title,
  body,
  className,
  children,
}: {
  icon: IconName;
  title: string;
  body: string;
  className?: string;
  children: ReactNode;
}) {
  return (
    <article className={cn('raised flex flex-col gap-5 p-6', className)}>
      <div className="flex items-start gap-3">
        <Plate icon={icon} />
        <div className="min-w-0">
          <h3 className="text-xl leading-tight font-extrabold tracking-tight">{title}</h3>
          <p className="max-w-measure text-text-muted">{body}</p>
        </div>
      </div>
      <div aria-hidden="true" className="mt-auto">
        {children}
      </div>
    </article>
  );
}

const steps = [
  {
    icon: 'medications',
    title: 'Crea los medicamentos',
    body: 'Desde el panel, con su dosis, su color y sus horas.',
  },
  {
    icon: 'link',
    title: 'Vincula el reloj',
    body: 'El reloj muestra un código; lo escribes en Ajustes.',
  },
  {
    icon: 'notifications',
    title: 'Recibe los avisos',
    body: 'El reloj suena a su hora y tú ves aquí si se tomó.',
  },
] as const;

const patientPoints = [
  'Funciona sin teléfono: el reloj va por su cuenta.',
  'Sin internet también: guarda el plan de 7 días y avisa igual.',
  'Texto y botones grandes; el color del medicamento siempre con su nombre.',
  'Tomada, Posponer u Omitir con un toque, desde la muñeca.',
];

/** Página pública de `/` para quien no ha entrado: el producto se muestra a sí mismo. */
export function LandingPage() {
  return (
    <div className="min-h-dvh bg-bg">
      <a href="#contenido" className="sr-only focus:not-sr-only focus:absolute focus:p-3">
        Saltar al contenido
      </a>
      <header className={cn(wrap, 'flex min-h-20 flex-wrap items-center gap-x-6 gap-y-2 py-3')}>
        <Link to="/" className="flex items-center gap-2 text-xl font-extrabold tracking-tight">
          <Plate icon="medications" size="sm" />
          Recuerda
        </Link>
        <nav aria-label="Secciones" className="hidden gap-6 font-bold tablet:flex">
          <a href="#como-funciona" className="hover:underline">
            Cómo funciona
          </a>
          <a href="#paciente" className="hover:underline">
            Para el paciente
          </a>
        </nav>
        <div className="ml-auto flex items-center gap-2">
          <Link to="/login" className={buttonClass('ghost', 'px-3')}>
            Entrar
          </Link>
          <span className="hidden tablet:block">
            <Link to="/registro" className={buttonClass('primary')}>
              Crear cuenta
            </Link>
          </span>
        </div>
      </header>

      <main id="contenido">
        <section aria-labelledby="hero-title" className="motion-safe:animate-rise">
          <div className={cn(wrap, 'flex flex-col items-start gap-6 pt-8 pb-12 desktop:pt-16')}>
            <Insignia icon="watch">Reloj y panel</Insignia>
            <h1
              id="hero-title"
              className="text-2xl font-extrabold tracking-display text-balance tablet:text-3xl desktop:text-4xl"
            >
              ¿Se tomó hoy su medicamento?
              <span className="block text-primary">Ahora lo sabes de un vistazo.</span>
            </h1>
            <p className="max-w-measure text-lg text-text-muted">
              Recuerda avisa en el reloj a la hora de cada dosis y te muestra en el panel qué se
              tomó, qué falta y cómo va la semana.
            </p>
            <div className="flex w-full flex-col gap-3 tablet:w-auto tablet:flex-row">
              <Link to="/registro" className={buttonClass('primary')}>
                Crear cuenta
              </Link>
              <Link to="/login?next=/ajustes" className={buttonClass('secondary')}>
                <Icon name="watch" />
                Vincular un reloj
              </Link>
            </div>
          </div>
          <div className="bg-sign-stripes">
            <div
              className={cn(
                wrap,
                'flex flex-col items-center gap-8 py-12 desktop:flex-row desktop:justify-center desktop:gap-16',
              )}
            >
              <p className="sr-only">
                Ejemplo del panel: 2 de 3 dosis tomadas hoy y la próxima a las 13:00. Al lado, el
                reloj con el mismo anillo.
              </p>
              <div
                aria-hidden="true"
                className="raised flex w-full max-w-lg flex-col gap-5 p-5 text-text"
              >
                <div className="flex items-center gap-3">
                  <Plate icon="today" size="sm" />
                  <div>
                    <p className="text-lg leading-tight font-extrabold">Hoy</p>
                    <p className="text-text-muted">¿Se tomó hoy su medicamento?</p>
                  </div>
                </div>
                <div className="flex flex-col items-center gap-5 tablet:flex-row">
                  <Ring value={2} total={3}>
                    <span className="text-2xl font-extrabold">2</span>
                    <span className="text-caption text-text-muted">de 3 tomadas</span>
                  </Ring>
                  <ul className="rows w-full min-w-0 flex-1">
                    <SampleDose
                      name="Losartán"
                      dosage="50 mg"
                      time="08:00"
                      color="blue"
                      state="taken"
                    />
                    <SampleDose
                      name="Metformina"
                      dosage="850 mg"
                      time="13:00"
                      color="teal"
                      state="due"
                    />
                  </ul>
                </div>
              </div>
              <WatchMock className="w-60 desktop:w-72" />
            </div>
          </div>
        </section>

        <section
          aria-labelledby="funciones-title"
          className={cn(wrap, 'flex flex-col gap-8 py-16')}
        >
          <div className="flex flex-col items-start gap-3">
            <Insignia icon="more">Qué hace</Insignia>
            <h2
              id="funciones-title"
              className="text-2xl font-extrabold tracking-display text-balance desktop:text-3xl"
            >
              Todo el día, en un solo sitio.
            </h2>
          </div>
          <div className="grid grid-cols-1 gap-5 desktop:grid-cols-12">
            <Feature
              icon="medications"
              title="El pastillero del día"
              body="Cada dosis en su franja, con su color, su nombre y su estado."
              className="desktop:col-span-7"
            >
              <div className="grid grid-cols-1 gap-4 tablet:grid-cols-2">
                <div className="flex flex-col gap-3">
                  <SignBar icon="morning" tone="sun" title="Mañana" as="p" />
                  <ul className="rows">
                    <SampleDose
                      name="Losartán"
                      dosage="50 mg"
                      time="08:00"
                      color="blue"
                      state="taken"
                    />
                  </ul>
                </div>
                <div className="flex flex-col gap-3">
                  <SignBar icon="night" tone="night" title="Noche" as="p" />
                  <ul className="rows">
                    <SampleDose
                      name="Atorvastatina"
                      dosage="20 mg"
                      time="21:00"
                      color="pink"
                      state="upcoming"
                    />
                  </ul>
                </div>
              </div>
            </Feature>
            <Feature
              icon="watch"
              title="La alerta en el reloj"
              body="Suena y vibra a la hora; se responde con un toque."
              className="desktop:col-span-5"
            >
              <div className="mx-auto flex aspect-square w-56 max-w-full flex-col items-center justify-center gap-2 rounded-full bg-danger text-on-danger">
                <Icon name="medications" className="size-8" />
                <p className="text-xl font-extrabold">Metformina</p>
                <p>850 mg · 13:00</p>
                <span className="inline-flex min-h-touch items-center gap-1 rounded-full bg-on-danger px-4 font-bold text-danger">
                  <Icon name="taken" className="size-5" />
                  Tomada
                </span>
              </div>
            </Feature>
            <Feature
              icon="history"
              title="Cómo va la semana"
              body="El porcentaje de tomas y cómo terminó cada día."
              className="desktop:col-span-5"
            >
              <div className="flex h-36 items-end gap-2">
                {SAMPLE_WEEK.map((pct, i) => (
                  <div
                    key={WEEKDAYS[i]}
                    className="flex h-full min-w-0 flex-1 flex-col items-center justify-end gap-1"
                  >
                    <svg
                      viewBox="0 0 10 100"
                      preserveAspectRatio="none"
                      className="w-full flex-1 border-b-2 border-border"
                    >
                      <rect
                        x="0"
                        width="10"
                        y={100 - Math.max(pct, 2)}
                        height={Math.max(pct, 2)}
                        className="fill-primary"
                      />
                    </svg>
                    <Icon
                      name={pct === 100 ? 'taken' : 'missed'}
                      className={pct === 100 ? 'text-state-taken' : 'text-state-missed'}
                    />
                    <span className="text-caption text-text-muted">{WEEKDAYS[i]}</span>
                  </div>
                ))}
              </div>
            </Feature>
            <Feature
              icon="voice"
              title="Pregúntale al reloj"
              body="Con la voz: responde corto, en pantalla y en voz alta."
              className="desktop:col-span-7"
            >
              <div className="flex flex-col gap-3">
                <p className="ml-auto max-w-xs rounded-lg rounded-br-sm bg-primary px-4 py-3 text-on-primary">
                  ¿Ya me tomé la de la mañana?
                </p>
                <p className="mr-auto max-w-xs rounded-lg rounded-bl-sm bg-surface px-4 py-3">
                  Sí. Tomaste Losartán a las 8:05.
                </p>
              </div>
            </Feature>
          </div>
        </section>

        <section
          id="como-funciona"
          aria-labelledby="pasos-title"
          className={cn(wrap, 'flex scroll-mt-4 flex-col gap-8 py-16')}
        >
          <div className="flex flex-col items-start gap-3">
            <Insignia icon="forward">Cómo funciona</Insignia>
            <h2
              id="pasos-title"
              className="text-2xl font-extrabold tracking-display text-balance desktop:text-3xl"
            >
              Listo en tres pasos.
            </h2>
          </div>
          <ol className="grid grid-cols-1 gap-5 desktop:grid-cols-3">
            {steps.map((step, i) => (
              <li key={step.title} className="flex flex-col gap-3 border-t-2 border-border pt-5">
                <div className="flex items-center gap-3">
                  <span className="inline-grid size-plate-md place-items-center rounded-md bg-sign text-lg font-extrabold text-on-sign">
                    {i + 1}
                  </span>
                  <Icon name={step.icon} className="text-primary" />
                </div>
                <h3 className="text-xl font-extrabold tracking-tight">{step.title}</h3>
                <p className="text-text-muted">{step.body}</p>
                {step.icon === 'link' && (
                  <p className="self-start rounded-md bg-surface px-4 py-2 text-xl font-extrabold tracking-wide">
                    XXXX-XXXX
                  </p>
                )}
              </li>
            ))}
          </ol>
        </section>

        <section
          id="paciente"
          aria-labelledby="paciente-title"
          className={cn(
            wrap,
            'flex scroll-mt-4 flex-col items-center gap-10 py-16 desktop:flex-row desktop:justify-between',
          )}
        >
          <div className="flex w-full max-w-xl flex-col items-start gap-4">
            <Insignia icon="patient">Para el paciente</Insignia>
            <h2
              id="paciente-title"
              className="text-2xl font-extrabold tracking-display text-balance desktop:text-3xl"
            >
              El reloj hace el trabajo; el paciente solo toca «Tomada».
            </h2>
            <ul className="flex flex-col gap-3">
              {patientPoints.map((point) => (
                <li key={point} className="flex gap-3">
                  <span className="inline-grid size-8 shrink-0 place-items-center rounded-sm bg-state-taken text-on-state">
                    <Icon name="taken" className="size-5" />
                  </span>
                  <span className="text-lg">{point}</span>
                </li>
              ))}
            </ul>
          </div>
          <WatchMock />
        </section>

        <section aria-labelledby="cta-title" className="bg-sign-gradient text-on-sign">
          <div className={cn(wrap, 'flex flex-col items-start gap-5 py-16 desktop:items-center')}>
            <h2
              id="cta-title"
              className="text-2xl font-extrabold tracking-display text-balance desktop:text-center desktop:text-3xl"
            >
              Empieza hoy: la primera alerta suena a su hora.
            </h2>
            <Link to="/registro" className={buttonClass('inverse')}>
              Crear cuenta
            </Link>
          </div>
        </section>
      </main>

      <footer
        className={cn(
          wrap,
          'flex flex-col gap-2 py-8 text-text-muted tablet:flex-row tablet:justify-between',
        )}
      >
        <p>Recuerda · recordatorios de medicamentos en el reloj.</p>
        <p>Tratamos tus datos según la Ley 1581 de 2012 de protección de datos personales.</p>
      </footer>
    </div>
  );
}
