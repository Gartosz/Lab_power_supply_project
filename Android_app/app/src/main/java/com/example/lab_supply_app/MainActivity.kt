package com.example.lab_supply_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lab_supply_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}