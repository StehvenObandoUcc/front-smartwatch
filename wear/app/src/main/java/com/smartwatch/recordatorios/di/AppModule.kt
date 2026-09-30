package com.smartwatch.recordatorios.di

import android.app.AlarmManager
import android.content.Context
import androidx.room.Room
import com.smartwatch.recordatorios.alarm.FullScreenIntentPolicy
import com.smartwatch.recordatorios.alarm.SystemFullScreenIntentPolicy
import com.smartwatch.recordatorios.data.local.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.time.Clock
import javax.inject.Qualifier
import javax.inject.Singleton

/** Ámbito que sobrevive a receivers y pantallas (trabajo corto tras un broadcast). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmBindings {
    @Binds
    abstract fun fullScreenIntentPolicy(impl: SystemFullScreenIntentPolicy): FullScreenIntentPolicy
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun database(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME).build()

    @Provides
    fun alarmManager(
        @ApplicationContext context: Context,
    ): AlarmManager = context.getSystemService(AlarmManager::class.java)

    @Provides
    @Singleton
    fun clock(): Clock = Clock.systemUTC()

    @Provides
    @Singleton
    @ApplicationScope
    fun applicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
