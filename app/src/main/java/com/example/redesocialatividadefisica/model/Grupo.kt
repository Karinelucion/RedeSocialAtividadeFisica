package com.example.redesocialatividadefisica.model

data class Grupo(
    val id: String,
    val nome: String,
    val usuarios: List<Usuario>
)
