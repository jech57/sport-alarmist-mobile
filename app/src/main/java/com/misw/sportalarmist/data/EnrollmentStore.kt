package com.misw.sportalarmist.data

import android.content.Context
import org.json.JSONObject
import java.io.File

/** Qué torneos confirmó el usuario con "Inscribirme" y con qué equipo, persistido en JSON local. */
class EnrollmentStore(private val context: Context) {

    fun enrolledTeamId(tournamentId: Int): String? {
        val root = readRoot()
        val key = tournamentId.toString()
        return if (root.has(key)) root.getString(key) else null
    }

    fun isEnrolled(tournamentId: Int): Boolean = enrolledTeamId(tournamentId) != null

    fun enrolledTournamentIds(): Set<Int> =
        readRoot().keys().asSequence().map { it.toInt() }.toSet()

    fun markEnrolled(tournamentId: Int, teamId: String) {
        val root = readRoot()
        root.put(tournamentId.toString(), teamId)
        file.writeText(root.toString())
    }

    private fun readRoot(): JSONObject =
        if (file.exists()) JSONObject(file.readText()) else JSONObject()

    private val file: File
        get() = File(context.filesDir, FILE_NAME)

    private companion object {
        const val FILE_NAME = "enrollments.json"
    }
}
