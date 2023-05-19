package com.example.lab_supply_app.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.os.Handler
import android.os.Looper
import android.widget.Button

class BleManager(private val bluetoothAdapter: BluetoothAdapter,
                 private val scanButton: Button,
                 private val textArray: Array<String>,
                 private val BLEScanCallback: BleScanCallback = BleScanCallback()){

    private lateinit var BleScanner : BluetoothLeScanner

    private val handler = Handler(Looper.getMainLooper())
    private var isScanning: Boolean = false
    @SuppressLint("MissingPermission")
    fun scanBleDevices() {
        BleScanner = bluetoothAdapter.bluetoothLeScanner

        if (isScanning) {

            isScanning = false
            scanButton.text = textArray[0]
            stopScan(BleScanner)

        } else {
            scanButton.text = textArray[1]
            handler.postDelayed({ stopScan(BleScanner) }, 10000)

            isScanning = true
            BleScanner.startScan(BLEScanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScan(BleScanner: BluetoothLeScanner) {
        BleScanner.stopScan(BLEScanCallback)
        handler.removeCallbacksAndMessages(null)
    }
}