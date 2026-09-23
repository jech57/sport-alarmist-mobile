package com.misw.sportalarmist.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.sportalarmist.R
import com.misw.sportalarmist.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        binding.searchBar.searchBarIcon.setImageResource(R.drawable.ic_search)
        binding.searchBar.searchBarHint.setText(R.string.hint_search_tournament)

        binding.tournamentItem.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_tournamentDetailFragment,
                bundleOf("tournament_id" to SAMPLE_TOURNAMENT_ID)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val SAMPLE_TOURNAMENT_ID = 1
    }
}
