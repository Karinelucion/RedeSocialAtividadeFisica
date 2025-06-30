package com.example.redesocialatividadefisica.helpers

import android.util.Log
import com.example.redesocialatividadefisica.model.RankingItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object AtividadeFirestoreHelper {

    private val firestore = FirebaseFirestore.getInstance()

    // Gravar nova atividade para o usuário
    fun salvarAtividade(
        user: FirebaseUser,
        nivelAceleracaoMedia: Float,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val atividade = hashMapOf(
            "datahora" to Timestamp.now(),
            "nivelAceleracaoMedia" to nivelAceleracaoMedia
        )

        firestore.collection("usuarios")
            .document(user.uid)
            .collection("atividades")
            .add(atividade)
            .addOnSuccessListener {
                Log.d("Firestore", "Atividade registrada com sucesso")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao salvar atividade", e)
                onFailure(e)
            }
    }

    // Recuperar todas as atividades do usuário
    fun buscarAtividades(
        uid: String,
        onResult: (List<Map<String, Any>>) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        firestore.collection("usuarios")
            .document(uid)
            .collection("atividades")
            .orderBy("datahora", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { it.data }
                onResult(lista)
                Log.d("Firestore", "Sucesso ao buscar atividades ${lista}")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Erro ao buscar atividades", it)
                onFailure(it)
            }
    }

    fun buscarRanking(
        onResult: (List<RankingItem>) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        val rankingList = mutableListOf<RankingItem>()

        firestore.collection("usuarios").get()
            .addOnSuccessListener { usuarios ->
                if (usuarios.isEmpty) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                var restantes = usuarios.size()

                for (usuarioDoc in usuarios) {
                    val uid = usuarioDoc.id
                    val nome = usuarioDoc.getString("nome") ?: "Usuário"

                    firestore.collection("usuarios")
                        .document(uid)
                        .collection("atividades")
                        .orderBy("nivelAceleracaoMedia", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { atividades ->
                            val top = atividades.firstOrNull()
                            val media = (top?.get("nivelAceleracaoMedia") as? Double)?.toFloat() ?: 0f

                            rankingList.add(RankingItem(nome, media))

                            restantes--
                            if (restantes == 0) {
                                val ordenado = rankingList.sortedByDescending { it.aceleracaoMaxima }
                                onResult(ordenado)
                            }
                        }
                        .addOnFailureListener {
                            restantes--
                            if (restantes == 0) {
                                val ordenado = rankingList.sortedByDescending { it.aceleracaoMaxima }
                                onResult(ordenado)
                            }
                        }
                }
            }
            .addOnFailureListener(onFailure)
    }

    fun buscarRankingDoGrupo(
        grupoId: String,
        onResult: (List<RankingItem>) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val rankingList = mutableListOf<RankingItem>()

        // Primeiro, buscar o grupo para obter a lista de membros
        firestore.collection("grupos")
            .document(grupoId)
            .get()
            .addOnSuccessListener { grupoDoc ->
                val membros = grupoDoc.get("membros") as? List<String> ?: emptyList()

                if (membros.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                var restantes = membros.size

                for (uid in membros) {
                    firestore.collection("usuarios")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { usuarioDoc ->
                            val nome = usuarioDoc.getString("nome") ?: "Usuário"

                            // Buscar a melhor atividade desse usuário
                            firestore.collection("usuarios")
                                .document(uid)
                                .collection("atividades")
                                .orderBy("nivelAceleracaoMedia", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnSuccessListener { atividades ->
                                    val top = atividades.firstOrNull()
                                    val media = (top?.get("nivelAceleracaoMedia") as? Double)?.toFloat() ?: 0f

                                    rankingList.add(RankingItem(nome, media))

                                    restantes--
                                    if (restantes == 0) {
                                        val ordenado = rankingList.sortedByDescending { it.aceleracaoMaxima }
                                        onResult(ordenado)
                                    }
                                }
                                .addOnFailureListener {
                                    restantes--
                                    if (restantes == 0) {
                                        val ordenado = rankingList.sortedByDescending { it.aceleracaoMaxima }
                                        onResult(ordenado)
                                    }
                                }

                        }
                        .addOnFailureListener {
                            restantes--
                            if (restantes == 0) {
                                val ordenado = rankingList.sortedByDescending { it.aceleracaoMaxima }
                                onResult(ordenado)
                            }
                        }
                }
            }
            .addOnFailureListener(onFailure)
    }

}