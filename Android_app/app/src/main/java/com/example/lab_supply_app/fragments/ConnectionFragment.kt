package com.example.lab_supply_app.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.lab_supply_app.databinding.ConnectionLayoutBinding
import com.example.lab_supply_app.models.ConnectionsViewModel

class ConnectionFragment : Fragment() {
    private lateinit var binding: ConnectionLayoutBinding
    private val connectionViewModel: ConnectionsViewModel by viewModels()
    private lateinit var bluetoothAdapter : BluetoothAdapter
    @RequiresApi(Build.VERSION_CODES.S)
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.bluetooth.visibility = View.INVISIBLE
        }else{
            Toast.makeText(binding.root.context,
                "Bluetooth is required to be able to connect with arduino module.",
                Toast.LENGTH_LONG).show()
            binding.bluetooth.isChecked = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConnectionLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.S)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    Toast.makeText(binding.root.context, "Wymagane jest przyznanie uprawnień " +
                            "do połączenia i skanowania Bluetooth.", Toast.LENGTH_LONG).show()
                }
            }
        requestPermission(Manifest.permission.BLUETOOTH_SCAN)
        requestPermission(Manifest.permission.BLUETOOTH_CONNECT)
        binding.bluetoothDevices.setOnClickListener{
            showBLEDevices()
        }
        val bluetoothManager: BluetoothManager = ContextCompat.getSystemService(binding.root.context, BluetoothManager::class.java)!!
        bluetoothAdapter = bluetoothManager.adapter
        val stateObserver = Observer<Boolean> { newState ->
            binding.bluetooth.isChecked = newState
        }
        connectionViewModel.bluetoothState.value = bluetoothAdapter.isEnabled
        connectionViewModel.bluetoothState.observe(viewLifecycleOwner, stateObserver)
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

    private fun showBLEDevices()
    {

    }

}