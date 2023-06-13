package com.example.lab_supply_app.bluetooth

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow

object ConnectedDevice {
    val connectedDevice = MutableLiveData(Pair("", ""))
    val current = MutableStateFlow(0.0f)

    fun handleDeviceUpdate(name: String, address: String)
    {
        if (connectedDevice.value?.second == "" || address == connectedDevice.value?.second)
            connectedDevice.postValue(Pair(name, address))
    }

    fun reset()
    {
        connectedDevice.postValue(Pair("", ""))
    }

    fun getAddress(): String? {
        val address = connectedDevice.value?.second
        return if (address != "") address else null
    }
}