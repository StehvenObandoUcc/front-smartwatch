package com.smartwatch.recordatorios.di

import com.smartwatch.recordatorios.api.apis.DevicesApi
import com.smartwatch.recordatorios.api.apis.DosesApi
import com.smartwatch.recordatorios.api.apis.PlanApi
import com.smartwatch.recordatorios.data.remote.Api
import com.smartwatch.recordatorios.sync.SyncScheduler
import com.smartwatch.recordatorios.sync.UploadTrigger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    fun devicesApi(api: Api): DevicesApi = api.pairing

    @Provides
    fun planApi(api: Api): PlanApi = api.plan

    @Provides
    fun dosesApi(api: Api): DosesApi = api.doses
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncBindings {
    @Binds
    abstract fun uploadTrigger(impl: SyncScheduler): UploadTrigger
}
