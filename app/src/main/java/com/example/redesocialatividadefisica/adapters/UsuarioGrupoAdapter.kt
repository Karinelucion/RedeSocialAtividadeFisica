package com.example.redesocialatividadefisica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialatividadefisica.R
import com.example.redesocialatividadefisica.model.Usuario

class UsuarioGrupoAdapter(
    private val usuarios: List<Usuario>,
    private val selecionados: MutableList<String>
) : RecyclerView.Adapter<UsuarioGrupoAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkUsuario: CheckBox = view.findViewById(R.id.checkUsuario)
        val txtNome: TextView = view.findViewById(R.id.txtNomeUsuario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pessoa_multiselect, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.txtNome.text = usuario.nome ?: "(sem nome)"
        holder.checkUsuario.setOnCheckedChangeListener(null) // evita comportamento estranho ao reciclar
        holder.checkUsuario.isChecked = selecionados.contains(usuario.uid)

        holder.checkUsuario.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selecionados.contains(usuario.uid)) {
                    selecionados.add(usuario.uid)
                }
            } else {
                selecionados.remove(usuario.uid)
            }
        }
    }

    override fun getItemCount(): Int = usuarios.size
}
