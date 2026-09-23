package com.misw.sportalarmist.data

import android.content.Context
import org.json.JSONObject
import java.io.File

class RegistrationDraftStore(private val context: Context) {

    fun load(tournamentId: Int, teamId: String): RegistrationDraft {
        val entry = readRoot().optJSONObject(key(tournamentId, teamId)) ?: return RegistrationDraft()
        return RegistrationDraft(
            name = entry.optString("name", ""),
            dorsal = entry.optString("dorsal", ""),
            teamPassword = entry.optString("teamPassword", "")
        )
    }

    fun save(tournamentId: Int, teamId: String, draft: RegistrationDraft) {
        val root = readRoot()
        root.put(
            key(tournamentId, teamId),
            JSONObject().apply {
                put("name", draft.name)
                put("dorsal", draft.dorsal)
                put("teamPassword", draft.teamPassword)
            }
        )
        file.writeText(root.toString())
    }

    private fun readRoot(): JSONObject =
        if (file.exists()) JSONObject(file.readText()) else JSONObject()

    private fun key(tournamentId: Int, teamId: String) = "$tournamentId:$teamId"

    private val file: File
        get() = File(context.filesDir, FILE_NAME)

    private companion object {
        const val FILE_NAME = "registration_drafts.json"
    }
}
