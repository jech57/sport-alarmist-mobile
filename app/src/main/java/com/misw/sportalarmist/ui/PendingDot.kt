package com.misw.sportalarmist.ui

import android.app.Activity
import android.view.View
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.PendingMatches

/**
 * Punto amarillo del calendario en la barra inferior (activity_main).
 * Llamar después de cualquier acción que cambie qué partidos están pendientes
 * (inscribirse a un torneo, guardar asistencia, aplazamiento de un partido).
 */
object PendingDot {

    fun refresh(activity: Activity) {
        activity.findViewById<View>(R.id.tabPartidosPendingDot)?.visibility =
            if (PendingMatches.any(activity)) View.VISIBLE else View.GONE
    }
}
