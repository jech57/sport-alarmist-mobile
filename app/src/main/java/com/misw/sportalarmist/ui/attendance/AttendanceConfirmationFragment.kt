package com.misw.sportalarmist.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.AttendanceStatus
import com.misw.sportalarmist.data.AttendanceStore
import com.misw.sportalarmist.data.EnrollmentStore
import com.misw.sportalarmist.data.MatchRepository
import com.misw.sportalarmist.data.Teams
import com.misw.sportalarmist.data.TournamentRepository
import com.misw.sportalarmist.databinding.FragmentAttendanceConfirmationBinding

class AttendanceConfirmationFragment : Fragment() {

    private var _binding: FragmentAttendanceConfirmationBinding? = null
    private val binding get() = _binding!!

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

        binding.notGoingButton.buttonLabel.text = getString(R.string.tab_not_going)

        binding.goingButton.button.setBackgroundResource(R.drawable.bg_button_outer_primary)
        binding.goingButton.buttonLabel.apply {
            setBackgroundResource(R.drawable.bg_button_inner_primary)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.button_text_primary))
            text = getString(R.string.tab_going)
        }

        binding.notGoingButton.button.setOnClickListener {
            attendanceStore.setStatus(match.id, AttendanceStatus.NOT_GOING)
            findNavController().navigateUp()
        }
        binding.goingButton.button.setOnClickListener {
            attendanceStore.setStatus(match.id, AttendanceStatus.GOING)
            findNavController().navigateUp()
        }
    }

    private fun badgeFor(teamId: String) =
        if (teamId == TEAM_ESTRELLA) R.drawable.ic_team_badge_estrella else R.drawable.ic_team_badge_triangulo

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val TEAM_ESTRELLA = "estrella"
    }
}
