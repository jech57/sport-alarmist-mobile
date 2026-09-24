package com.misw.sportalarmist.ui.matches

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
import com.google.android.material.tabs.TabLayout
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.AttendanceStatus
import com.misw.sportalarmist.data.AttendanceStore
import com.misw.sportalarmist.data.Match
import com.misw.sportalarmist.data.MatchRepository
import com.misw.sportalarmist.data.MatchSchedule
import com.misw.sportalarmist.data.EnrollmentStore
import com.misw.sportalarmist.data.Tournament
import com.misw.sportalarmist.data.TournamentRepository
import com.misw.sportalarmist.databinding.FragmentMatchesBinding

class MatchesFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    private lateinit var attendanceStore: AttendanceStore
    private lateinit var tournaments: List<Tournament>
    private var allMatches: List<Match> = emptyList()
    private lateinit var adapters: List<MatchListAdapter>
    private var searchQuery: String = ""

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

        attendanceStore = AttendanceStore(requireContext())
        tournaments = TournamentRepository(requireContext()).getAll()
        allMatches = MatchRepository(EnrollmentStore(requireContext())).getAll()

        adapters = listOf(
            MatchListAdapter(
                showPendingStatus = false,
                tournamentName = ::tournamentName,
                nextAlarm = ::nextAlarmText
            ) { goToAttendanceConfirmation(it) },
            MatchListAdapter(showPendingStatus = false, tournamentName = ::tournamentName) { goToAttendanceConfirmation(it) },
            MatchListAdapter(showPendingStatus = true, tournamentName = ::tournamentName) { goToAttendanceConfirmation(it) }
        )
        binding.matchesList.layoutManager = LinearLayoutManager(requireContext())
        binding.matchesTabs.getTabAt(PENDING_TAB_POSITION)?.setCustomView(R.layout.tab_pending)

        binding.matchesTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.emptyStateText.setText(emptyStateTextFor(tab.position))
                renderTab(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })

        setUpSearch()

        val filterTournament = arguments?.getString("filter_tournament")
        if (!filterTournament.isNullOrBlank()) {
            binding.searchBar.searchBarHint.setText(filterTournament)
        } else {
            renderTab(binding.matchesTabs.selectedTabPosition)
        }
    }

    private fun setUpSearch() {
        binding.searchBar.searchBarHint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString().orEmpty()
                renderTab(binding.matchesTabs.selectedTabPosition)
            }
        })
    }

    private fun renderTab(tabPosition: Int) {
        updatePendingDot()

        val status = statusFor(tabPosition)
        val tabMatches = allMatches.filter { attendanceStore.statusOf(it.id) == status }
        val filtered = if (searchQuery.isBlank()) {
            tabMatches
        } else {
            tabMatches.filter { tournamentName(it).contains(searchQuery, ignoreCase = true) }
        }

        val adapter = adapters[tabPosition]
        binding.matchesList.adapter = adapter
        adapter.submitList(filtered)

        val hasAnyMatch = tabMatches.isNotEmpty()
        binding.emptyStateText.visibility = if (hasAnyMatch) View.GONE else View.VISIBLE
        binding.matchesList.visibility = if (hasAnyMatch) View.VISIBLE else View.GONE
        binding.noResultsText.visibility =
            if (hasAnyMatch && filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    /**
     * Punto amarillo de "hay partidos pendientes": a la derecha del texto de la pestaña
     * "Pendientes" y en la esquina del calendario de la barra inferior (activity_main).
     */
    private fun updatePendingDot() {
        val hasPending = allMatches.any { attendanceStore.statusOf(it.id) == AttendanceStatus.PENDING }
        val visibility = if (hasPending) View.VISIBLE else View.GONE
        binding.matchesTabs.getTabAt(PENDING_TAB_POSITION)?.customView
            ?.findViewById<View>(R.id.tabPendingDot)?.visibility = visibility
        requireActivity().findViewById<View>(R.id.tabPartidosPendingDot)?.visibility = visibility
    }

    private fun tournamentName(match: Match): String =
        tournaments.firstOrNull { it.id == match.tournamentId }?.name.orEmpty()

    /** "Próxima alarma: 11-07-26 07:30 PM", o null si no va, no tiene recordatorios o ya pasaron. */
    private fun nextAlarmText(match: Match): String? {
        if (attendanceStore.statusOf(match.id) != AttendanceStatus.GOING) return null
        val alarm = MatchSchedule.nextAlarm(match, attendanceStore.remindersOf(match.id)) ?: return null
        return getString(R.string.next_alarm, MatchSchedule.formatAlarm(alarm))
    }

    private fun statusFor(tabPosition: Int): AttendanceStatus = when (tabPosition) {
        1 -> AttendanceStatus.NOT_GOING
        PENDING_TAB_POSITION -> AttendanceStatus.PENDING
        else -> AttendanceStatus.GOING
    }

    private fun emptyStateTextFor(tabPosition: Int): Int = when (tabPosition) {
        1 -> R.string.empty_state_matches_not_going
        2 -> R.string.empty_state_matches_pending
        else -> R.string.empty_state_matches_going
    }

    private fun goToAttendanceConfirmation(match: Match) {
        findNavController().navigate(
            R.id.action_matchesFragment_to_attendanceConfirmationFragment,
            bundleOf("tournament_id" to match.tournamentId)
        )
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) renderTab(binding.matchesTabs.selectedTabPosition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val PENDING_TAB_POSITION = 2
    }
}