package com.misw.sportalarmist.ui.tournament

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.sportalarmist.R
import com.misw.sportalarmist.databinding.FragmentTournamentDetailBinding

class TournamentDetailFragment : Fragment() {

    private var _binding: FragmentTournamentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTournamentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBar.searchBarIcon.setImageResource(R.drawable.ic_search)
        binding.searchBar.searchBarHint.setText(R.string.hint_search_team)

        binding.appBar.appBarBackIcon.visibility = View.VISIBLE
        binding.appBar.appBarBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        val tournamentId = arguments?.getInt("tournament_id") ?: SAMPLE_TOURNAMENT_ID
        binding.teamTriangulo.setOnClickListener { goToRegistration(tournamentId, "triangulo") }
        binding.teamEstrella.setOnClickListener { goToRegistration(tournamentId, "estrella") }
    }

    private fun goToRegistration(tournamentId: Int, teamId: String) {
        findNavController().navigate(
            R.id.action_tournamentDetailFragment_to_registrationFragment,
            bundleOf("tournament_id" to tournamentId, "team_id" to teamId)
        )
    }

    private companion object {
        const val SAMPLE_TOURNAMENT_ID = 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
