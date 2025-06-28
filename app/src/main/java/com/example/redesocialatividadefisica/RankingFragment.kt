package com.example.redesocialatividadefisica

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redesocialatividadefisica.adapters.RankingItemAdapter
import com.example.redesocialatividadefisica.databinding.FragmentHomeBinding
import com.example.redesocialatividadefisica.databinding.FragmentRankingBinding
import com.example.redesocialatividadefisica.helpers.AtividadeFirestoreHelper

class RankingFragment : Fragment() {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerRanking.layoutManager = LinearLayoutManager(requireContext())

        AtividadeFirestoreHelper.buscarRanking(
            onResult = { ranking ->
                binding.recyclerRanking.adapter = RankingItemAdapter(ranking)
            },
            onFailure = {
                Toast.makeText(requireContext(), "Erro ao carregar ranking", Toast.LENGTH_SHORT).show()
            }
        )
    }

}
