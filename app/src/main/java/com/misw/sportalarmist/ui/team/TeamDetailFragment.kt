package com.misw.sportalarmist.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.RegistrationDraftStore
import com.misw.sportalarmist.data.Teams
import com.misw.sportalarmist.data.TournamentRepository
import com.misw.sportalarmist.databinding.FragmentTeamDetailBinding

class TeamDetailFragment : Fragment() {

    private var _binding: FragmentTeamDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tournamentId = arguments?.getInt("tournament_id") ?: 0
        val teamId = arguments?.getString("team_id").orEmpty()

        val tournament = TournamentRepository(requireContext()).getAll()
            .firstOrNull { it.id == tournamentId }
        val team = Teams.ALL.firstOrNull { it.id == teamId }
        val draft = RegistrationDraftStore(requireContext()).load(tournamentId, teamId)

        binding.appBar.appBarBackIcon.visibility = View.VISIBLE
        binding.appBar.appBarBackIcon.setOnClickListener { findNavController().navigateUp() }

        binding.tournamentTitle.text = tournament?.name.orEmpty()
        binding.teamName.text = team?.name.orEmpty()
        binding.teamIcon.setImageResource(
            if (teamId == TEAM_ESTRELLA) R.drawable.ic_team_badge_estrella else R.drawable.ic_team_badge_triangulo
        )
        binding.dorsalValue.text = getString(R.string.dorsal_value, draft.dorsal)
        binding.nameValue.text = draft.name

        binding.goToMatchesButton.buttonLabel.apply {
            setBackgroundResource(R.drawable.bg_button_inner_neutral)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.button_text_neutral))
            text = getString(R.string.action_go_to_matches, tournament?.name.orEmpty())
        }
        binding.goToMatchesButton.button.setOnClickListener {
            findNavController().navigate(
                R.id.matchesFragment,
                bundleOf("filter_tournament" to tournament?.name)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val TEAM_ESTRELLA = "estrella"
    }
}
