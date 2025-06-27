package com.example.redesocialatividadefisica

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(), SensorEventListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var sensorManager: SensorManager

    private var _view: View? = null
    private val viewBinding get() = _view!!
    private var isMonitoring = false
    private val collectedValues = mutableListOf<Float>()

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
            if (!isMonitoring) {
                isMonitoring = true
                collectedValues.clear()
                sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
                val mAcel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

                sensorManager.registerListener(this, mAcel, SensorManager.SENSOR_DELAY_NORMAL)

                viewBinding.findViewById<Button>(R.id.btnMonitor).text = "Parar Monitoramento"
            }else{
                isMonitoring = false
                sensorManager.unregisterListener(this)
                viewBinding.findViewById<Button>(R.id.btnMonitor).text = "Iniciar Monitoramento"
            }
        }

        val recycler = viewBinding.findViewById<RecyclerView>(R.id.recyclerHistorico)
        // TODO: configurar adapter e layout manager do recycler
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _view = null // evita memory leak
    }

    override fun onSensorChanged(sensors: SensorEvent?) {
        val x = sensors?.values?.get(0)
        val y = sensors?.values?.get(1)
        val z = sensors?.values?.get(2)

        val media = (x!! + y!! +z!!) /3
        collectedValues.add(media)
        val mediaformatada = String.format("%.2f", media)

        viewBinding.findViewById<TextView>(R.id.txtNivelMovimento).text = "Nível de movimento: $mediaformatada"
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // TODO: Aqui não precisa implementar nada
    }
}
