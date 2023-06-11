package com.example.lab_supply_app.bluetooth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.lab_supply_app.databinding.BleDeviceItemBinding

class BleDevicesAdapter (deviceComparator: BleDevicesComparator) : ListAdapter<BleDevice, BleDevicesViewHolder>(deviceComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDevicesViewHolder {
        return BleDevicesViewHolder(
            BleDeviceItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BleDevicesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            ConnectedDevice.handleDeviceUpdate(item.name, item.address)
        }
    }

}