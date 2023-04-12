package com.example.lab_supply_app.models

import android.app.Application
import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData

class ConnectionsViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var bluetoothState : LiveData<Boolean>

    fun setBTState(bluetoothAdapter: BluetoothAdapter)
    {
        bluetoothState = liveData { bluetoothAdapter.isEnabled }
    }
}
