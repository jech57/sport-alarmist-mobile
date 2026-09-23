package com.misw.sportalarmist.ui.tournament

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.Teams
import com.misw.sportalarmist.data.TournamentRepository
import com.misw.sportalarmist.databinding.FragmentTournamentDetailBinding

class TournamentDetailFragment : Fragment() {

    private var _binding: FragmentTournamentDetailBinding? = null
    private val binding get() = _binding!!

    private var tournamentId: Int = SAMPLE_TOURNAMENT_ID

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

        tournamentId = arguments?.getInt("tournament_id") ?: SAMPLE_TOURNAMENT_ID
        val tournament = TournamentRepository(requireContext()).getAll()
            .firstOrNull { it.id == tournamentId }
        binding.tournamentTitle.text = tournament?.name ?: getString(R.string.tournament_sample_name)

        binding.searchBar.searchBarIcon.setImageResource(R.drawable.ic_search)
        binding.searchBar.searchBarHint.hint = getString(R.string.hint_search_team)

        binding.appBar.appBarBackIcon.visibility = View.VISIBLE
        binding.appBar.appBarBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.teamTriangulo.setOnClickListener { goToRegistration("triangulo") }
        binding.teamEstrella.setOnClickListener { goToRegistration("estrella") }

        setUpSearch()
    }

    private fun setUpSearch() {
        binding.searchBar.searchBarHint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString().orEmpty()
                val matchingIds = Teams.ALL.filter { it.name.contains(query, ignoreCase = true) }
                    .map { it.id }

                binding.teamTriangulo.visibility = if ("triangulo" in matchingIds) View.VISIBLE else View.GONE
                binding.teamEstrella.visibility = if ("estrella" in matchingIds) View.VISIBLE else View.GONE
                binding.noResultsText.visibility = if (matchingIds.isEmpty()) View.VISIBLE else View.GONE
            }
        })
    }

    private fun goToRegistration(teamId: String) {
        findNavController().navigate(
            R.id.action_tournamentDetailFragment_to_registrationFragment,
            bundleOf("tournament_id" to tournamentId, "team_id" to teamId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val SAMPLE_TOURNAMENT_ID = 1
    }
}
