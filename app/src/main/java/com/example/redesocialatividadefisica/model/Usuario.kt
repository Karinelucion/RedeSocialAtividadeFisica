package com.example.redesocialatividadefisica.model

import com.google.firebase.Timestamp

data class Usuario (
    val uid: String = "",
    val nome: String? = "",
    val email: String = "",
    val criadoEm: Timestamp = Timestamp.now()
)