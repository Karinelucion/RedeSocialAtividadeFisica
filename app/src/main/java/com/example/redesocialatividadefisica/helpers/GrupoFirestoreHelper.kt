package com.example.redesocialatividadefisica.helpers

import android.util.Log
import com.example.redesocialatividadefisica.model.Grupo
import com.example.redesocialatividadefisica.model.RankingItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object GrupoFirestoreHelper {
    private val firestore = FirebaseFirestore.getInstance()

    fun criarGrupo(
        nomeGrupo: String,
        criador: FirebaseUser,
        membrosSelecionados: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val membros = membrosSelecionados.toMutableSet().apply { add(criador.uid) }.toList()

        val grupo = hashMapOf(
            "nome" to nomeGrupo,
            "criadorUid" to criador.uid,
            "membros" to membros,
            "criadoEm" to Timestamp.now()
        )

        firestore.collection("grupos")
            .add(grupo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun atualizarGrupo(
        grupoId: String,
        novoNome: String? = null,
        novaListaMembros: List<String>? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updates = mutableMapOf<String, Any>()
        if (!novoNome.isNullOrBlank()) updates["nome"] = novoNome
        if (!novaListaMembros.isNullOrEmpty()) updates["membros"] = novaListaMembros

        if (updates.isEmpty()) {
            onSuccess()
            return
        }

        firestore.collection("grupos")
            .document(grupoId)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun excluirGrupo(
        grupoId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("grupos")
            .document(grupoId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun buscarGruposDoUsuario(
        uidUsuario: String,
        onResult: (List<Map<String, Any>>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("grupos")
            .whereArrayContains("membros", uidUsuario)
            .get()
            .addOnSuccessListener { result ->
                val grupos = result.map { it.data + mapOf("id" to it.id) }
                onResult(grupos)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun buscarTodosGrupos(
        onResult: (List<Map<String, Any>>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("grupos")
            .get()
            .addOnSuccessListener { result ->
                val grupos = result.map { it.data + mapOf("id" to it.id) }
                onResult(grupos)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun buscarGrupo(
        grupoId: String,
        onResult: (Grupo) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        firestore.collection("grupos")
            .document(grupoId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.data!!
                    val grupo = Grupo(
                        id = document.id,
                        nome = data["nome"] as? String ?: "",
                        criadorUid = data["criadorUid"] as? String ?: "",
                        membros = data["membros"] as? List<String> ?: emptyList(),
                        criadoEm = data["criadoEm"] as? Timestamp ?: Timestamp.now()
                    )
                    onResult(grupo)
                } else {
                    onFailure(Exception("Grupo não encontrado"))
                }
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }


}