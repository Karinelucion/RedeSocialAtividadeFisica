package com.example.redesocialatividadefisica

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _view: View? = null
    private val viewBinding get() = _view!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout do fragmento
        _view = inflater.inflate(R.layout.fragment_home, container, false)
        return viewBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnMonitor = viewBinding.findViewById<Button>(R.id.btnMonitor)
        btnMonitor.setOnClickListener {
            // TODO: implementar lógica de iniciar monitoramento
        }

        val recycler = viewBinding.findViewById<RecyclerView>(R.id.recyclerHistorico)
        // TODO: configurar adapter e layout manager do recycler
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _view = null // evita memory leak
    }
}
