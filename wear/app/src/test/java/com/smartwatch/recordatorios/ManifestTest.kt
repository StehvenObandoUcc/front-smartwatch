package com.smartwatch.recordatorios

import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.alarm.BootReceiver
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManifestTest {
    private val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    private val pm = context.packageManager

    @Test
    fun `la app es independiente (standalone)`() {
        val info = pm.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        assertThat(info.metaData.getBoolean("com.google.android.wearable.standalone")).isTrue()
    }

    @Test
    fun `declara los permisos de alarmas exactas y arranque`() {
        val permissions =
            pm.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions!!.toList()
        assertThat(permissions).containsAtLeast(
            "android.permission.SCHEDULE_EXACT_ALARM",
            "android.permission.RECEIVE_BOOT_COMPLETED",
            "android.permission.POST_NOTIFICATIONS",
            "android.permission.USE_FULL_SCREEN_INTENT",
        )
    }

    @Test
    fun `BootReceiver escucha arranque, actualizacion y cambio de permiso`() {
        listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
        ).forEach { action ->
            val receivers = pm.queryBroadcastReceivers(Intent(action).setPackage(context.packageName), 0)
            assertThat(receivers.map { it.activityInfo.name }).contains(BootReceiver::class.java.name)
        }
    }
}
