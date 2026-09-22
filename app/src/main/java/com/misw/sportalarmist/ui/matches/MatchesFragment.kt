package com.misw.sportalarmist.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.misw.sportalarmist.R
import com.misw.sportalarmist.databinding.FragmentMatchesBinding

class MatchesFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

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
