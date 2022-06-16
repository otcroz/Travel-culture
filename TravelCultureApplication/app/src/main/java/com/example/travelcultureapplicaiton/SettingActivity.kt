package com.example.travelcultureapplicaiton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.travelcultureapplicaiton.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("appTest", "SettingActivity")
        val binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}