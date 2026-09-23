package com.misw.sportalarmist.data

data class Team(
    val id: String,
    val name: String
)

/** Equipos estáticos (no varían por torneo, así que no van en un JSON). */
object Teams {
    val ALL = listOf(
        Team(id = "triangulo", name = "Triángulo"),
        Team(id = "estrella", name = "Estrella")
    )
}
