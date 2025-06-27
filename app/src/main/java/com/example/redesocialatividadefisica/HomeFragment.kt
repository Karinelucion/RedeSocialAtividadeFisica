package com.example.redesocialatividadefisica

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.example.redesocialatividadefisica.viewmodel.SensorViewModel
import com.example.redesocialatividadefisica.databinding.FragmentHomeBinding
import com.example.redesocialatividadefisica.service.SensorThreadService
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: SensorViewModel
    private var service: SensorThreadService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as SensorThreadService.LocalBinder
            service = localBinder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(requireActivity()).get(SensorViewModel::class.java)

        setupObservers()
        setupButton()
    }

    private fun setupObservers() {
        viewModel.currentValue.observe(viewLifecycleOwner, Observer { value ->
            binding.txtNivelMovimento.text = "Nível de movimento: %.2f".format(Locale.getDefault(), value)
        })

        viewModel.finalAverage.observe(viewLifecycleOwner, Observer { average ->
            binding.txtNivelMovimento.text = "Média total: %.2f".format(Locale.getDefault(), average)
        })
    }

    private fun setupButton() {
        binding.btnMonitor.setOnClickListener {
            if (isBound) {
                stopMonitoring()
            } else {
                startMonitoring()
            }
        }
    }

    private fun startMonitoring() {
        ContextCompat.startForegroundService(
            requireContext(),
            Intent(requireContext(), SensorThreadService::class.java)
        )

        requireActivity().bindService(
            Intent(requireContext(), SensorThreadService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

        binding.btnMonitor.text = "Parar Monitoramento"
    }

    private fun stopMonitoring() {
        if (isBound) {
            viewModel.setFinalAverage(service?.getFinalAverage() ?: 0f)
            requireActivity().unbindService(connection)
            isBound = false
        }

        requireActivity().stopService(
            Intent(requireContext(), SensorThreadService::class.java)
        )

        binding.btnMonitor.text = "Iniciar Monitoramento"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isBound) {
            requireActivity().unbindService(connection)
        }
    }
}
