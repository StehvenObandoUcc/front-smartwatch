package com.smartwatch.recordatorios.alarm

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Si la alerta puede abrirse a pantalla completa. Es opcional: sin ella basta la notificación. */
fun interface FullScreenIntentPolicy {
    fun canUseFullScreenIntent(): Boolean
}

/** Android 14+ puede negar el full-screen intent; antes siempre estaba permitido. */
class SystemFullScreenIntentPolicy
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : FullScreenIntentPolicy {
        override fun canUseFullScreenIntent(): Boolean =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                context.getSystemService(NotificationManager::class.java).canUseFullScreenIntent()
    }
