package com.misw.sportalarmist.ui

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.misw.sportalarmist.R

/**
 * Mensaje de éxito (layout toast_success) que aparece justo encima de la barra
 * inferior de navegación. Se usa en la inscripción a un torneo y al configurar
 * la alarma de un partido.
 *
 * No usa un Toast del sistema (Android decide dónde ponerlo y a veces ignora la
 * posición pedida): se muestra dentro de la app, en el contenedor toastHost de
 * activity_main.xml, que está anclado arriba de la barra inferior.
 */
object SuccessToast {

    private const val VISIBLE_MS = 2000L
    private const val FADE_MS = 200L

    fun show(activity: Activity, @StringRes message: Int) {
        val host = activity.findViewById<ViewGroup>(R.id.toastHost)
        if (host == null) {
            // Por si se llama desde una pantalla sin activity_main.
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            return
        }

        // Si ya había un mensaje visible, se reemplaza.
        (host.getTag(R.id.toastHost) as? Runnable)?.let { host.removeCallbacks(it) }
        host.animate().cancel()
        host.removeAllViews()

        val toastView = LayoutInflater.from(activity).inflate(R.layout.toast_success, host, false)
        firstTextView(toastView)?.setText(message)
        host.addView(toastView)

        host.alpha = 0f
        host.visibility = View.VISIBLE
        host.animate().alpha(1f).setDuration(FADE_MS).start()

        val hide = Runnable {
            host.animate().alpha(0f).setDuration(FADE_MS).withEndAction {
                host.visibility = View.GONE
                host.removeAllViews()
            }.start()
        }
        host.setTag(R.id.toastHost, hide)
        host.postDelayed(hide, VISIBLE_MS)
    }

    /** Busca el TextView del layout del toast para ponerle el mensaje. */
    private fun firstTextView(view: View): TextView? {
        if (view is TextView) return view
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                firstTextView(view.getChildAt(i))?.let { return it }
            }
        }
        return null
    }
}