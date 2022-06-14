package com.example.travelcultureapplicaiton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.travelcultureapplicaiton.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)

        // 리사이클러뷰에서 이벤트 발생 시 데이터 정보 가져옴

        
        setContentView(binding.root)
    }
}