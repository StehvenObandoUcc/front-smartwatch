import { useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';

import {
  createMedication,
  createSchedule,
  listSchedules,
  updateMedication,
  updateSchedule,
  type Medication,
  type Schedule,
  getListMedicationsQueryKey,
  useArchiveMedication,
  useListMedications,
  useListSchedules,
} from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { Plate } from '../components/atoms/Plate/Plate';
import { EmptyState } from '../components/molecules/EmptyState';
import { ErrorRetry } from '../components/molecules/ErrorRetry';
import { Skeleton } from '../components/atoms/Skeleton/Skeleton';
import { Text } from '../components/atoms/Text/Text';
import { MedicationForm } from '../features/medications/MedicationForm';
import { toFormValues, toPayload, type MedicationFormValues } from '../features/medications/schema';
import { scheduleText } from '../features/medications/scheduleText';
import { usePatient } from '../features/patients/PatientGate';
import { errorMessage } from '../lib/errors';

export function MedicationsPage() {
  const { patientId, timezone } = usePatient();
  const queryClient = useQueryClient();
  const [adding, setAdding] = useState(false);
  const [editing, setEditing] = useState<{
    medication: Medication;
    schedule: Schedule | null;
  } | null>(null);
  const [loadingEdit, setLoadingEdit] = useState<string>();
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string>();
  const medications = useListMedications(patientId);
  const archive = useArchiveMedication({
    mutation: {
      onSuccess: () =>
        queryClient.invalidateQueries({ queryKey: getListMedicationsQueryKey(patientId) }),
    },
  });

  async function startEdit(medication: Medication) {
    setLoadingEdit(medication.id);
    setSaveError(undefined);
    try {
      // El formulario maneja un horario; si hay varios se edita el primero.
      const { items } = await listSchedules(patientId, medication.id);
      setEditing({ medication, schedule: items[0] ?? null });
    } catch (error) {
      setSaveError(errorMessage(error));
    } finally {
      setLoadingEdit(undefined);
    }
  }

  async function save(values: MedicationFormValues) {
    const { medication, schedule } = toPayload(values);
    setSaving(true);
    setSaveError(undefined);
    try {
      if (editing) {
        const { id } = editing.medication;
        await updateMedication(patientId, id, medication);
        if (editing.schedule) await updateSchedule(patientId, id, editing.schedule.id, schedule);
        else await createSchedule(patientId, id, schedule);
      } else {
        const created = await createMedication(patientId, medication);
        await createSchedule(patientId, created.id, schedule);
      }
      await queryClient.invalidateQueries();
      setAdding(false);
      setEditing(null);
    } catch (error) {
      setSaveError(errorMessage(error));
    } finally {
      setSaving(false);
    }
  }

  if (adding || editing) {
    return (
      <MedicationForm
        timezone={timezone}
        initial={editing ? toFormValues(editing.medication, editing.schedule) : undefined}
        submitting={saving}
        error={saveError}
        onSubmit={(values) => void save(values)}
        onCancel={() => {
          setAdding(false);
          setEditing(null);
        }}
      />
    );
  }

  return (
    <section className="flex flex-col gap-4" aria-labelledby="meds-title">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <Text id="meds-title" variant="subtitle" as="h2">
          Medicamentos activos
        </Text>
        <Button icon="add" onClick={() => setAdding(true)}>
          Agregar medicamento
        </Button>
      </div>

      {medications.isPending && <Skeleton label="Cargando medicamentos" />}
      {medications.isError && (
        <ErrorRetry
          message={errorMessage(medications.error)}
          onRetry={() => void medications.refetch()}
        />
      )}
      {medications.data?.items.length === 0 && (
        <EmptyState
          icon="medications"
          title="Aún no hay medicamentos"
          body="Agrega el primero con su horario y sonará en el reloj."
          action={
            <Button icon="add" onClick={() => setAdding(true)}>
              Agregar medicamento
            </Button>
          }
        />
      )}
      <ul className="flex flex-col gap-3">
        {medications.data?.items.map((med) => (
          <MedicationRow
            key={med.id}
            patientId={patientId}
            medication={med}
            loadingEdit={loadingEdit === med.id}
            archiving={archive.isPending && archive.variables.medicationId === med.id}
            onEdit={() => void startEdit(med)}
            onArchive={() => archive.mutate({ patientId, medicationId: med.id })}
          />
        ))}
      </ul>
    </section>
  );
}

type RowProps = {
  patientId: string;
  medication: Medication;
  loadingEdit: boolean;
  archiving: boolean;
  onEdit: () => void;
  onArchive: () => void;
};

/**
 * Un medicamento con su horario a la vista («08:00 · 20:00 — todos los días»). Quitar (archivar) pide
 * confirmación: un toque por error en el móvil haría que deje de sonar en el reloj.
 */
function MedicationRow({
  patientId,
  medication,
  loadingEdit,
  archiving,
  onEdit,
  onArchive,
}: RowProps) {
  const [confirming, setConfirming] = useState(false);
  const schedules = useListSchedules(patientId, medication.id);
  const schedule = schedules.data?.items[0];
  return (
    <li className="card flex flex-wrap items-center justify-between gap-4 p-4">
      <div className="flex min-w-0 items-center gap-3">
        <Plate icon="medications" hex={medication.color} size="lg" />
        <div className="min-w-0">
          <Text variant="subtitle" as="p">
            {medication.name}
          </Text>
          <Text tone="muted">
            {schedule ? `${medication.dosage} · ${scheduleText(schedule)}` : medication.dosage}
          </Text>
        </div>
      </div>
      {confirming ? (
        <div
          role="group"
          aria-label={`Quitar ${medication.name}`}
          className="flex w-full flex-wrap items-center gap-2"
        >
          <Text className="w-full font-bold">
            ¿Quitar {medication.name}? Dejará de sonar en el reloj.
          </Text>
          <Button variant="secondary" icon="back" onClick={() => setConfirming(false)}>
            No
          </Button>
          <Button variant="danger" icon="remove" loading={archiving} onClick={onArchive}>
            Sí, quitar
          </Button>
        </div>
      ) : (
        <div className="flex gap-2">
          <Button
            variant="secondary"
            icon="edit"
            aria-label={`Editar ${medication.name}`}
            loading={loadingEdit}
            onClick={onEdit}
          >
            Editar
          </Button>
          <Button
            variant="ghost"
            icon="remove"
            aria-label={`Quitar ${medication.name}`}
            onClick={() => setConfirming(true)}
          >
            Quitar
          </Button>
        </div>
      )}
    </li>
  );
}
