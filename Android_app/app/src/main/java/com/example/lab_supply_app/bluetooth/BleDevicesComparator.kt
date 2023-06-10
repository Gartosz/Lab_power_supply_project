package com.example.lab_supply_app.bluetooth

import androidx.recyclerview.widget.DiffUtil

class BleDevicesComparator : DiffUtil.ItemCallback<BleDevice>(){
    override fun areContentsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean {
        return oldItem == newItem
    }
}