package com.example.travelcultureapplicaiton


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.travelcultureapplicaiton.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 페이지 이동
        binding.gotoSignUp.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 이메일 로그인


        // 카카오 간편 로그인


        // 페이스북 간편 로그인
    }
}