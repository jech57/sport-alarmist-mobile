package com.misw.sportalarmist.data

import android.content.Context
import org.json.JSONArray
import java.io.File

/** Qué torneos confirmó el usuario con "Inscribirme", persistido en JSON local. */
class EnrollmentStore(private val context: Context) {

    fun enrolledTournamentIds(): Set<Int> {
        if (!file.exists()) return emptySet()
        val array = JSONArray(file.readText())
        return (0 until array.length()).map { array.getInt(it) }.toSet()
    }

    fun isEnrolled(tournamentId: Int): Boolean = tournamentId in enrolledTournamentIds()

    fun markEnrolled(tournamentId: Int) {
        val ids = enrolledTournamentIds() + tournamentId
        file.writeText(JSONArray(ids.toList()).toString())
    }

    private val file: File
        get() = File(context.filesDir, FILE_NAME)

    private companion object {
        const val FILE_NAME = "enrollments.json"
    }
}
