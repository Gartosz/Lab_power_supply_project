package com.example.lab_supply_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.lab_supply_app.databinding.RwValuesLayoutBinding
import com.example.lab_supply_app.models.RWViewModel

class RWFragment : Fragment() {
    private lateinit var binding: RwValuesLayoutBinding
    private val cocktailViewModel: RWViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RwValuesLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}