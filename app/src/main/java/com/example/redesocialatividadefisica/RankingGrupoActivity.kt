package com.example.redesocialatividadefisica

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialatividadefisica.adapters.RankingItemAdapter
import com.example.redesocialatividadefisica.helpers.AtividadeFirestoreHelper

class RankingGrupoActivity : AppCompatActivity() {
    private lateinit var recyclerRanking: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking_grupo)

        val toolbar = findViewById<Toolbar>(R.id.toolbarGlobal)
        setSupportActionBar(toolbar)

        // Habilita o botão "up" (seta)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Listener para clicar na seta de voltar
        toolbar.setNavigationOnClickListener {
            finish()  // ou finish()
        }

        recyclerRanking = findViewById(R.id.recyclerRanking)
        recyclerRanking.layoutManager = LinearLayoutManager(this)

        val grupoId = intent.getStringExtra("grupoId") ?: return

        AtividadeFirestoreHelper.buscarRankingDoGrupo(
            grupoId = grupoId,
            onResult = { rankingList ->
                val adapter = RankingItemAdapter(rankingList)
                recyclerRanking.adapter = adapter
            },
            onFailure = {
                Toast.makeText(this, "Erro ao carregar ranking", Toast.LENGTH_SHORT).show()
            }
        )
    }
}