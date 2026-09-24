package com.misw.sportalarmist.data

data class Match(
    val id: Int,
    val tournamentId: Int,
    val homeTeamId: String,
    val awayTeamId: String,
    val date: String,
    val time: String
)
