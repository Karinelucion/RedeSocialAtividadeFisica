package com.example.redesocialatividadefisica

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
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
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: SensorViewModel
    private var service: SensorThreadService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as SensorThreadService.LocalBinder
            service = localBinder.getService()
//            isBound = true

            service?.sensorLiveData?.observe(viewLifecycleOwner) { value ->
                if (isBound) {
                    binding.txtNivelMovimento.text = "Nível de movimento: %.2f".format(value)
                }
            }
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(requireActivity()).get(SensorViewModel::class.java)

        binding.btnMonitor.setOnClickListener {
            if (isBound) {
                stopMonitoring()
            } else {
                startMonitoring()
            }
        }
    }


    private fun startMonitoring() {
        val serviceIntent = Intent(requireContext(), SensorThreadService::class.java)
        isBound = true
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
        requireActivity().bindService(
            serviceIntent,
            connection,
            Context.BIND_AUTO_CREATE
        )
        binding.btnMonitor.text = "Parar Monitoramento"
    }

    private fun stopMonitoring() {
        try {
            if (isBound) {
                requireActivity().unbindService(connection)
                isBound = false
            }

            val stopIntent = Intent(requireContext(), SensorThreadService::class.java)
            requireActivity().stopService(stopIntent)

            binding.btnMonitor.text = "Iniciar Monitoramento"
            val average = service?.getFinalAverage() ?: 0f
            binding.txtNivelMovimento.text = "Nível de movimento médio: %.2f".format(average)
        } catch (e: Exception) {
            Log.e("HomeFragment", "Erro ao parar monitoramento", e)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isBound) {
            requireActivity().unbindService(connection)
        }
    }
}
