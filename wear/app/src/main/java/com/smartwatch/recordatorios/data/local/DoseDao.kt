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

    @Query("SELECT COUNT(*) FROM doses")
    suspend fun count(): Int

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

    @Query("SELECT * FROM dose_events WHERE syncState = 'PENDING' ORDER BY occurredAt")
    suspend fun pending(): List<DoseEventEntity>

    @Query("SELECT * FROM dose_events WHERE doseId = :doseId ORDER BY occurredAt")
    suspend fun forDose(doseId: String): List<DoseEventEntity>
}
