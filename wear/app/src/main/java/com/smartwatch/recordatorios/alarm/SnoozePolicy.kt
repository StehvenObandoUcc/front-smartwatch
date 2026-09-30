package com.smartwatch.recordatorios.alarm

import java.time.Duration

/** Posponer: 10 minutos, como máximo 3 veces por dosis. */
object SnoozePolicy {
    private const val DELAY_MINUTES = 10L
    val DELAY: Duration = Duration.ofMinutes(DELAY_MINUTES)
    const val MAX_SNOOZES = 3

    fun canSnooze(snoozeCount: Int): Boolean = snoozeCount < MAX_SNOOZES

    fun nextAlarmAt(nowMillis: Long): Long = nowMillis + DELAY.toMillis()
}
