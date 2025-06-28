package com.example.redesocialatividadefisica.model

import com.google.firebase.Timestamp

data class Atividade(
    val datahora: Timestamp = Timestamp.now(),
    val nivelAceleracaoMedia: Float = 0.0F
)
