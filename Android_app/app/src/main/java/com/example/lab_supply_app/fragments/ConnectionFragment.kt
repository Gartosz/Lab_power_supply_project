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
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions -> permissions.entries.forEach{
                    val isGranted = it.value
                    val permission = it.key
                    if(!isGranted) {
                        val neverAskAgain = !ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            permission
                        )
                        if (neverAskAgain) {
                            //user click "never ask again"
                        } else {
                            //show explain dialog
                        }

                        Toast.makeText(
                            binding.root.context, "Wymagane jest przyznanie uprawnień " +
                                    "do połączenia i skanowania Bluetooth.", Toast.LENGTH_LONG
                        ).show()
                        return@registerForActivityResult
                    }
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

    private fun checkPermissions(permissions: MutableList<String>)
    {
        permissions.forEachIndexed { index, element ->
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    binding.root.context,
                    element
                ) -> {
                    permissions.removeAt(index)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissions(permissions: MutableList<String>) : Boolean {
        checkPermissions(permissions)
        if (permissions.isNotEmpty()) {
            Toast.makeText(
                binding.root.context, "Wymagane jest przyznanie uprawnień " +
                        "do połączenia i skanowania Bluetooth.", Toast.LENGTH_LONG
            ).show()
            requestPermissionLauncher.launch(
                permissions.toTypedArray()
            )
            checkPermissions(permissions)
        }
        return permissions.isEmpty()
    }

    private fun showBLEDevices()
    {

    }

}