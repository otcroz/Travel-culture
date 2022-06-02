package com.example.travelcultureapplicaiton

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.travelcultureapplicaiton.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nextButton = binding.nextSignIn
        val nextSuccess = binding.nextSuccess
        val inputName = binding.inputName
        val residence = binding.residence
        val favoriteCategory = binding.favoriteCategory
        val newEmail = binding.newEmail
        val inputNewpassword = binding.inputNewpassword
        val checkPassword = binding.checkPassword

        //모든 EditText 값 > 0 일때 다음 화면으로 넘어가도록 한다.
        var textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (newEmail.length() > 0 && inputNewpassword.length() > 0 && checkPassword.length() > 0){
                    nextButton.isEnabled = true
                    nextButton.setBackgroundColor(Color.parseColor("#B4D480"));
                    if(residence.length() > 0 && favoriteCategory.length() > 0 && inputName.length() > 0){
                        nextSuccess.isEnabled = true
                        nextSuccess.setBackgroundColor(Color.parseColor("#B4D480"));
                    }
                } else{
                    nextButton.isEnabled = false
                    nextButton.setBackgroundColor(Color.parseColor("#d3d3d3"));
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }

        // 이벤트 핸들러 연결하기
        newEmail.addTextChangedListener(textWatcher)
        inputNewpassword.addTextChangedListener(textWatcher)
        checkPassword.addTextChangedListener(textWatcher)
        residence.addTextChangedListener(textWatcher)
        favoriteCategory.addTextChangedListener(textWatcher)
        inputName.addTextChangedListener(textWatcher)

        // 다음으로 버튼 클릭
        nextButton.setOnClickListener {
            nextButton.setBackgroundColor(Color.parseColor("#d3d3d3"))
            nextButton.text = "입력완료~!"
            // 버튼 색깔 설정하기
            inputName.visibility = View.VISIBLE
            residence.visibility = View.VISIBLE
            favoriteCategory.visibility = View.VISIBLE
            nextSuccess.visibility = View.VISIBLE

            // 이전에 입력했던 창들은 보이지 않게
            newEmail.visibility = View.GONE
            inputNewpassword.visibility = View.GONE
            checkPassword.visibility = View.GONE
            nextButton.visibility = View.GONE


        }
        nextSuccess.setOnClickListener {
            // db에 회원 정보 옮기는 작업 수행
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}