package com.misw.sportalarmist.data

import android.content.Context

/** ¿Hay al menos un partido con asistencia pendiente? Lo usan la pantalla de Partidos y la barra inferior. */
object PendingMatches {

    fun any(context: Context): Boolean {
        val attendanceStore = AttendanceStore(context)
        return MatchRepository(EnrollmentStore(context)).getAll()
            .any { attendanceStore.statusOf(it.id) == AttendanceStatus.PENDING }
    }
}
