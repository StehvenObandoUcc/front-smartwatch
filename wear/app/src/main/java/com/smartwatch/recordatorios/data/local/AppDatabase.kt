package com.smartwatch.recordatorios.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DoseEntity::class, DoseEventEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doseDao(): DoseDao

    abstract fun doseEventDao(): DoseEventDao

    companion object {
        const val NAME = "recordatorios.db"
    }
}
