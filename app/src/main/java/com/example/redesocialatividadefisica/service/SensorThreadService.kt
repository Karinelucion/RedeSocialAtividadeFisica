package com.example.redesocialatividadefisica.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.redesocialatividadefisica.R
import kotlin.math.pow
import kotlin.math.sqrt


class SensorThreadService : Service(), SensorEventListener {
    // Binder para comunicação
    inner class LocalBinder : Binder() {
        fun getService(): SensorThreadService = this@SensorThreadService
    }

    private val binder = LocalBinder()
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // LiveData para transmissão dos valores
    private val _sensorLiveData = MutableLiveData<Float>()
    val sensorLiveData: LiveData<Float> get() = _sensorLiveData

    // Dados para cálculo da média
    private val collectedValues = mutableListOf<Float>()
    private val lock = Any()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        startForegroundService() // DEVE ser a PRIMEIRA chamada
        initSensor()
    }

    private fun startForegroundService() {
        try {
            // 1. Crie o canal (Android 8+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    "sensor_channel",
                    "Monitoramento",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                        .createNotificationChannel(this)
                }
            }

            // 2. Crie a notificação MÍNIMA
            val notification = NotificationCompat.Builder(this, "sensor_channel")
                .setContentTitle("Monitoramento Ativo")
                .setContentText("Coletando dados de movimento")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // ÍCONE OBRIGATÓRIO!
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            // 3. Inicie como foreground service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(1, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            } else {
                startForeground(1, notification)
            }
        } catch (e: Exception) {
            Log.e("SensorService", "Erro no foreground", e)
            stopSelf() // Encerra se falhar
        }
    }

    private fun initSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: run {
            stopSelf() // Encerra se não houver sensor
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Garante que o serviço reinicie se for encerrado
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]
            val magnitude = sqrt(x * x + y * y + z * z.toDouble()).toFloat()

            // Atualiza LiveData
            _sensorLiveData.postValue(magnitude)

            // Armazena para cálculo da média
            synchronized(lock) {
                collectedValues.add(magnitude)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // TODO : Nao é necessario implemetação
    }

    fun getFinalAverage(): Float {
        return synchronized(lock) {
            if (collectedValues.isEmpty()) 0f else collectedValues.average().toFloat()
        }
    }

    override fun onDestroy() {
        try {
            // 1. Pare a coleta de dados do sensor
            sensorManager.unregisterListener(this)


            // 3. Pare completamente o serviço
            stopSelf()

        } catch (e: Exception) {
            Log.e("SensorService", "Erro ao destruir serviço", e)
        } finally {
            super.onDestroy()
        }
    }
}