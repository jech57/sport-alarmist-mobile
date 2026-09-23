package com.misw.sportalarmist.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.misw.sportalarmist.R

/** Adapter genérico para los dropdowns de búsqueda (torneos, equipos, etc.). */
class SearchResultAdapter<T>(
    private val label: (T) -> String,
    private val onClick: (T) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private var items: List<T> = emptyList()

    fun submitList(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.label.text = label(item)
        holder.label.setOnClickListener { onClick(item) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view as TextView
    }
}
