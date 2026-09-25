package com.misw.sportalarmist.data

/**
 * No hay backend de partidos: por cada torneo en el que el usuario se
 * inscribió se genera un único partido fijo contra el otro equipo, con
 * fecha/hora fijas, solo para poder probar el flujo de asistencia.
 *
 * Si se pasa un MatchChangeStore, se aplican los aplazamientos simulados
 * (fecha/hora nuevas) sobre la fecha/hora fijas.
 */
class MatchRepository(
    private val enrollmentStore: EnrollmentStore,
    private val changeStore: MatchChangeStore? = null
) {

    fun getAll(): List<Match> =
        enrollmentStore.enrolledTournamentIds().mapNotNull { tournamentId ->
            enrollmentStore.enrolledTeamId(tournamentId)?.let { teamId ->
                forEnrollment(tournamentId, teamId)
            }
        }

    fun forEnrollment(tournamentId: Int, teamId: String): Match {
        val opponentId = Teams.ALL.first { it.id != teamId }.id
        val schedule = changeStore?.scheduleOf(tournamentId)
        return Match(
            id = tournamentId,
            tournamentId = tournamentId,
            homeTeamId = teamId,
            awayTeamId = opponentId,
            date = schedule?.date ?: FIXED_DATE,
            time = schedule?.time ?: FIXED_TIME
        )
    }

    private companion object {
        const val FIXED_DATE = "12-11-2026"
        const val FIXED_TIME = "07:30 PM"
    }
}