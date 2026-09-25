package com.misw.sportalarmist.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.AttendanceStatus
import com.misw.sportalarmist.data.AttendanceStore
import com.misw.sportalarmist.data.EnrollmentStore
import com.misw.sportalarmist.data.MatchChange
import com.misw.sportalarmist.data.MatchChangeStore
import com.misw.sportalarmist.data.MatchRepository
import com.misw.sportalarmist.data.MatchSchedule
import com.misw.sportalarmist.databinding.DialogMatchChangedBinding
import java.lang.ref.WeakReference
import java.util.Calendar
import kotlin.random.Random

/** Pantallas que deben refrescarse cuando cambia la fecha/hora de un partido. */
interface MatchChangeListener {
    fun onMatchChanged()
}

/**
 * Simula que el organizador aplaza un partido (no hay backend).
 * Cada partido se aplaza COMO MÁXIMO UNA VEZ (el modal nunca sale dos veces para el mismo partido):
 * - El primer "Voy" desde que se inició la app (en cualquier partido) SIEMPRE lo aplaza.
 * - Cualquier otro guardado con un estado de RANDOM_TRIGGER_STATUSES tiene CHANGE_CHANCE de aplazarlo.
 *
 * DELAY_MS después de guardar: se aplaza fecha, hora o ambas al azar, el partido vuelve a
 * "Pendientes" (los recordatorios se conservan para precargarlos) y se muestra el modal "Atención".
 */
object MatchChangeSimulator {

    private const val DELAY_MS = 5_000L
    private const val CHANGE_CHANCE = 1.0 / 3.0

    /** Estados que disparan la probabilidad de cambio (si el partido nunca se ha aplazado). */
    private val RANDOM_TRIGGER_STATUSES = setOf(AttendanceStatus.GOING, AttendanceStatus.NOT_GOING)

    /**
     * ¿Ya hubo un "Voy" que aplazó un partido desde que se inició la app?
     * Vive en memoria: vuelve a false cuando el proceso de la app se reinicia.
     */
    private var firstGoingOfLaunchUsed = false

    // Rango de horas de inicio posibles al aplazar la hora (en minutos desde medianoche).
    private const val EARLIEST_START_MINUTES = 6 * 60   // 06:00 AM
    private const val LATEST_START_MINUTES = 22 * 60    // 10:00 PM
    private const val TIME_STEP_MINUTES = 30

    private val handler = Handler(Looper.getMainLooper())

    /** Partidos con un aplazamiento programado que todavía no se ha aplicado (los 5 segundos). */
    private val scheduledMatchIds = mutableSetOf<Int>()

    /** Llamar justo después de guardar la asistencia de un partido. */
    fun onAttendanceSaved(activity: FragmentActivity, matchId: Int, status: AttendanceStatus) {
        val changeStore = MatchChangeStore(activity)
        // Si ya se aplazó una vez (o hay un aplazamiento en camino), no se vuelve a aplazar.
        if (changeStore.hasBeenChanged(matchId) || matchId in scheduledMatchIds) return

        val shouldChange = when {
            status == AttendanceStatus.GOING && !firstGoingOfLaunchUsed -> {
                firstGoingOfLaunchUsed = true
                true
            }
            status in RANDOM_TRIGGER_STATUSES -> Random.nextDouble() < CHANGE_CHANCE
            else -> false
        }
        if (shouldChange) schedule(activity, matchId)
    }

    private fun schedule(activity: FragmentActivity, matchId: Int) {
        val appContext = activity.applicationContext
        val activityRef = WeakReference(activity)
        scheduledMatchIds += matchId
        handler.postDelayed({
            scheduledMatchIds -= matchId
            val change = applyRandomChange(appContext, matchId) ?: return@postDelayed
            val current = activityRef.get()
            if (current != null && !current.isFinishing && !current.isDestroyed) {
                refreshScreens(current)
                showDialog(current, change)
            }
        }, DELAY_MS)
    }

    /** Aplaza fecha, hora o ambas y devuelve el partido a "Pendientes". */
    private fun applyRandomChange(context: android.content.Context, matchId: Int): MatchChange? {
        val changeStore = MatchChangeStore(context)
        if (changeStore.hasBeenChanged(matchId)) return null
        val match = MatchRepository(EnrollmentStore(context), changeStore).getAll()
            .firstOrNull { it.id == matchId } ?: return null
        val start = MatchSchedule.startOf(match) ?: return null

        val change = MatchChange.values().random()
        val calendar = Calendar.getInstance().apply { time = start }
        if (change.affectsDate) calendar.add(Calendar.DAY_OF_YEAR, Random.nextInt(1, 8)) // 1 a 7 días
        if (change.affectsTime) postponeTime(calendar)

        changeStore.applyChange(
            matchId,
            MatchSchedule.formatMatchDate(calendar.time),
            MatchSchedule.formatMatchTime(calendar.time),
            change
        )
        // Vuelve a Pendientes. Los recordatorios NO se borran: se precargan al volver a decir "Voy".
        AttendanceStore(context).setStatus(matchId, AttendanceStatus.PENDING)
        return change
    }

    /** Mueve la hora más tarde el mismo día (30 min a 3 h); si ya es muy tarde, a otra hora del día. */
    private fun postponeTime(calendar: Calendar) {
        val current = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        val later = (1..6).map { current + it * TIME_STEP_MINUTES }.filter { it <= LATEST_START_MINUTES }
        val newMinutes = if (later.isNotEmpty()) {
            later.random()
        } else {
            (EARLIEST_START_MINUTES..LATEST_START_MINUTES step TIME_STEP_MINUTES)
                .filter { it != current }
                .random()
        }
        calendar.set(Calendar.HOUR_OF_DAY, newMinutes / 60)
        calendar.set(Calendar.MINUTE, newMinutes % 60)
    }

    private fun refreshScreens(activity: FragmentActivity) {
        activity.findViewById<View>(R.id.tabPartidosPendingDot)?.visibility = View.VISIBLE
        val navHost = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHost?.childFragmentManager?.fragments?.forEach { fragment ->
            (fragment as? MatchChangeListener)?.onMatchChanged()
        }
    }

    private fun showDialog(activity: FragmentActivity, change: MatchChange) {
        val binding = DialogMatchChangedBinding.inflate(activity.layoutInflater)
        binding.dialogMessage.setText(
            when (change) {
                MatchChange.DATE -> R.string.match_changed_date
                MatchChange.TIME -> R.string.match_changed_time
                MatchChange.DATE_AND_TIME -> R.string.match_changed_date_and_time
            }
        )

        val dialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .setCancelable(false) // solo se cierra con OK
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.okButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}