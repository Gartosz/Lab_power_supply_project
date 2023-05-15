package com.example.lab_supply_app.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Intent
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
import com.example.lab_supply_app.bluetooth.BleDevicesAdapter
import com.example.lab_supply_app.bluetooth.BleScanCallback
import com.example.lab_supply_app.databinding.ConnectionLayoutBinding
import com.example.lab_supply_app.models.ConnectionsViewModel

class ConnectionFragment : Fragment() {
    private lateinit var binding: ConnectionLayoutBinding
    private val connectionViewModel: ConnectionsViewModel by viewModels()
    private lateinit var bluetoothAdapter: BluetoothAdapter
    @RequiresApi(Build.VERSION_CODES.S)
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.bluetooth.visibility = View.INVISIBLE
        }else{
            Toast.makeText(binding.root.context,
                "Wymagane jest przyznanie uprawnień " +
                        "do połączenia i skanowania Bluetooth.",
                Toast.LENGTH_LONG).show()
            binding.bluetooth.isChecked = false
        }
    }
    private var scanning = false
    private val bleScanCallback = BleScanCallback()
    private lateinit var bluetoothLeScanner : BluetoothLeScanner
    @RequiresApi(Build.VERSION_CODES.S)
    private val requiredPermissions = listOf(Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConnectionLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bluetoothManager: BluetoothManager = binding.root.context.applicationContext.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        val stateObserver = Observer<Boolean> { newState ->
            binding.bluetooth.isChecked = newState
            if (!newState)
                binding.bluetooth.visibility = View.VISIBLE
            else
                binding.bluetooth.visibility = View.INVISIBLE
        }
        connectionViewModel.bluetoothState.value = bluetoothAdapter.isEnabled
        connectionViewModel.bluetoothState.observe(viewLifecycleOwner, stateObserver)
        binding.bluetooth.setOnCheckedChangeListener { _, isChecked ->
            setBluetooth(!isChecked)
        }
        @RequiresApi(Build.VERSION_CODES.S)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions -> permissions.entries.forEach{
                val isGranted = it.value
                val permission = it.key
                if(!isGranted) {
                    Toast.makeText(
                        binding.root.context, "Wymagany jest dostep do " + permission, Toast.LENGTH_LONG
                    ).show()

                }
            }

        }
        setBluetooth(true)
        binding.bluetoothDevices.setOnClickListener{
            if (scanning)
                turnScanningOff()
            else
                showBLEDevices()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        connectionViewModel.bluetoothState.value = bluetoothAdapter.isEnabled
    }

    override fun onResume() {
        super.onResume()
        connectionViewModel.bluetoothState.value = bluetoothAdapter.isEnabled
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

    @SuppressLint("MissingPermission")
    private fun showBLEDevices()
    {
        if(setBluetooth(true))
        {
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            if (!scanning) {
                bluetoothLeScanner.startScan(bleScanCallback)
                scanning = true
            }
        }
    }

    private fun turnScanningOff()
    {

    }

    private fun setBluetooth(isChecked: Boolean) : Boolean
    {
        println("guzior" + isChecked)

        if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            var permissions = requiredPermissions.toMutableList()
            if(requestPermissions(permissions))
                binding.bluetooth.visibility = View.INVISIBLE
            return permissions.isEmpty()
        }
        if (isChecked)
        {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
            return bluetoothAdapter.isEnabled
        }
        return false
    }

}