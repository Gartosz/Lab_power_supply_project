package com.example.lab_supply_app.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow


class BleService(): Service() {

    private val binder = LocalBinder()
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    val connectMessage = MutableStateFlow("DISCONNECTED")
    val data = MutableStateFlow<List<BluetoothGattService>>(listOf())
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        close()
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

    @SuppressLint("MissingPermission")
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        bluetoothGatt?.let { gatt ->
            gatt.readCharacteristic(characteristic)
        } ?: run {
            return
        }
    }

    private fun bytesToString(value: ByteArray): String
    {
        val array = ByteBuffer.wrap(value).array()
        var result = ""
        for (i in array) {
            result += i.toInt().toChar()
        }

        return result
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

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                scope.launch {
                data.value = listOf()
                gatt?.let {
                    data.value = it.services
                    enableNotificationsAndIndications()
                }
            }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ConnectedDevice.current.value = bytesToString(value)
            }
        }
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ConnectedDevice.current.value = bytesToString(characteristic.value)
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            ConnectedDevice.current.value = bytesToString(characteristic.value)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            ConnectedDevice.current.value = bytesToString(value)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            println("descriptor write: ${descriptor.uuid}, ${descriptor.characteristic.uuid}, $status")
        }

        @SuppressLint("MissingPermission")
        suspend fun enableNotificationsAndIndications() {

            bluetoothGatt?.services?.forEach { gattSvcForNotify ->
                gattSvcForNotify.characteristics?.forEach { chracteristic ->

                    chracteristic.descriptors.find { desc ->
                        desc.uuid.toString() == BleServicesData.uuid
                    }?.also { data ->
                        val notifyRegistered =
                            bluetoothGatt?.setCharacteristicNotification(chracteristic, true)

                        if (chracteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                            data.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                            bluetoothGatt?.writeDescriptor(data)
                        }

                        if (chracteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
                            data.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)
                            bluetoothGatt?.writeDescriptor(data)
                        }

                        delay(300L)

                    }

                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun connect(address: String) {
        if (bluetoothAdapter?.isEnabled == true && address != bluetoothGatt?.device?.address) {
            bluetoothAdapter.let { adapter ->
                try {
                    connectMessage.value = "CONNECTING"
                    val device = adapter?.getRemoteDevice(address)
                    device?.connectGatt(this, false, bluetoothGattCallback)
                } catch (error: Exception) {
                    connectMessage.value = "DISCONNECTED"
                    Log.e("GATT_CONNECT", error.toString())
                }
            }
        }
    }


        @SuppressLint("MissingPermission")
        fun close() {
            try {
                connectMessage.value = "DISCONNECTING"
                bluetoothGatt?.let { gatt ->
                    gatt.disconnect()
                    gatt.close()
                }
            } catch (error: Exception) {
                Log.e("GATT_DISCONNECT", error.toString())
            } finally {
                connectMessage.value = "DISCONNECTED"
                bluetoothGatt = null
            }
        }

}