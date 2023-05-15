package com.example.lab_supply_app.models

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ConnectionsViewModel(application: Application) : AndroidViewModel(application) {
    val bluetoothState : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val BLEDevices : MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())

//    fun setBTState(bluetoothAdapter: BluetoothAdapter)
//    {
//        bluetoothState = mutableLiveData<Boolean> { bluetoothAdapter.isEnabled }
//    }
}
