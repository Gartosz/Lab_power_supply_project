package com.example.lab_supply_app.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab_supply_app.R
import com.example.lab_supply_app.bluetooth.*
import com.example.lab_supply_app.databinding.ConnectionLayoutBinding
import com.example.lab_supply_app.models.ConnectionsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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
    private var bluetoothRequest = false
    private var bluetoothService : BleService? = null
    private var observeStateFlow : Job? = null
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName,
            service: IBinder
        ) {
            bluetoothService = (service as BleService.LocalBinder).getService()
            observeStateFlow = lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    bluetoothService?.connectMessage?.collect() {
                        binding.connectionStatus.text = it
                    }
                }
            }
            ConnectedDevice.connectedDevice.value?.let { useDevice(it) }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            observeStateFlow?.cancel("Service disconnected")
            bluetoothService = null
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
            bluetoothRequest = true
            setBluetooth()
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

                if (permissionsDeniedPermanently && bluetoothRequest)
                {
                    bluetoothRequest = false
                    startActivity(Intent
                        (Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireActivity().packageName, null)))
                }
        }
        setBluetooth()
        binding.bluetoothDevices.setOnClickListener{
        toggleScan()
        }
        setRecyclerView()
        ConnectedDevice.connectedDevice.observe(viewLifecycleOwner) {
            if(bluetoothService == null && it.second.isNotEmpty())
            {
                val gattServiceIntent = Intent(binding.root.context, BleService::class.java)
                requireContext().applicationContext.bindService(gattServiceIntent, serviceConnection,
                                                 Context.BIND_AUTO_CREATE)
            }
            else if (bluetoothService != null && !ConnectedDevice.getAddress().isNullOrEmpty()) {
                useDevice(it)
            }
            binding.deviceName.text = it.first
        }
        binding.connectionStatus.setOnLongClickListener {
            removeConnection()
            return@setOnLongClickListener true
        }
    }

    private fun useDevice(it: Pair<String, String>) {
        setConnection()
        binding.deviceName.text = it.first
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
            if(!this::BLEManager.isInitialized) {
                BLEManager = BleManager(bluetoothAdapter, binding.bluetoothDevices,
                    binding.root.context.resources.getStringArray(R.array.devicesList),
                    BLEScanCallback = BleScanCallback {
                        val deviceAddress = it?.device?.address
                        if (deviceAddress.isNullOrBlank())
                            return@BleScanCallback
                        else if (!connectionViewModel.BLEDevices.value?.any { device -> device.address == deviceAddress }!!) {
                            val devicesList = connectionViewModel.BLEDevices.value
                            val deviceName = if(it.device.name.isNullOrEmpty()) "Unnamed"
                            else it.device.name
                            devicesList?.add(BleDevice(deviceName, deviceAddress))
                            connectionViewModel.BLEDevices.postValue(devicesList)
                            binding.availableDevices.adapter?.notifyItemInserted(
                                (connectionViewModel.BLEDevices.value?.size!!) - 1
                            )
                        }
                    })
            }
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

    private fun setConnection()
    {
        bluetoothService?.let { bluetooth ->
            if (bluetooth.setAdapter(bluetoothAdapter)
                && (!ConnectedDevice.getAddress().isNullOrEmpty())
            ) {
                bluetooth.connect(ConnectedDevice.getAddress()!!)
            }

            else
                ConnectedDevice.reset()
        }
    }
    private fun removeConnection()
    {
        ConnectedDevice.reset()
        bluetoothService?.close()
    }

}