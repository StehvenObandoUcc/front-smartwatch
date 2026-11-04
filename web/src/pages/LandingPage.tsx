import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
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
  { word: 'morning', panel: 'panel-morning' },
  { word: 'afternoon', panel: 'panel-afternoon' },
  { word: 'night', panel: 'panel-night' },
] as const;

// Los textos viven en i18n (landing.*); aquí solo el orden y el pictograma de cada uno.
const facts = [1, 2, 3] as const;

const steps: { n: 1 | 2 | 3; icon: IconName }[] = [
  { n: 1, icon: 'medications' },
  { n: 2, icon: 'link' },
  { n: 3, icon: 'notifications' },
];

const watchPoints = [1, 2, 3, 4] as const;

const privacy: { n: 1 | 2 | 3; icon: IconName }[] = [
  { n: 1, icon: 'consent' },
  { n: 2, icon: 'patient' },
  { n: 3, icon: 'remove' },
];

const faqs = [1, 2, 3, 4, 5] as const;

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
  const { t } = useTranslation();
  return (
    <div aria-hidden="true" className="grid grid-cols-1 gap-4 tablet:grid-cols-2">
      <div className="panel panel-afternoon flex flex-col items-center gap-4 rounded-lg p-6 shadow-raised">
        <Ring value={5} total={8}>
          <span className="text-2xl leading-none font-medium">{t('landing.sampleCount')}</span>
          <span className="mt-1 text-caption text-on-panel-muted">{t('landing.sampleToday')}</span>
        </Ring>
        <p className="text-lg">
          <span className="text-on-panel-muted">{t('landing.sampleNext')}</span>{' '}
          <span className="font-medium">14:00 · Losartán</span>
        </p>
      </div>
      <div className="raised flex flex-col gap-2 p-5">
        <SignBar
          icon="morning"
          tone="sun"
          title={t('landing.sampleMorning')}
          meta={t('landing.sampleMorningHours')}
          as="p"
        />
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
                  {t('watch.taken')}
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
  const { t } = useTranslation();
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
        {t('common.skipToContent')}
      </a>
      <header className={cn(wrap, 'flex min-h-20 flex-wrap items-center gap-x-6 gap-y-2 py-3')}>
        <Link to="/" className="flex items-center gap-3 text-lg font-medium tracking-tight">
          <Plate icon="medications" tone="ink" size="sm" />
          Recuerda
        </Link>
        <nav aria-label={t('landing.sections')} className="hidden gap-6 desktop:flex">
          {[
            ['#como-funciona', t('landing.navHow')],
            ['#reloj', t('landing.navWatch')],
            ['#panel', t('landing.navPanel')],
            ['#privacidad', t('landing.navPrivacy')],
            ['#preguntas', t('landing.navFaq')],
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
            {t('landing.signIn')}
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
                {t('landing.heroBefore')}{' '}
                <span
                  key={part.word}
                  aria-live={cycle.still || cycle.paused ? 'off' : 'polite'}
                  className="inline-block underline decoration-2 underline-offset-8 motion-safe:animate-word"
                >
                  {t(`landing.${part.word}`)}
                </span>
                .
              </h1>
              <p className="max-w-measure text-lg text-on-panel-muted">{t('landing.heroBody')}</p>
              <div className="flex w-full flex-col gap-3 tablet:w-auto tablet:flex-row">
                <Link to="/registro" className={buttonClass('primary')}>
                  {t('auth.createAccount')}
                </Link>
                <Link to="/login" className={buttonClass('secondary', 'min-h-control')}>
                  {t('landing.haveAccount')}
                </Link>
              </div>
              {!cycle.still && (
                <button
                  type="button"
                  onClick={cycle.toggle}
                  className={buttonClass('quiet', '-ml-3 text-caption')}
                >
                  <Icon name={cycle.paused ? 'play' : 'pause'} className="size-5" />
                  {cycle.paused ? t('landing.resume') : t('landing.pause')}
                </button>
              )}
            </div>
            <p className="sr-only">{t('landing.heroWatchAlt')}</p>
            <WatchMock className="w-60 tablet:w-72" />
          </div>
        </section>

        <section aria-label={t('landing.inShort')} className={cn(wrap, 'py-12')}>
          <ul className="grid grid-cols-1 divide-y divide-border tablet:grid-cols-3 tablet:divide-x tablet:divide-y-0">
            {facts.map((n) => (
              <li key={n} className="flex flex-col gap-1 py-5 tablet:px-6 tablet:first:pl-0">
                <span className="text-2xl font-medium tracking-display">
                  {t(`landing.fact${n}Big`)}
                </span>
                <span className="text-text-muted">{t(`landing.fact${n}Small`)}</span>
              </li>
            ))}
          </ul>
        </section>

        <section
          id="como-funciona"
          aria-labelledby="pasos-title"
          className={cn(wrap, 'flex scroll-mt-4 flex-col gap-10 py-16 desktop:py-24')}
        >
          <SectionTitle id="pasos-title">{t('landing.stepsTitle')}</SectionTitle>
          <ol className="grid grid-cols-1 gap-8 desktop:grid-cols-3">
            {steps.map((step, i) => (
              <li key={step.n} className="flex flex-col gap-3 border-t border-border pt-6">
                <span className="flex items-center gap-3">
                  <span className="inline-grid size-plate-md place-items-center rounded-md bg-primary text-lg font-medium text-on-primary">
                    {i + 1}
                  </span>
                  <Icon name={step.icon} className="text-accent" />
                </span>
                <h3 className="text-xl font-medium tracking-tight">
                  {t(`landing.step${step.n}Title`)}
                </h3>
                <p className="max-w-measure text-text-muted">{t(`landing.step${step.n}Body`)}</p>
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
            <SectionTitle id="reloj-title">{t('landing.watchTitle')}</SectionTitle>
            <ul className="flex flex-col gap-4">
              {watchPoints.map((n) => (
                <li key={n} className="flex gap-3">
                  <span className="inline-grid size-8 shrink-0 place-items-center rounded-full bg-state-taken text-on-state">
                    <Icon name="taken" className="size-5" />
                  </span>
                  <span className="text-lg">{t(`landing.watch${n}`)}</span>
                </li>
              ))}
            </ul>
          </div>
          <p className="sr-only">{t('landing.watchAlarmAlt')}</p>
          <WatchMock screen="alarm" className="w-64 tablet:w-80" />
        </section>

        <section
          id="panel"
          aria-labelledby="panel-title"
          className={cn(wrap, 'flex scroll-mt-4 flex-col gap-10 py-16 desktop:py-24')}
        >
          <div className="flex flex-col gap-4">
            <SectionTitle id="panel-title">{t('landing.panelTitle')}</SectionTitle>
            <p className="max-w-measure text-lg text-text-muted">{t('landing.panelBody')}</p>
          </div>
          <p className="sr-only">{t('landing.panelAlt')}</p>
          <PanelSample />
        </section>

        <section
          id="privacidad"
          aria-labelledby="privacidad-title"
          className={cn(wrap, 'flex scroll-mt-4 flex-col gap-10 py-16 desktop:py-24')}
        >
          <div className="flex flex-col gap-4">
            <SectionTitle id="privacidad-title">{t('landing.privacyTitle')}</SectionTitle>
            <p className="max-w-measure text-lg text-text-muted">{t('landing.privacyBody')}</p>
          </div>
          <ul className="grid grid-cols-1 gap-8 desktop:grid-cols-3">
            {privacy.map((item) => (
              <li key={item.n} className="flex gap-4">
                <Plate icon={item.icon} />
                <span className="flex flex-col gap-1">
                  <span className="text-lg font-medium">{t(`landing.privacy${item.n}Title`)}</span>
                  <span className="text-text-muted">{t(`landing.privacy${item.n}Body`)}</span>
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
          <SectionTitle id="preguntas-title">{t('landing.faqTitle')}</SectionTitle>
          <div className="max-w-3xl divide-y divide-border border-y border-border">
            {faqs.map((n) => (
              <details key={n} className="group">
                <summary className="flex min-h-touch cursor-pointer list-none items-center justify-between gap-4 py-4 text-lg font-medium [&::-webkit-details-marker]:hidden">
                  {t(`landing.faq${n}Q`)}
                  <Icon
                    name="forward"
                    className="text-text-muted transition-transform group-open:rotate-90 motion-reduce:transition-none"
                  />
                </summary>
                <p className="max-w-measure pb-5 text-text-muted">{t(`landing.faq${n}A`)}</p>
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
              {t('landing.ctaTitle')}
            </h2>
            <div className="flex w-full flex-col gap-3 tablet:w-auto tablet:flex-row">
              <Link to="/registro" className={buttonClass('primary')}>
                {t('auth.createAccount')}
              </Link>
              <Link to="/login" className={buttonClass('secondary', 'min-h-control')}>
                {t('landing.haveAccount')}
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
        <p>{t('landing.footer')}</p>
        <p>{t('common.law')}</p>
      </footer>
    </div>
  );
}
