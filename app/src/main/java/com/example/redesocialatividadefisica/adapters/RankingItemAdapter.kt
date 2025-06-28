package com.example.redesocialatividadefisica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialatividadefisica.R
import com.example.redesocialatividadefisica.model.RankingItem

class RankingItemAdapter (private val lista: List<RankingItem>) :
    RecyclerView.Adapter<RankingItemAdapter.RankingViewHolder>() {

    class RankingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtPosicao: TextView = view.findViewById(R.id.txtPosicao)
        val txtNomeUsuario: TextView = view.findViewById(R.id.txtNomeUsuario)
        val txtRecorde: TextView = view.findViewById(R.id.txtRecorde)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return RankingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val item = lista[position]
        holder.txtPosicao.text = (position + 1).toString()
        holder.txtNomeUsuario.text = item.nome
        holder.txtRecorde.text = String.format("%.2f m/s²", item.aceleracaoMaxima)
    }

    override fun getItemCount(): Int = lista.size

}