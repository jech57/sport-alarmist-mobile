package com.misw.sportalarmist.ui.matches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.Match
import com.misw.sportalarmist.data.Teams

class MatchListAdapter(
    private val showPendingStatus: Boolean,
    private val tournamentName: (Match) -> String,
    /** Texto de "Próxima alarma: …", o null para ocultar la fila. */
    private val nextAlarm: (Match) -> String? = { null },
    private val onClick: (Match) -> Unit
) : RecyclerView.Adapter<MatchListAdapter.ViewHolder>() {

    private var items: List<Match> = emptyList()

    fun submitList(newItems: List<Match>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_match_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = items[position]
        holder.tournamentLabel.text = tournamentName(match)
        val homeTeam = Teams.ALL.firstOrNull { it.id == match.homeTeamId }
        val awayTeam = Teams.ALL.firstOrNull { it.id == match.awayTeamId }

        holder.homeTeamIcon.setImageResource(badgeFor(match.homeTeamId))
        holder.homeTeamName.text = homeTeam?.name.orEmpty()
        holder.awayTeamIcon.setImageResource(badgeFor(match.awayTeamId))
        holder.awayTeamName.text = awayTeam?.name.orEmpty()
        holder.matchDate.text = match.date
        holder.matchTime.text = match.time
        holder.statusRow.visibility = if (showPendingStatus) View.VISIBLE else View.GONE

        val alarmText = nextAlarm(match)
        holder.alarmRow.visibility = if (alarmText != null) View.VISIBLE else View.GONE
        holder.alarmText.text = alarmText.orEmpty()

        holder.itemView.setOnClickListener { onClick(match) }
    }

    private fun badgeFor(teamId: String) =
        if (teamId == TEAM_ESTRELLA) R.drawable.ic_team_badge_estrella else R.drawable.ic_team_badge_triangulo

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tournamentLabel: TextView = view.findViewById(R.id.tournamentLabel)
        val homeTeamIcon: ImageView = view.findViewById(R.id.homeTeamIcon)
        val homeTeamName: TextView = view.findViewById(R.id.homeTeamName)
        val awayTeamIcon: ImageView = view.findViewById(R.id.awayTeamIcon)
        val awayTeamName: TextView = view.findViewById(R.id.awayTeamName)
        val matchDate: TextView = view.findViewById(R.id.matchDate)
        val matchTime: TextView = view.findViewById(R.id.matchTime)
        val statusRow: LinearLayout = view.findViewById(R.id.statusRow)
        val alarmRow: LinearLayout = view.findViewById(R.id.alarmRow)
        val alarmText: TextView = view.findViewById(R.id.alarmText)
    }

    private companion object {
        const val TEAM_ESTRELLA = "estrella"
    }
}