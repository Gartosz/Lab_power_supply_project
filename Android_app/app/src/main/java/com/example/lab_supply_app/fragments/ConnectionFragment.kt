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

    private fun requestPermission(permission: String)
    {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                binding.root.context,
                permission
            ) -> {
                // You can use the API that requires the permission.
            }
            //            shouldShowRequestPermissionRationale() -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
            //            showInContextUI()
            //        }
            else -> {
                Toast.makeText(binding.root.context, "Wymagane jest przyznanie uprawnień " +
                        "do połączenia i skanowania Bluetooth.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(
                    permission

                )
            }
        }
    }



}