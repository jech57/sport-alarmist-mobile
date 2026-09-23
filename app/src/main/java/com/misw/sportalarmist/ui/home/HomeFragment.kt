package com.misw.sportalarmist.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.EnrollmentStore
import com.misw.sportalarmist.data.Tournament
import com.misw.sportalarmist.data.TournamentRepository
import com.misw.sportalarmist.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var enrollmentStore: EnrollmentStore
    private var tournaments: List<Tournament> = emptyList()
    private lateinit var tournamentsAdapter: TournamentListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enrollmentStore = EnrollmentStore(requireContext())
        tournaments = TournamentRepository(requireContext()).getAll()

        binding.searchBar.searchBarIcon.setImageResource(R.drawable.ic_search)
        binding.searchBar.searchBarHint.hint = getString(R.string.hint_search_tournament)

        setUpTournamentsList()
        setUpSearch()
    }

    private fun setUpTournamentsList() {
        tournamentsAdapter = TournamentListAdapter(
            enrolledTeamId = { enrollmentStore.enrolledTeamId(it.id) },
            onClick = { goToTournament(it) }
        )
        binding.tournamentsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tournamentsAdapter
        }
        tournamentsAdapter.submitList(sortEnrolledFirst(tournaments))
    }

    private fun setUpSearch() {
        binding.searchBar.searchBarHint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString().orEmpty()
                val matches = tournaments.filter { it.name.contains(query, ignoreCase = true) }
                tournamentsAdapter.submitList(sortEnrolledFirst(matches))
                binding.noResultsText.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE
                binding.tournamentsList.visibility = if (matches.isEmpty()) View.GONE else View.VISIBLE
            }
        })
    }

    private fun sortEnrolledFirst(list: List<Tournament>): List<Tournament> =
        list.sortedByDescending { enrollmentStore.isEnrolled(it.id) }

    private fun goToTournament(tournament: Tournament) {
        findNavController().navigate(
            R.id.action_homeFragment_to_tournamentDetailFragment,
            bundleOf("tournament_id" to tournament.id)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
