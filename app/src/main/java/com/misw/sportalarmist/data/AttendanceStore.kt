package com.misw.sportalarmist.data

import android.content.Context
import org.json.JSONObject
import java.io.File

enum class AttendanceStatus { GOING, NOT_GOING, PENDING }

class AttendanceStore(private val context: Context) {

    fun statusOf(matchId: Int): AttendanceStatus {
        val value = readRoot().optString(matchId.toString(), null) ?: return AttendanceStatus.PENDING
        return when (value) {
            "going" -> AttendanceStatus.GOING
            "not_going" -> AttendanceStatus.NOT_GOING
            else -> AttendanceStatus.PENDING
        }
    }

    fun setStatus(matchId: Int, status: AttendanceStatus) {
        val root = readRoot()
        val value = when (status) {
            AttendanceStatus.GOING -> "going"
            AttendanceStatus.NOT_GOING -> "not_going"
            AttendanceStatus.PENDING -> null
        }
        if (value == null) root.remove(matchId.toString()) else root.put(matchId.toString(), value)
        file.writeText(root.toString())
    }

    private fun readRoot(): JSONObject =
        if (file.exists()) JSONObject(file.readText()) else JSONObject()

    private val file: File get() = File(context.filesDir, FILE_NAME)

    private companion object {
        const val FILE_NAME = "attendance.json"
    }
}
