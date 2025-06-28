package com.example.redesocialatividadefisica

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GruposFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grupos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnNovoGrupo = view.findViewById<FloatingActionButton>(R.id.btnNovoGrupo)
        btnNovoGrupo.setOnClickListener {
            startActivity(Intent(requireContext(), CadastroGrupoActivity::class.java))
        }
    }
}
