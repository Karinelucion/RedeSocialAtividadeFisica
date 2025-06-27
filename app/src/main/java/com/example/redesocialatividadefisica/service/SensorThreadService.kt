package com.example.redesocialatividadefisica.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.redesocialatividadefisica.R
import kotlin.math.pow
import kotlin.math.sqrt


class SensorThreadService : Service(), SensorEventListener {
    inner class LocalBinder : Binder() {
        fun getService(): SensorThreadService = this@SensorThreadService
    }
    private lateinit var sensorManager: SensorManager
    private val _sensorData = MutableLiveData<Float>()
    private val collectedValues = mutableListOf<Float>()
    private val lock = Any()
    private var sum = 0.0
    private var count = 0
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService()
        } else {
            startForeground(1, Notification())
        }
        startSensorCollection()
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                "sensor_channel",
                "Monitoramento de Movimento",
                NotificationManager.IMPORTANCE_LOW
            ).also { channel ->
                (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(channel)
            }
        }

        val notification = NotificationCompat.Builder(this, "sensor_channel")
            .setContentTitle("")
            .setContentText("")
            .setSmallIcon(android.R.color.transparent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        startForeground(1, notification)
    }

    private fun startSensorCollection() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val magnitude = sqrt(it.values[0].pow(2) + it.values[1].pow(2) + it.values[2].pow(2))

            synchronized(lock) {
                sum += magnitude
                count++
                collectedValues.add(magnitude.toFloat())
            }

            _sensorData.postValue(magnitude.toFloat())
        }
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(this)
        stopSelf()
    }

    fun getFinalAverage(): Float = synchronized(lock) {
        if (count == 0) 0f else (sum / count).toFloat()
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}