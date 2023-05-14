package com.example.lab_supply_app.bluetooth

import androidx.recyclerview.widget.DiffUtil

class BleDevicesComparator : DiffUtil.ItemCallback<String>(){
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}