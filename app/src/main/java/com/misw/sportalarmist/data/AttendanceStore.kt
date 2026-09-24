package com.misw.sportalarmist.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

enum class AttendanceStatus { GOING, NOT_GOING, PENDING }

/** Recordatorios que el usuario puede elegir cuando confirma que va. */
enum class ReminderOffset(val minutesBefore: Long) {
    ONE_WEEK(7 * 24 * 60),
    THREE_DAYS(3 * 24 * 60),
    ONE_DAY(24 * 60),
    FIVE_HOURS(5 * 60),
    TWO_HOURS(2 * 60)
}

class AttendanceStore(private val context: Context) {

    fun statusOf(matchId: Int): AttendanceStatus {
        val value = readJson(statusFile).optString(matchId.toString(), null) ?: return AttendanceStatus.PENDING
        return when (value) {
            "going" -> AttendanceStatus.GOING
            "not_going" -> AttendanceStatus.NOT_GOING
            else -> AttendanceStatus.PENDING
        }
    }

    fun setStatus(matchId: Int, status: AttendanceStatus) {
        val root = readJson(statusFile)
        val value = when (status) {
            AttendanceStatus.GOING -> "going"
            AttendanceStatus.NOT_GOING -> "not_going"
            AttendanceStatus.PENDING -> null
        }
        if (value == null) root.remove(matchId.toString()) else root.put(matchId.toString(), value)
        statusFile.writeText(root.toString())
    }

    fun remindersOf(matchId: Int): Set<ReminderOffset> {
        val array = readJson(remindersFile).optJSONArray(matchId.toString()) ?: return emptySet()
        return (0 until array.length())
            .mapNotNull { i -> ReminderOffset.values().firstOrNull { it.name == array.optString(i) } }
            .toSet()
    }

    fun setReminders(matchId: Int, reminders: Set<ReminderOffset>) {
        val root = readJson(remindersFile)
        if (reminders.isEmpty()) {
            root.remove(matchId.toString())
        } else {
            root.put(matchId.toString(), JSONArray(reminders.map { it.name }))
        }
        remindersFile.writeText(root.toString())
    }

    private fun readJson(file: File): JSONObject =
        if (file.exists()) JSONObject(file.readText()) else JSONObject()

    private val statusFile: File get() = File(context.filesDir, STATUS_FILE_NAME)
    private val remindersFile: File get() = File(context.filesDir, REMINDERS_FILE_NAME)

    private companion object {
        const val STATUS_FILE_NAME = "attendance.json"
        const val REMINDERS_FILE_NAME = "attendance_reminders.json"
    }
}