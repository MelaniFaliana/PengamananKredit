package com.example.pengamanankredit.bottomnav.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pengamanankredit.R

class NasabahAdapter(
    private val list: List<NasabahData>,
    private val onItemClick: (NasabahData) -> Unit
) : RecyclerView.Adapter<NasabahAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama = itemView.findViewById<TextView>(R.id.tvNamaItem)
        val noRek = itemView.findViewById<TextView>(R.id.tvNoRekItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nasabah, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.nama.text = data.nama
        holder.noRek.text = data.noRekening
        holder.itemView.setOnClickListener {
            onItemClick(data)
        }
    }
}
