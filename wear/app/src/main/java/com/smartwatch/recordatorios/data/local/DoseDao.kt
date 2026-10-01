package com.smartwatch.recordatorios.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseDao {
    @Query("SELECT * FROM doses ORDER BY scheduledAt")
    fun observeAll(): Flow<List<DoseEntity>>

    @Query("SELECT * FROM doses WHERE id = :id")
    suspend fun get(id: String): DoseEntity?

    @Query("SELECT * FROM doses WHERE status = 'SCHEDULED' ORDER BY nextAlarmAt")
    suspend fun scheduled(): List<DoseEntity>

    @Query("SELECT * FROM doses")
    suspend fun all(): List<DoseEntity>

    @Query("DELETE FROM doses WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)

    @Query("DELETE FROM doses WHERE status != 'SCHEDULED' AND scheduledAt < :before")
    suspend fun deleteResolvedBefore(before: Long)

    @Upsert
    suspend fun upsert(doses: List<DoseEntity>)

    @Update
    suspend fun update(dose: DoseEntity)

    @Query("DELETE FROM doses")
    suspend fun deleteAll()
}

@Dao
interface DoseEventDao {
    @Insert
    suspend fun insert(event: DoseEventEntity)

    @Query(
        "SELECT * FROM dose_events WHERE syncState = 'PENDING' AND action != 'SNOOZED' " +
            "ORDER BY occurredAt LIMIT :limit",
    )
    suspend fun pending(limit: Int): List<DoseEventEntity>

    @Query("UPDATE dose_events SET syncState = 'CONFIRMED' WHERE eventId IN (:ids)")
    suspend fun markConfirmed(ids: List<String>)

    @Query("SELECT * FROM dose_events WHERE doseId = :doseId ORDER BY occurredAt")
    suspend fun forDose(doseId: String): List<DoseEventEntity>
}
