package com.misw.sportalarmist.data

import android.content.Context
import org.json.JSONArray

/**
 * Lista estática de torneos, empacada como asset (`tournaments.json`) en vez
 * de venir de un backend real — no hay conexión a base de datos en esta app.
 */
class TournamentRepository(private val context: Context) {

    fun getAll(): List<Tournament> {
        val json = context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        return (0 until array.length()).map { index ->
            val entry = array.getJSONObject(index)
            Tournament(id = entry.getInt("id"), name = entry.getString("name"))
        }
    }

    private companion object {
        const val ASSET_NAME = "tournaments.json"
    }
}
