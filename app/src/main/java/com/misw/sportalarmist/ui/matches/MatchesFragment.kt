package com.misw.sportalarmist.ui.matches

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.EnrollmentStore
import com.misw.sportalarmist.data.Tournament
import com.misw.sportalarmist.data.TournamentRepository
import com.misw.sportalarmist.databinding.FragmentMatchesBinding
import com.misw.sportalarmist.ui.common.SearchResultAdapter

class MatchesFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    private lateinit var enrolledTournaments: List<Tournament>
    private lateinit var searchAdapter: SearchResultAdapter<Tournament>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // El texto del empty state depende del tab seleccionado (nodos
        // 30:1850 "Voy", 14:6371 "No voy", 14:6291 "Pendientes").
        binding.matchesTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.emptyStateText.setText(emptyStateTextFor(tab.position))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })

        setUpSearch()
    }

    private fun setUpSearch() {
        val enrollmentStore = EnrollmentStore(requireContext())
        val enrolledIds = enrollmentStore.enrolledTournamentIds()
        enrolledTournaments = TournamentRepository(requireContext()).getAll()
            .filter { it.id in enrolledIds }

        // Solo filtra: seleccionar un torneo no navega a ningún lado, eso
        // sigue siendo tarea del listado de Torneos.
        searchAdapter = SearchResultAdapter<Tournament>(label = { it.name }, onClick = {})
        binding.searchResults.searchResultsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        binding.searchBar.searchBarHint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString().orEmpty()
                if (query.isBlank()) {
                    binding.searchResults.searchResultsList.visibility = View.GONE
                    binding.noResultsText.visibility = View.GONE
                    return
                }
                val matches = enrolledTournaments.filter { it.name.contains(query, ignoreCase = true) }
                searchAdapter.submitList(matches)
                binding.searchResults.searchResultsList.visibility =
                    if (matches.isEmpty()) View.GONE else View.VISIBLE
                binding.noResultsText.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE
            }
        })
    }

    private fun emptyStateTextFor(tabPosition: Int): Int = when (tabPosition) {
        1 -> R.string.empty_state_matches_not_going
        2 -> R.string.empty_state_matches_pending
        else -> R.string.empty_state_matches_going
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
