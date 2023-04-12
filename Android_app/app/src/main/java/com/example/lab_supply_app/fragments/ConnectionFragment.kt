package com.example.lab_supply_app.fragments

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.lab_supply_app.databinding.ConnectionLayoutBinding
import com.example.lab_supply_app.models.ConnectionsViewModel

class ConnectionFragment : Fragment() {
    private lateinit var binding: ConnectionLayoutBinding
    private val connectionViewModel: ConnectionsViewModel by viewModels()
    private lateinit var bluetoothAdapter : BluetoothAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConnectionLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}