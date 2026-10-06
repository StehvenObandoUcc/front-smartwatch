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
} from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { ColorDot } from '../components/atoms/ColorDot/ColorDot';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { MedicationForm } from '../features/medications/MedicationForm';
import { toFormValues, toPayload, type MedicationFormValues } from '../features/medications/schema';
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
      <div className="flex items-center justify-between gap-4">
        <Text id="meds-title" variant="subtitle" as="h2">
          Medicamentos activos
        </Text>
        <Button onClick={() => setAdding(true)}>Añadir medicamento</Button>
      </div>

      {medications.isPending && <Spinner label="Cargando medicamentos" />}
      {medications.isError && (
        <div role="alert" className="flex flex-col items-start gap-2">
          <Text tone="danger">{errorMessage(medications.error)}</Text>
          <Button variant="secondary" onClick={() => void medications.refetch()}>
            Reintentar
          </Button>
        </div>
      )}
      {medications.data?.items.length === 0 && (
        <Text tone="muted">
          Aún no hay medicamentos. Añade el primero para que suene en el reloj.
        </Text>
      )}
      <ul className="flex flex-col gap-2">
        {medications.data?.items.map((med) => (
          <li key={med.id} className="card flex flex-wrap items-center justify-between gap-4 p-4">
            <div className="flex items-center gap-3">
              <ColorDot hex={med.color} />
              <div>
                <Text variant="subtitle" as="p">
                  {med.name}
                </Text>
                <Text tone="muted">{med.dosage}</Text>
              </div>
            </div>
            <div className="flex gap-1">
              <Button
                variant="ghost"
                aria-label={`Editar ${med.name}`}
                loading={loadingEdit === med.id}
                onClick={() => void startEdit(med)}
              >
                Editar
              </Button>
              <Button
                variant="ghost"
                aria-label={`Archivar ${med.name}`}
                loading={archive.isPending && archive.variables.medicationId === med.id}
                onClick={() => archive.mutate({ patientId, medicationId: med.id })}
              >
                Archivar
              </Button>
            </div>
          </li>
        ))}
      </ul>
    </section>
  );
}
