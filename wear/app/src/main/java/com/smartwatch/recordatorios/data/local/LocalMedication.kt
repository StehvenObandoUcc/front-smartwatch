package com.smartwatch.recordatorios.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Medicamento creado en el propio reloj (modo sin vincular). Suena todos los días a [times]; sus
 * dosis se generan en `doses` con el prefijo `local:` y no se suben al backend.
 */
@Entity(tableName = "local_medications")
data class LocalMedicationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val doseLabel: String,
    val colorKey: String,
    /** Horas locales del reloj, "HH:mm" separadas por coma. */
    val times: String,
)

@Dao
interface LocalMedicationDao {
    @Query("SELECT * FROM local_medications ORDER BY name")
    fun observeAll(): Flow<List<LocalMedicationEntity>>

    @Query("SELECT * FROM local_medications")
    suspend fun all(): List<LocalMedicationEntity>

    @Upsert
    suspend fun upsert(medication: LocalMedicationEntity)

    @Query("DELETE FROM local_medications WHERE id = :id")
    suspend fun delete(id: String)
}
