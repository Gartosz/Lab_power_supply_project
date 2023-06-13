package com.example.lab_supply_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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