package com.example.redesocialatividadefisica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialatividadefisica.R
import com.example.redesocialatividadefisica.model.Atividade
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AtividadeHistoricoAdapter(private val lista: List<Atividade>) :
    RecyclerView.Adapter<AtividadeHistoricoAdapter.AtividadeViewHolder>() {

    class AtividadeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtData: TextView = itemView.findViewById(R.id.txtData)
        val txtValor: TextView = itemView.findViewById(R.id.txtValor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtividadeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historico, parent, false)
        return AtividadeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AtividadeViewHolder, position: Int) {
        val atividade = lista[position]

        // Formatando a data do Timestamp
        val data = atividade.datahora.toDate()
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        formato.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        holder.txtData.text = formato.format(data)

        // Formatando a aceleração média
        val valorFormatado = String.format("%.2f m/s²", atividade.nivelAceleracaoMedia)
        holder.txtValor.text = valorFormatado
    }

    override fun getItemCount(): Int = lista.size

}