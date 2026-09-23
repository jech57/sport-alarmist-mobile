package com.misw.sportalarmist.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.Teams
import com.misw.sportalarmist.data.Tournament

class TournamentListAdapter(
    private val enrolledTeamId: (Tournament) -> String?,
    private val onClick: (Tournament) -> Unit
) : RecyclerView.Adapter<TournamentListAdapter.ViewHolder>() {

    private var items: List<Tournament> = emptyList()

    fun submitList(newItems: List<Tournament>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tournament_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tournament = items[position]
        holder.name.text = tournament.name

        val teamId = enrolledTeamId(tournament)
        holder.status.text = if (teamId != null) {
            val teamName = Teams.ALL.firstOrNull { it.id == teamId }?.name.orEmpty()
            holder.status.context.getString(R.string.tournament_status_registered_team, teamName)
        } else {
            holder.status.context.getString(R.string.tournament_status_not_registered)
        }

        holder.itemView.setOnClickListener { onClick(tournament) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tournamentName)
        val status: TextView = view.findViewById(R.id.tournamentStatus)
    }
}
