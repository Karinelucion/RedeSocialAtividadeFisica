package com.example.redesocialatividadefisica.helpers

import android.util.Log
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
}