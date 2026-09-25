package com.misw.sportalarmist.ui.attendance

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
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
import com.misw.sportalarmist.data.Match
import com.misw.sportalarmist.data.MatchChange
import com.misw.sportalarmist.data.MatchChangeStore
import com.misw.sportalarmist.data.MatchRepository
import com.misw.sportalarmist.data.ReminderOffset
import com.misw.sportalarmist.data.Teams
import com.misw.sportalarmist.data.TournamentRepository
import com.misw.sportalarmist.databinding.FragmentAttendanceConfirmationBinding
import com.misw.sportalarmist.ui.MatchChangeSimulator
import com.misw.sportalarmist.ui.SuccessToast

class AttendanceConfirmationFragment : Fragment() {

    private var _binding: FragmentAttendanceConfirmationBinding? = null
    private val binding get() = _binding!!

    /** Selección en pantalla; solo se persiste al tocar Guardar. */
    private var selectedStatus = AttendanceStatus.PENDING
    private lateinit var reminderChecks: Map<ReminderOffset, CheckBox>

    /** Lo que estaba guardado al abrir la pantalla, para saber si hubo cambios. */
    private var initialStatus = AttendanceStatus.PENDING
    private var initialReminders: Set<ReminderOffset> = emptySet()
    private var initialChange: MatchChange? = null
    private lateinit var changeStore: MatchChangeStore

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
        changeStore = MatchChangeStore(requireContext())
        val teamId = enrollmentStore.enrolledTeamId(tournamentId).orEmpty()
        val tournament = TournamentRepository(requireContext()).getAll()
            .firstOrNull { it.id == tournamentId }
        val match = MatchRepository(enrollmentStore, changeStore).forEnrollment(tournamentId, teamId)
        val homeTeam = Teams.ALL.firstOrNull { it.id == match.homeTeamId }
        val awayTeam = Teams.ALL.firstOrNull { it.id == match.awayTeamId }

        binding.appBar.appBarBackIcon.visibility = View.VISIBLE
        binding.appBar.appBarBackIcon.setOnClickListener { findNavController().navigateUp() }

        binding.tournamentTitle.text = tournament?.name.orEmpty()
        binding.homeTeamIcon.setImageResource(badgeFor(match.homeTeamId))
        binding.homeTeamName.text = homeTeam?.name.orEmpty()
        binding.awayTeamIcon.setImageResource(badgeFor(match.awayTeamId))
        binding.awayTeamName.text = awayTeam?.name.orEmpty()
        initialChange = changeStore.changeOf(match.id)
        binding.matchDateTime.text = dateTimeText(match, initialChange)

        reminderChecks = mapOf(
            ReminderOffset.ONE_WEEK to binding.reminderOneWeek,
            ReminderOffset.THREE_DAYS to binding.reminderThreeDays,
            ReminderOffset.ONE_DAY to binding.reminderOneDay,
            ReminderOffset.FIVE_HOURS to binding.reminderFiveHours,
            ReminderOffset.TWO_HOURS to binding.reminderTwoHours
        )

        // Lo guardado no cambia hasta tocar Guardar, así que sirve también tras una rotación.
        initialStatus = attendanceStore.statusOf(match.id)
        initialReminders = attendanceStore.remindersOf(match.id)

        if (savedInstanceState == null) {
            // Primera vez: se carga lo guardado (PENDING = ambos botones sin seleccionar).
            selectedStatus = initialStatus
            reminderChecks.forEach { (offset, check) -> check.isChecked = offset in initialReminders }
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

    private fun checkedReminders(): Set<ReminderOffset> =
        reminderChecks.filterValues { it.isChecked }.keys

    private fun isValidSelection(): Boolean = when (selectedStatus) {
        AttendanceStatus.GOING -> checkedReminders().isNotEmpty()
        AttendanceStatus.NOT_GOING -> true
        AttendanceStatus.PENDING -> false
    }

    /** ¿Lo que hay en pantalla es distinto de lo guardado? */
    private fun hasChanges(): Boolean =
        selectedStatus != initialStatus ||
                (selectedStatus == AttendanceStatus.GOING && checkedReminders() != initialReminders)

    /** Guardar solo se habilita con una selección válida y distinta de la guardada. */
    private fun canSave(): Boolean = isValidSelection() && hasChanges()

    /**
     * Editando = el partido ya tenía una respuesta guardada, o fue aplazado y el
     * usuario está volviendo a confirmar.
     */
    private fun isEditing(): Boolean =
        initialStatus != AttendanceStatus.PENDING || initialChange != null

    /** "fecha · hora" con la parte que cambió en otro color. */
    private fun dateTimeText(match: Match, change: MatchChange?): CharSequence {
        val full = getString(R.string.match_date_time, match.date, match.time)
        if (change == null) return full
        val highlight = color(R.color.match_changed_highlight)
        return SpannableString(full).apply {
            if (change.affectsDate) {
                val start = full.indexOf(match.date)
                if (start >= 0) {
                    setSpan(ForegroundColorSpan(highlight), start, start + match.date.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            if (change.affectsTime) {
                val start = full.lastIndexOf(match.time)
                if (start >= 0) {
                    setSpan(ForegroundColorSpan(highlight), start, start + match.time.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }

    private fun updateSaveButton() {
        val enabled = canSave()
        binding.saveButton.button.isEnabled = enabled
        if (enabled) {
            binding.saveButton.button.setBackgroundResource(R.drawable.bg_button_outer_primary)
            binding.saveButton.buttonLabel.setBackgroundResource(R.drawable.bg_button_inner_primary)
            binding.saveButton.buttonLabel.setTextColor(color(R.color.button_text_primary))
        } else {
            // Sólido, sin transparencia: fondo oscuro, borde y texto naranja apagado.
            binding.saveButton.button.setBackgroundResource(R.drawable.bg_button_outer_primary_disabled)
            binding.saveButton.buttonLabel.background = null
            binding.saveButton.buttonLabel.setTextColor(color(R.color.button_text_primary_disabled))
        }
    }

    private fun save(store: AttendanceStore, matchId: Int) {
        if (!canSave()) return
        val editing = isEditing()
        store.setStatus(matchId, selectedStatus)
        val reminders = if (selectedStatus == AttendanceStatus.GOING) checkedReminders() else emptySet()
        store.setReminders(matchId, reminders)
        changeStore.clearHighlight(matchId) // ya reconfirmó: se quita el resaltado
        when {
            editing -> SuccessToast.show(requireActivity(), R.string.changes_saved_success)
            selectedStatus == AttendanceStatus.GOING ->
                SuccessToast.show(requireActivity(), R.string.alarm_configured_success)
        }
        MatchChangeSimulator.onAttendanceSaved(requireActivity(), matchId, selectedStatus)
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