package com.example.travelcultureapplicaiton


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
        binding.btnLogin.setOnClickListener {
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            if(email == "" || password == ""){
                Toast.makeText(baseContext, "아이디 또는 비밀번호를 입력해주세요!", Toast.LENGTH_SHORT)
                    .show()
            } else{
                MyApplication.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        binding.authEmailEditView.text.clear()
                        binding.authPasswordEditView.text.clear()
                        //Log.d("mobileApp", task.)
                        if (task.isSuccessful) {
                            if (MyApplication.checkAuth()) {
                                MyApplication.email = email
                                // 메인 페이지로 이동
                                val intent = Intent(this, MainActivity::class.java)
                                //intent.putExtra("userData", email) // 해당하는 닉네임(현재는 이메일로 대체)
                                startActivity(intent)

                                finish()
                            } else {
                                Toast.makeText(baseContext, "이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // 카카오 간편 로그인


        // 페이스북 간편 로그인
    }
}