package com.example.travelcultureapplicaiton

import android.app.Application
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.travelcultureapplicaiton.databinding.ActivityRegisterBinding
import java.util.*

class RegisterActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nextButton = binding.nextSignIn
        val nextSuccess = binding.nextSuccess
        val inputName = binding.inputName
        val residence = binding.residence
        val favoriteCategory = binding.favoriteCategory
        val newEmail = binding.newEmail
        val inputNewpassword = binding.inputNewpassword
        val checkPassword = binding.checkPassword

        // 로그인 인텐트
        val intent = Intent(this, LoginActivity::class.java)

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
        // 회원가입 완료
        nextSuccess.setOnClickListener {
            // db에 회원 정보 옮기는 작업 수행
            val email = binding.newEmail.text.toString()
            val password = binding.inputNewpassword.text.toString()
            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { sendTask ->
                                if (sendTask.isSuccessful) {
                                    Toast.makeText(baseContext, "회원가입 성공!! 메일을 확인해주세요", Toast.LENGTH_SHORT).show()
                                    startActivity(intent)
                                    // 유저 정보 저장하기
                                    saveStore()

                                } else {
                                    Toast.makeText(baseContext, "메일발송 실패", Toast.LENGTH_SHORT).show()
                                    startActivity(intent)
                                }
                            }
                    } else {
                        Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                }

            //startActivity(intent)
        }
    }

    // 유저 정보 저장
    private fun saveStore(){ // 이메일, 닉네임, 지역, 카테고리
        Log.d("appTest","유저 테이블 생성하기")
        val data = mapOf(
            "email" to binding.newEmail.text.toString(), // read, write 권한을 가지도록 하기위해 설정
            "nickname" to binding.inputName.text.toString(),
            "residence" to binding.residence.text.toString(),

            //"category" to 여러 개의 값을 받는다.
        )

        MyApplication.db.collection("user") // 이미지, 글에 대한 정보가 이 테이블에 저장
            .add(data) // 데이터 업로드
            .addOnSuccessListener{
                Log.d("mobileApp", "data save success")
            }
            .addOnFailureListener{
                Log.d("mobileApp", "data save error")
            }
        binding.newEmail.text.clear()
        binding.inputNewpassword.text.clear()
    }
}