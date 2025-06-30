package com.example.redesocialatividadefisica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialatividadefisica.R
import com.example.redesocialatividadefisica.model.Grupo

class GrupoAdapter(
    private val grupos: List<Grupo>,
    private val onVisualizarClick: (Grupo) -> Unit,
    private val onEditarClick: (Grupo) -> Unit,
    private val onExcluirClick: (Grupo) -> Unit
) : RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder>() {

    inner class GrupoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNomeGrupo: TextView = view.findViewById(R.id.txtNomeGrupo)
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnExcluir: ImageButton = view.findViewById(R.id.btnExcluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grupo, parent, false)
        return GrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
        val grupo = grupos[position]
        holder.txtNomeGrupo.text = grupo.nome

        holder.txtNomeGrupo.setOnClickListener { onVisualizarClick(grupo) }
        holder.btnEditar.setOnClickListener { onEditarClick(grupo) }
        holder.btnExcluir.setOnClickListener { onExcluirClick(grupo) }
    }

    override fun getItemCount(): Int = grupos.size
}
