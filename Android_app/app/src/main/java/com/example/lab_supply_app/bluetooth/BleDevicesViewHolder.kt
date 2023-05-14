package com.example.lab_supply_app.bluetooth

import androidx.recyclerview.widget.RecyclerView
import com.example.lab_supply_app.databinding.BleDeviceItemBinding

class BleDevicesViewHolder (private val binding: BleDeviceItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: String) {
        binding.deviceName.text = item
    }
}