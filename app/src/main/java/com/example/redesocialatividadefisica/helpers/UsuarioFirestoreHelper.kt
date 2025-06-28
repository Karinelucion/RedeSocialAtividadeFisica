package com.example.redesocialatividadefisica.helpers

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

object UsuarioFirestoreHelper {

    private val firestore = FirebaseFirestore.getInstance()

    fun salvarUsuario(user: FirebaseUser, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}, onAlreadyExists: () -> Unit = {}) {
        val docRef = firestore.collection("usuarios").document(user.uid)

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                onAlreadyExists()
            } else {
                val usuario = hashMapOf(
                    "uid" to user.uid,
                    "nome" to user.displayName,
                    "email" to user.email,
                    "criadoEm" to Timestamp.now()
                )
                docRef.set(usuario)
                    .addOnSuccessListener {
                        onSuccess()
                        Log.d("Firestore", "Usuário cadastrado com sucesso")
                    }
                    .addOnFailureListener {
                        e -> onFailure(e)
                        Log.e("Firestore", "Erro ao cadastrar usuário", e)
                    }
            }
        }
    }

    fun buscarUsuario(uid: String, onComplete: (Map<String, Any>?) -> Unit) {
        firestore.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                onComplete(document.data)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }
}