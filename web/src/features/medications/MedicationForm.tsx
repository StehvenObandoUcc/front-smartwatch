import { useTranslation } from 'react-i18next';
import { zodResolver } from '@hookform/resolvers/zod';
import { useFieldArray, useForm, useWatch } from 'react-hook-form';

import { Button } from '../../components/atoms/Button/Button';
import { Icon } from '../../components/atoms/Icon/Icon';
import { ColorDot } from '../../components/atoms/ColorDot/ColorDot';
import { Input } from '../../components/atoms/Input/Input';
import { Text } from '../../components/atoms/Text/Text';
import { FormField } from '../../components/molecules/FormField';
import { medicationColors, medicationHex } from '../../design/tokens';
import { cn } from '../../lib/cn';
import { localDate } from '../../lib/dates';
import { medicationFormSchema, type MedicationFormValues } from './schema';

// Lunes a domingo, como en el contrato (1–7); la palabra corta está en medications.day.*.
const weekdays = [1, 2, 3, 4, 5, 6, 7] as const;

type Props = {
  timezone: string;
  initial?: MedicationFormValues;
  submitting: boolean;
  error?: string | undefined;
  onSubmit: (values: MedicationFormValues) => void;
  onCancel: () => void;
};

export function MedicationForm({
  timezone,
  initial,
  submitting,
  error,
  onSubmit,
  onCancel,
}: Props) {
  const { t } = useTranslation();
  const { register, handleSubmit, control, formState, setValue } = useForm<MedicationFormValues>({
    resolver: zodResolver(medicationFormSchema),
    defaultValues: initial ?? {
      name: '',
      dosage: '',
      instructions: '',
      color: medicationHex.blue,
      times: [{ value: '08:00' }],
      daysOfWeek: ['1', '2', '3', '4', '5', '6', '7'],
      startDate: localDate(new Date(), timezone),
      endDate: '',
    },
  });
  const times = useFieldArray({ control, name: 'times' });
  const { errors } = formState;
  const selectedColor = useWatch({ control, name: 'color' });
  const isCustomColor = !Object.values(medicationHex).includes(selectedColor.toLowerCase());

  return (
    <form noValidate onSubmit={handleSubmit(onSubmit)} className="flex max-w-xl flex-col gap-4">
      <FormField label={t('medications.form.name')} error={errors.name?.message}>
        {(p) => <Input {...p} {...register('name')} />}
      </FormField>
      <FormField
        label={t('medications.form.dosage')}
        hint={t('medications.form.dosageHint')}
        error={errors.dosage?.message}
      >
        {(p) => <Input {...p} {...register('dosage')} />}
      </FormField>
      <FormField label={t('medications.form.instructions')} error={errors.instructions?.message}>
        {(p) => <Input {...p} {...register('instructions')} />}
      </FormField>

      <fieldset className="flex flex-col gap-2">
        <legend className="text-body font-bold">{t('medications.form.color')}</legend>
        <div className="flex flex-wrap gap-2">
          {medicationColors.map((color) => (
            <label key={color} className="group choice">
              <input
                type="radio"
                value={medicationHex[color]}
                className="sr-only"
                {...register('color')}
              />
              <ColorDot color={color} />
              {t(`medications.colors.${color}`)}
              <Icon name="taken" className="hidden size-5 group-has-checked:block" />
            </label>
          ))}
          <label
            className={cn(
              'flex min-h-touch items-center gap-2 rounded-md border-2 px-3 has-focus-visible:outline-3 has-focus-visible:outline-offset-2 has-focus-visible:outline-focus',
              isCustomColor ? 'border-primary bg-primary text-on-primary' : 'border-border-strong',
            )}
          >
            <input
              type="color"
              aria-label={t('medications.form.customColorLabel')}
              value={selectedColor}
              onChange={(event) =>
                setValue('color', event.target.value, { shouldValidate: true, shouldDirty: true })
              }
              className="size-6 cursor-pointer"
            />
            {t('medications.form.customColor')}
          </label>
        </div>
        {errors.color?.message && (
          <p role="alert" className="text-caption text-danger">
            {errors.color.message}
          </p>
        )}
      </fieldset>

      <fieldset className="flex flex-col gap-2">
        <legend className="text-body font-bold">{t('medications.form.times')}</legend>
        {times.fields.map((field, index) => (
          <div key={field.id} className="flex items-center gap-2">
            <Input
              type="time"
              aria-label={t('medications.form.time', { n: index + 1 })}
              invalid={Boolean(errors.times?.[index])}
              {...register(`times.${index}.value`)}
            />
            {times.fields.length > 1 && (
              <Button
                variant="ghost"
                aria-label={t('medications.form.removeTime', { n: index + 1 })}
                onClick={() => times.remove(index)}
              >
                {t('medications.remove')}
              </Button>
            )}
          </div>
        ))}
        {(errors.times?.message ?? errors.times?.root?.message) && (
          <p role="alert" className="text-caption text-danger">
            {errors.times?.message ?? errors.times?.root?.message}
          </p>
        )}
        <div>
          <Button variant="secondary" onClick={() => times.append({ value: '12:00' })}>
            {t('medications.form.addTime')}
          </Button>
        </div>
      </fieldset>

      <fieldset className="flex flex-col gap-2">
        <legend className="text-body font-bold">{t('medications.form.days')}</legend>
        <div className="flex flex-wrap gap-2">
          {weekdays.map((day) => (
            <label key={day} className="choice min-w-touch justify-center">
              <input type="checkbox" value={day} className="sr-only" {...register('daysOfWeek')} />
              {t(`medications.day.${day}`)}
            </label>
          ))}
        </div>
        {errors.daysOfWeek?.message && (
          <p role="alert" className="text-caption text-danger">
            {errors.daysOfWeek.message}
          </p>
        )}
      </fieldset>

      <div className="flex flex-wrap gap-4">
        <FormField label={t('medications.form.from')} error={errors.startDate?.message}>
          {(p) => <Input type="date" {...p} {...register('startDate')} />}
        </FormField>
        <FormField label={t('medications.form.until')} error={errors.endDate?.message}>
          {(p) => <Input type="date" {...p} {...register('endDate')} />}
        </FormField>
      </div>

      {error && (
        <Text tone="danger" as="p">
          {error}
        </Text>
      )}
      <div className="flex flex-wrap gap-2">
        <Button type="submit" loading={submitting}>
          {t('common.save')}
        </Button>
        <Button variant="ghost" onClick={onCancel}>
          {t('common.cancel')}
        </Button>
      </div>
    </form>
  );
}
