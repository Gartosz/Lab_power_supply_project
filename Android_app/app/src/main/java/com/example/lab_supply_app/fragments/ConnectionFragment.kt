package com.example.lab_supply_app.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab_supply_app.R
import com.example.lab_supply_app.bluetooth.BleDevicesAdapter
import com.example.lab_supply_app.bluetooth.BleDevicesComparator
import com.example.lab_supply_app.bluetooth.BleManager
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
            if(result.resultCode != RESULT_OK){
            Toast.makeText(binding.root.context,
                "Wymagane jest przyznanie uprawnień " +
                        "do połączenia i skanowania Bluetooth.",
                Toast.LENGTH_LONG).show()
            binding.bluetooth.isChecked = false
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private val requiredPermissions = listOf(Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT)
    private var permissionsDeniedPermanently = false
    private lateinit var BLEManager: BleManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConnectionLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bluetoothDevices.text = binding.root.context.resources.getStringArray(R.array.devicesList)[0]
        val bluetoothManager: BluetoothManager = binding.root.context.applicationContext.
        getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        val stateObserver = Observer<Boolean> { newState ->
            if (!newState)
                binding.bluetooth.visibility = View.VISIBLE
            else
                binding.bluetooth.visibility = View.INVISIBLE
        }
        connectionViewModel.bluetoothState.value = verifyBluetooth()
        connectionViewModel.bluetoothState.observe(viewLifecycleOwner, stateObserver)
        binding.bluetooth.setOnClickListener {
            val result = setBluetooth()

            if (!result && permissionsDeniedPermanently)
            {
                fun Context.openAppSystemSettings() = startActivity(Intent
                    (Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)))
            }
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
                        binding.root.context, "Wymagany jest dostep do $permission", Toast.LENGTH_LONG
                    ).show()
                    val notPermanentlyDenied = ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(), permission)
                    if (!permissionsDeniedPermanently) {
                        permissionsDeniedPermanently = !notPermanentlyDenied
                    }
                }
            }

        }
        setBluetooth()
        binding.bluetoothDevices.setOnClickListener{
        toggleScan()
        }
        setRecyclerView()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        connectionViewModel.bluetoothState.postValue(verifyBluetooth())
    }

    override fun onResume() {
        super.onResume()
        connectionViewModel.bluetoothState.postValue(verifyBluetooth())
    }

    private fun setRecyclerView()
    {
        val adapter = BleDevicesAdapter(BleDevicesComparator())
        binding.availableDevices.adapter = adapter
        binding.availableDevices.layoutManager = LinearLayoutManager(requireContext())
        connectionViewModel.BLEDevices.observe(viewLifecycleOwner, adapter::submitList)
    }

    private fun checkPermissions(permissions: MutableList<String>)
    {
        val iterator = permissions.iterator()
        while(iterator.hasNext())
        {
            val next = iterator.next()
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    binding.root.context,
                    next
                ) -> {
                    iterator.remove()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissions(permissions: MutableList<String>) : Boolean {
        checkPermissions(permissions)
        permissionsDeniedPermanently = false
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
    private fun toggleScan()
    {
        if(setBluetooth())
        {
            connectionViewModel.BLEDevices.postValue(mutableListOf())
            binding.availableDevices.adapter?.notifyDataSetChanged()
            BLEManager = BleManager(bluetoothAdapter, binding.bluetoothDevices,
                binding.root.context.resources.getStringArray(R.array.devicesList),
                BLEScanCallback = BleScanCallback {
                val deviceAddress = it?.device?.address
                if (deviceAddress.isNullOrBlank()) return@BleScanCallback

                if (!connectionViewModel.BLEDevices.value?.contains(deviceAddress)!!) {
                    val devicesList = connectionViewModel.BLEDevices.value
                    devicesList?.add(deviceAddress)
                    connectionViewModel.BLEDevices.postValue(devicesList)
                    binding.availableDevices.adapter?.notifyItemInserted(
                        (connectionViewModel.BLEDevices.value?.size!!) - 1
                    )
                }
            })
            BLEManager.scanBleDevices()
        }
    }

    private fun setBluetooth() : Boolean
    {
        val verification = requestBluetooth()
        connectionViewModel.bluetoothState.postValue(verification)
        return verification
    }

    private fun requestBluetooth(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = requiredPermissions.toList().toMutableList()
            requestPermissions(permissions)
            if (permissions.isNotEmpty())
                return permissions.isEmpty()
        }
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
            return bluetoothAdapter.isEnabled
        }
        return true
    }

    private fun verifyBluetooth(): Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = requiredPermissions.toList().toMutableList()
            checkPermissions(permissions)

            if (permissions.isNotEmpty())
                return permissions.isEmpty()
        }
        return bluetoothAdapter.isEnabled
    }


}