package com.example.lab_supply_app.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ConnectionsViewModel(application: Application) : AndroidViewModel(application) {
    val bluetoothState : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val BLEDevices : MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())

}
