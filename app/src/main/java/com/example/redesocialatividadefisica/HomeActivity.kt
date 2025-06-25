package com.example.redesocialatividadefisica

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar = findViewById(R.id.toolbarGlobal)
        setSupportActionBar(toolbar)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Inicialmente mostra HomeFragment
        if (savedInstanceState == null) {
            openFragment(HomeFragment(), "Olá, ${getFirstName()}!")
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> openFragment(HomeFragment(), "Olá, ${getFirstName()}!")
                R.id.ranking -> openFragment(RankingFragment(), "Ranking de usuários")
                R.id.grupos -> openFragment(GruposFragment(), "Grupos")
            }
            true
        }
    }

    private fun openFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        supportActionBar?.title = title
    }

    private fun getFirstName(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName?.split(" ")?.firstOrNull() ?: "usuário"
    }
}
