package com.example.redesocialatividadefisica

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialatividadefisica.adapters.UsuarioGrupoAdapter
import com.example.redesocialatividadefisica.helpers.GrupoFirestoreHelper
import com.example.redesocialatividadefisica.model.Usuario
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroGrupoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val usuariosSelecionados = mutableListOf<String>() // UIDs dos membros

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_grupo)

        val toolbar = findViewById<Toolbar>(R.id.toolbarGlobal)
        setSupportActionBar(toolbar)

        // Habilita o botão "up" (seta)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Listener para clicar na seta de voltar
        toolbar.setNavigationOnClickListener {
            finish()  // ou finish()
        }

        auth = FirebaseAuth.getInstance()
        val uidAtual = auth.currentUser?.uid ?: return

        val grupoId = intent.getStringExtra("grupoId") // null se for cadastro
        val isEdicao = grupoId != null

        val edtNomeGrupo = findViewById<EditText>(R.id.edtNomeGrupo)
        val fabSalvarGrupo = findViewById<FloatingActionButton>(R.id.fabSalvarGrupo)
        val recycler = findViewById<RecyclerView>(R.id.recyclerPessoas)

        val usuarios = mutableListOf<Usuario>()
        val adapter = UsuarioGrupoAdapter(usuarios, usuariosSelecionados)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        if (isEdicao) {
            GrupoFirestoreHelper.buscarGrupo(
                grupoId = grupoId!!,
                onResult = { grupo ->
                    edtNomeGrupo.setText(grupo.nome)
                    usuariosSelecionados.clear()
                    usuariosSelecionados.addAll(grupo.membros)
                    adapter.notifyDataSetChanged()
                },
                onFailure = {
                    Toast.makeText(this, "Erro ao carregar grupo", Toast.LENGTH_SHORT).show()
                }
            )

        }

        FirebaseFirestore.getInstance().collection("usuarios").get().addOnSuccessListener { result ->
            usuarios.clear()
            for (doc in result) {
                val uid = doc.getString("uid") ?: continue
                if (uid == uidAtual) continue
                val nome = doc.getString("nome")
                usuarios.add(Usuario(uid, nome))
            }
            adapter.notifyDataSetChanged()
        }

        fabSalvarGrupo.setOnClickListener {
            val nomeGrupo = edtNomeGrupo.text.toString().trim()
            if (nomeGrupo.isEmpty()) {
                Toast.makeText(this, "Informe um nome para o grupo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuarioAtual = auth.currentUser ?: return@setOnClickListener
            if (!usuariosSelecionados.contains(usuarioAtual.uid)) {
                usuariosSelecionados.add(usuarioAtual.uid)
            }

            if (isEdicao) {
                GrupoFirestoreHelper.atualizarGrupo(
                    grupoId = grupoId!!,
                    novoNome = nomeGrupo,
                    novaListaMembros = usuariosSelecionados,
                    onSuccess = {
                        Toast.makeText(this, "Grupo atualizado!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    },
                    onFailure = {
                        Toast.makeText(this, "Erro ao atualizar grupo", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                GrupoFirestoreHelper.criarGrupo(
                    nomeGrupo = nomeGrupo,
                    criador = usuarioAtual,
                    membrosSelecionados = usuariosSelecionados,
                    onSuccess = {
                        Toast.makeText(this, "Grupo criado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onFailure = { e ->
                        Toast.makeText(this, "Erro ao criar grupo: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
