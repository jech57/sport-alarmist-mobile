package com.misw.sportalarmist.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.AttendanceStatus
import com.misw.sportalarmist.data.AttendanceStore
import com.misw.sportalarmist.data.EnrollmentStore
import com.misw.sportalarmist.data.MatchRepository
import com.misw.sportalarmist.data.ReminderOffset
import com.misw.sportalarmist.data.Teams
import com.misw.sportalarmist.data.TournamentRepository
import com.misw.sportalarmist.databinding.FragmentAttendanceConfirmationBinding

class AttendanceConfirmationFragment : Fragment() {

    private var _binding: FragmentAttendanceConfirmationBinding? = null
    private val binding get() = _binding!!

    /** Selección en pantalla; solo se persiste al tocar Guardar. */
    private var selectedStatus = AttendanceStatus.PENDING
    private lateinit var reminderChecks: Map<ReminderOffset, CheckBox>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttendanceConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tournamentId = arguments?.getInt("tournament_id") ?: 0
        val enrollmentStore = EnrollmentStore(requireContext())
        val attendanceStore = AttendanceStore(requireContext())
        val teamId = enrollmentStore.enrolledTeamId(tournamentId).orEmpty()
        val tournament = TournamentRepository(requireContext()).getAll()
            .firstOrNull { it.id == tournamentId }
        val match = MatchRepository(enrollmentStore).forEnrollment(tournamentId, teamId)
        val homeTeam = Teams.ALL.firstOrNull { it.id == match.homeTeamId }
        val awayTeam = Teams.ALL.firstOrNull { it.id == match.awayTeamId }

        binding.appBar.appBarBackIcon.visibility = View.VISIBLE
        binding.appBar.appBarBackIcon.setOnClickListener { findNavController().navigateUp() }

        binding.tournamentTitle.text = tournament?.name.orEmpty()
        binding.homeTeamIcon.setImageResource(badgeFor(match.homeTeamId))
        binding.homeTeamName.text = homeTeam?.name.orEmpty()
        binding.awayTeamIcon.setImageResource(badgeFor(match.awayTeamId))
        binding.awayTeamName.text = awayTeam?.name.orEmpty()
        binding.matchDateTime.text = getString(R.string.match_date_time, match.date, match.time)

        reminderChecks = mapOf(
            ReminderOffset.ONE_WEEK to binding.reminderOneWeek,
            ReminderOffset.THREE_DAYS to binding.reminderThreeDays,
            ReminderOffset.ONE_DAY to binding.reminderOneDay,
            ReminderOffset.FIVE_HOURS to binding.reminderFiveHours,
            ReminderOffset.TWO_HOURS to binding.reminderTwoHours
        )

        if (savedInstanceState == null) {
            // Primera vez: se carga lo guardado (PENDING = ambos botones sin seleccionar).
            selectedStatus = attendanceStore.statusOf(match.id)
            val savedReminders = attendanceStore.remindersOf(match.id)
            reminderChecks.forEach { (offset, check) -> check.isChecked = offset in savedReminders }
        } else {
            // Rotación: los CheckBox restauran su estado solos; el estado de asistencia lo restauramos aquí.
            selectedStatus = savedInstanceState.getString(KEY_STATUS)
                ?.let { AttendanceStatus.valueOf(it) } ?: AttendanceStatus.PENDING
        }

        binding.goingButton.setOnClickListener { selectStatus(AttendanceStatus.GOING) }
        binding.notGoingButton.setOnClickListener { selectStatus(AttendanceStatus.NOT_GOING) }
        reminderChecks.values.forEach { check ->
            check.setOnCheckedChangeListener { _, _ -> updateSaveButton() }
        }

        binding.cancelButton.buttonLabel.text = getString(R.string.action_cancel)
        binding.cancelButton.button.setOnClickListener { findNavController().navigateUp() }

        binding.saveButton.button.setBackgroundResource(R.drawable.bg_button_outer_primary)
        binding.saveButton.buttonLabel.text = getString(R.string.attendance_save)
        binding.saveButton.button.setOnClickListener { save(attendanceStore, match.id) }

        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_STATUS, selectedStatus.name)
    }

    private fun selectStatus(status: AttendanceStatus) {
        selectedStatus = status
        render()
    }

    private fun render() {
        val pending = selectedStatus == AttendanceStatus.PENDING
        styleChoice(
            binding.goingButton,
            selected = selectedStatus == AttendanceStatus.GOING,
            pending = pending,
            selectedBackground = R.drawable.bg_attendance_going
        )
        styleChoice(
            binding.notGoingButton,
            selected = selectedStatus == AttendanceStatus.NOT_GOING,
            pending = pending,
            selectedBackground = R.drawable.bg_attendance_not_going
        )
        binding.remindersSection.visibility =
            if (selectedStatus == AttendanceStatus.GOING) View.VISIBLE else View.GONE
        updateSaveButton()
    }

    /** Tres estados: sin elegir (gris), elegido (verde/rojo) y no elegido (oscuro). */
    private fun styleChoice(
        view: TextView,
        selected: Boolean,
        pending: Boolean,
        @DrawableRes selectedBackground: Int
    ) {
        val (background, textColor) = when {
            selected -> selectedBackground to R.color.attendance_on_selected
            pending -> R.drawable.bg_attendance_idle to R.color.white
            else -> R.drawable.bg_attendance_dimmed to R.color.attendance_dimmed_text
        }
        view.setBackgroundResource(background)
        view.setTextColor(color(textColor))
    }

    private fun canSave(): Boolean = when (selectedStatus) {
        AttendanceStatus.GOING -> reminderChecks.values.any { it.isChecked }
        AttendanceStatus.NOT_GOING -> true
        AttendanceStatus.PENDING -> false
    }

    private fun updateSaveButton() {
        val enabled = canSave()
        binding.saveButton.button.isEnabled = enabled
        binding.saveButton.button.alpha = if (enabled) 1f else 0.6f
        binding.saveButton.buttonLabel.apply {
            if (enabled) {
                setBackgroundResource(R.drawable.bg_button_inner_primary)
                setTextColor(color(R.color.button_text_primary))
            } else {
                background = null
                setTextColor(color(R.color.orange_primary))
            }
        }
    }

    private fun save(store: AttendanceStore, matchId: Int) {
        if (!canSave()) return
        store.setStatus(matchId, selectedStatus)
        val reminders = if (selectedStatus == AttendanceStatus.GOING) {
            reminderChecks.filterValues { it.isChecked }.keys
        } else {
            emptySet()
        }
        store.setReminders(matchId, reminders)
        findNavController().navigateUp()
    }

    private fun color(@ColorRes res: Int) = ContextCompat.getColor(requireContext(), res)

    private fun badgeFor(teamId: String) =
        if (teamId == TEAM_ESTRELLA) R.drawable.ic_team_badge_estrella else R.drawable.ic_team_badge_triangulo

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val TEAM_ESTRELLA = "estrella"
        const val KEY_STATUS = "attendance_selected_status"
    }
}