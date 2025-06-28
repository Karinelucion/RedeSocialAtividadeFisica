package com.example.redesocialatividadefisica.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SensorViewModel : ViewModel() {
    private val _currentValue = MutableLiveData<Float>()
    val currentValue: LiveData<Float> get() = _currentValue

    // Armazena a média final
    private val _finalAverage = MutableLiveData<Float>()
    val finalAverage: LiveData<Float> get() = _finalAverage

    fun updateCurrentValue(value: Float) {
        _currentValue.postValue(value)
    }

    fun setFinalAverage(average: Float) {
        _finalAverage.postValue(average)
    }
}