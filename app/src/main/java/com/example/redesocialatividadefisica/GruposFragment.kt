package com.example.redesocialatividadefisica

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialatividadefisica.adapters.GrupoAdapter
import com.example.redesocialatividadefisica.helpers.GrupoFirestoreHelper
import com.example.redesocialatividadefisica.model.Grupo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class GruposFragment : Fragment() {

    private lateinit var recyclerGrupos: RecyclerView
    private lateinit var adapter: GrupoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carregarGrupos()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        carregarGrupos()
        return inflater.inflate(R.layout.fragment_grupos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerGrupos = view.findViewById(R.id.recyclerGrupos)
        recyclerGrupos.layoutManager = LinearLayoutManager(requireContext())

        val btnNovoGrupo = view.findViewById<FloatingActionButton>(R.id.btnNovoGrupo)
        btnNovoGrupo.setOnClickListener {
            startActivity(Intent(requireContext(), CadastroGrupoActivity::class.java))
        }

        carregarGrupos()
    }

    private fun carregarGrupos() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        GrupoFirestoreHelper.buscarGruposDoUsuario(
            uidUsuario = uid,
            onResult = { gruposMapList ->
                val grupos = gruposMapList.mapNotNull { mapa ->
                    try {
                        Grupo(
                            id = mapa["id"] as? String ?: "",
                            nome = mapa["nome"] as? String ?: "",
                            membros = mapa["membros"] as? List<String> ?: emptyList()
                        )
                    } catch (e: Exception) {
                        null // Ignora entradas inválidas
                    }
                }

                adapter = GrupoAdapter(
                    grupos = grupos,
                    onVisualizarClick = { grupo -> abrirRankingGrupo(grupo)},
                    onEditarClick = { grupo -> abrirEdicaoGrupo(grupo) },
                    onExcluirClick = { grupo -> excluirGrupo(grupo) }
                )
                recyclerGrupos.adapter = adapter
            }
            ,
            onFailure = {
                Toast.makeText(requireContext(), "Erro ao carregar grupos", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun abrirRankingGrupo(grupo: Grupo) {
        val intent = Intent(requireContext(), RankingGrupoActivity::class.java)
        intent.putExtra("grupoId", grupo.id)
        startActivity(intent)
    }

    private fun abrirEdicaoGrupo(grupo: Grupo) {
        val intent = Intent(requireContext(), CadastroGrupoActivity::class.java)
        intent.putExtra("grupoId", grupo.id) // passa id do grupo para editar
        startActivity(intent)
    }

    private fun excluirGrupo(grupo: Grupo) {
        GrupoFirestoreHelper.excluirGrupo(
            grupoId = grupo.id,
            onSuccess = {
                Toast.makeText(requireContext(), "Grupo excluído", Toast.LENGTH_SHORT).show()
                carregarGrupos() // recarrega lista
            },
            onFailure = {
                Toast.makeText(requireContext(), "Erro ao excluir grupo", Toast.LENGTH_SHORT).show()
            }
        )
    }
}