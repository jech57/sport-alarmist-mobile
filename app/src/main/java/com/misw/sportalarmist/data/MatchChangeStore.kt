package com.misw.sportalarmist.data

import android.content.Context
import org.json.JSONObject
import java.io.File

/** Qué se aplazó del partido. Define el texto del modal y qué se resalta en pantalla. */
enum class MatchChange {
    DATE, TIME, DATE_AND_TIME;

    val affectsDate: Boolean get() = this != TIME
    val affectsTime: Boolean get() = this != DATE
}

/**
 * Cambios simulados de fecha/hora de los partidos (no hay backend).
 * Guarda por partido: la fecha/hora nueva y qué cambió (para resaltarlo hasta que
 * el usuario vuelva a confirmar).
 */
class MatchChangeStore(private val context: Context) {

    data class Schedule(val date: String, val time: String)

    fun scheduleOf(matchId: Int): Schedule? {
        val entry = entryOf(matchId) ?: return null
        val date = entry.optString(KEY_DATE, "")
        val time = entry.optString(KEY_TIME, "")
        return if (date.isNotEmpty() && time.isNotEmpty()) Schedule(date, time) else null
    }

    /** ¿El partido ya fue aplazado alguna vez? Cada partido se aplaza como máximo una vez. */
    fun hasBeenChanged(matchId: Int): Boolean = scheduleOf(matchId) != null

    /** Qué cambió y todavía no ha sido reconfirmado por el usuario, o null. */
    fun changeOf(matchId: Int): MatchChange? {
        val name = entryOf(matchId)?.optString(KEY_CHANGE, "").orEmpty()
        return MatchChange.values().firstOrNull { it.name == name }
    }

    fun applyChange(matchId: Int, date: String, time: String, change: MatchChange) = edit(matchId) {
        put(KEY_DATE, date)
        put(KEY_TIME, time)
        put(KEY_CHANGE, change.name)
    }

    /** El usuario ya volvió a confirmar: se quita el resaltado (la fecha/hora nueva se mantiene). */
    fun clearHighlight(matchId: Int) = edit(matchId) { remove(KEY_CHANGE) }

    private fun entryOf(matchId: Int): JSONObject? = readRoot().optJSONObject(matchId.toString())

    private fun edit(matchId: Int, block: JSONObject.() -> Unit) {
        val root = readRoot()
        val entry = root.optJSONObject(matchId.toString()) ?: JSONObject()
        entry.block()
        root.put(matchId.toString(), entry)
        file.writeText(root.toString())
    }

    private fun readRoot(): JSONObject =
        if (file.exists()) JSONObject(file.readText()) else JSONObject()

    private val file: File get() = File(context.filesDir, FILE_NAME)

    private companion object {
        const val FILE_NAME = "match_changes.json"
        const val KEY_DATE = "date"
        const val KEY_TIME = "time"
        const val KEY_CHANGE = "change"
    }
}