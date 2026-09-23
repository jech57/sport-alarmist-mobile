package com.misw.sportalarmist.ui.tournament

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
