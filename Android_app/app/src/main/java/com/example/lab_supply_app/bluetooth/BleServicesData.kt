package com.example.lab_supply_app.bluetooth

import android.bluetooth.BluetoothGattDescriptor

const val UUID_DEFAULT = "-0000-1000-8000-00805F9B34FB"
const val PARAM_NOTIFY = 16

abstract class UuidParsable(val uuid: String) {

    abstract fun commands(param: Any? = null): Array<String>
    abstract fun getReadStringFromBytes(byteArray: ByteArray): String
}

fun ByteArray.toHex(): String =
    "0x" + joinToString(separator = "") { eachByte -> "%02X".format(eachByte).uppercase() }


object BleServicesData: UuidParsable("00002902$UUID_DEFAULT".lowercase()) {

    override fun commands(param: Any?): Array<String> {
        return if (param == PARAM_NOTIFY)
            arrayOf(
                "Enable Notifications: ${BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE.toHex()}",
                "Disable Notifications: ${BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE.toHex()}"
            ) else arrayOf(
            "Enable Indications: ${BluetoothGattDescriptor.ENABLE_INDICATION_VALUE.toHex()}",
            "Disable Indications: ${BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE.toHex()}"
        )
    }

    override fun getReadStringFromBytes(byteArray: ByteArray): String {
        return if (byteArray.contentEquals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) ||
            byteArray.contentEquals(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)
        )
            "Enabled."
        else
            "Disabled."
    }
}
