package com.example.redesocialatividadefisica.model

import com.google.firebase.Timestamp

data class Grupo(
    val id: String = "",
    val nome: String = "",
    val criadorUid: String = "",
    val membros: List<String> = listOf(),
    val criadoEm: Timestamp = Timestamp.now()
)
