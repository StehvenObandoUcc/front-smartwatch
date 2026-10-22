package com.smartwatch.recordatorios.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

// 2 → 3 solo añade `local_medications`: migración automática para no borrar los datos del reloj.
@Database(
    entities = [DoseEntity::class, DoseEventEntity::class, LocalMedicationEntity::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 2, to = 3)],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doseDao(): DoseDao

    abstract fun doseEventDao(): DoseEventDao

    abstract fun localMedicationDao(): LocalMedicationDao

    companion object {
        const val NAME = "recordatorios.db"
    }
}
