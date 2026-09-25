package com.misw.sportalarmist.data

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Calcula fechas a partir de match.date + match.time.
 * Usa SimpleDateFormat/Calendar para funcionar con cualquier minSdk.
 */
object MatchSchedule {

    // Formatos aceptados para "fecha hora" del partido (ej. "12-07-2026 07:30 PM").
    // Si tu Match usa otro formato, agrégalo aquí.
    private val inputPatterns = listOf(
        "dd-MM-yyyy hh:mm a",
        "dd-MM-yyyy HH:mm",
        "yyyy-MM-dd hh:mm a",
        "yyyy-MM-dd HH:mm"
    )

    // Formato del mockup: "11-07-26 07:30 PM".
    private const val ALARM_OUTPUT_PATTERN = "dd-MM-yy hh:mm a"
    private const val MATCH_DATE_PATTERN = "dd-MM-yyyy"
    private const val MATCH_TIME_PATTERN = "hh:mm a"

    fun startOf(match: Match): Date? {
        val raw = "${match.date} ${match.time}".trim()
        for (pattern in inputPatterns) {
            // Locale.US para que "AM/PM" se lea igual sin importar el idioma del teléfono.
            val format = SimpleDateFormat(pattern, Locale.US).apply { isLenient = false }
            val position = ParsePosition(0)
            val date = format.parse(raw, position)
            // Solo se acepta si se consumió todo el texto (evita parseos parciales).
            if (date != null && position.index == raw.length) return date
        }
        return null
    }

    /** La alarma más cercana que todavía no ha pasado, o null si no hay ninguna. */
    fun nextAlarm(match: Match, reminders: Set<ReminderOffset>, now: Date = Date()): Date? {
        val start = startOf(match) ?: return null
        return reminders
            .map { offset ->
                Calendar.getInstance().apply {
                    time = start
                    add(Calendar.MINUTE, -offset.minutesBefore.toInt())
                }.time
            }
            .filter { it.after(now) }
            .minOrNull()
    }

    fun formatAlarm(date: Date): String =
        SimpleDateFormat(ALARM_OUTPUT_PATTERN, Locale.US).format(date)

    /** Mismo formato que match.date ("12-07-2026"). */
    fun formatMatchDate(date: Date): String =
        SimpleDateFormat(MATCH_DATE_PATTERN, Locale.US).format(date)

    /** Mismo formato que match.time ("07:30 PM"). */
    fun formatMatchTime(date: Date): String =
        SimpleDateFormat(MATCH_TIME_PATTERN, Locale.US).format(date)
}