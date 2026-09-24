package com.misw.sportalarmist.data

/**
 * No hay backend de partidos: por cada torneo en el que el usuario se
 * inscribió se genera un único partido fijo contra el otro equipo, con
 * fecha/hora fijas, solo para poder probar el flujo de asistencia.
 */
class MatchRepository(private val enrollmentStore: EnrollmentStore) {

    fun getAll(): List<Match> =
        enrollmentStore.enrolledTournamentIds().mapNotNull { tournamentId ->
            enrollmentStore.enrolledTeamId(tournamentId)?.let { teamId ->
                forEnrollment(tournamentId, teamId)
            }
        }

    fun forEnrollment(tournamentId: Int, teamId: String): Match {
        val opponentId = Teams.ALL.first { it.id != teamId }.id
        return Match(
            id = tournamentId,
            tournamentId = tournamentId,
            homeTeamId = teamId,
            awayTeamId = opponentId,
            date = FIXED_DATE,
            time = FIXED_TIME
        )
    }

    private companion object {
        const val FIXED_DATE = "12-07-2026"
        const val FIXED_TIME = "07:30 PM"
    }
}
