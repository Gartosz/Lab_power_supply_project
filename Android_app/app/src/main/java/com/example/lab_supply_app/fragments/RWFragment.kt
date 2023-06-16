package com.example.lab_supply_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.lab_supply_app.bluetooth.ConnectedDevice
import com.example.lab_supply_app.databinding.RwValuesLayoutBinding
import com.example.lab_supply_app.models.RWViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RWFragment : Fragment() {
    private lateinit var binding: RwValuesLayoutBinding
    private val RWViewModel: RWViewModel by viewModels()
    private var observeCurrent : Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RwValuesLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(observeCurrent == null) {
            observeCurrent = lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    ConnectedDevice.current.collect() {
                        if (it == "CONNECTION ERROR")
                        {
                            ConnectedDevice.current.value = ""
                            val builder = AlertDialog.Builder(binding.root.context)
                            builder.setTitle("CONNECTION LOST")
                            builder.setMessage("The BLE device has been disconnected!")

                            builder.setNeutralButton("Maybe") { dialog, which ->
                                Toast.makeText(binding.root.context,
                                    "OK", Toast.LENGTH_SHORT).show()
                            }
                            builder.show()
                        }
                        else
                            binding.readCurrent.text = "$it mA"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observeCurrent?.cancel("View destroyed")
    }
}