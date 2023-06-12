package com.example.lab_supply_app.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow


class BleService(): Service() {

    private val binder = LocalBinder()
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    val connectMessage = MutableStateFlow("DISCONNECTED")

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService() : BleService {
            return this@BleService
        }
    }

    fun setAdapter(adapter: BluetoothAdapter?): Boolean {
        bluetoothAdapter = adapter
        return adapter != null
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            bluetoothGatt = gatt

            when (newState) {
                BluetoothProfile.STATE_CONNECTING -> connectMessage.value = "CONNECTING"

                BluetoothProfile.STATE_CONNECTED -> {
                    connectMessage.value = "CONNECTED"
                    bluetoothGatt?.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTING -> connectMessage.value = "DISCONNECTING"

                BluetoothProfile.STATE_DISCONNECTED -> connectMessage.value = "DISCONNECTED"

                else -> connectMessage.value = "UNKNOWN"
            }
        }
    }

}