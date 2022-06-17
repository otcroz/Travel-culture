package com.example.travelcultureapplicaiton

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.travelcultureapplicaiton.MyApplication.Companion.auth
import com.example.travelcultureapplicaiton.databinding.ActivityRegisterBinding
import java.util.*
import kotlin.collections.ArrayList

class RegisterActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegisterBinding
    lateinit var userCheckcategory: ArrayList<String>
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var checkboxItems = arrayListOf<String>()
        userCheckcategory = arrayListOf<String>()

        val nextButton = binding.nextSignIn
        val nextSuccess = binding.nextSuccess
        val inputName = binding.inputName
        val residence = binding.residence
        val favoriteCategory = binding.favoriteCategory
        val newEmail = binding.newEmail
        val inputNewpassword = binding.inputNewpassword
        val checkPassword = binding.checkPassword

        // residence Spinner 연결
        val sAdapter = ArrayAdapter.createFromResource(this, R.array.set_location, android.R.layout.simple_spinner_dropdown_item)
        binding.residence.adapter = sAdapter;

        // 로그인 인텐트
        val intent = Intent(this, LoginActivity::class.java)

        // 회원가입 > 관심 분야 클릭했을 때
        val dialogCategory: Array<String> = resources.getStringArray(R.array.set_category)
        var displaySelected = ""

        binding.favoriteCategory.setOnClickListener{
            AlertDialog.Builder(this).run {
                setTitle("멀티 아이템 목록 선택")
                setIcon(android.R.drawable.ic_dialog_info)

                setMultiChoiceItems(dialogCategory, booleanArrayOf(false, false, false, false),
                    object : DialogInterface.OnMultiChoiceClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int, p2: Boolean) {
                            if(p2){
                                checkboxItems.add(dialogCategory[p1])
                            }else{
                                checkboxItems.remove(dialogCategory[p1])
                            }
                        }
                    }
                )
                    // 확인 버튼을 눌렀을 때
                        setPositiveButton("확인", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                displaySelected = ""
                                userCheckcategory.clear()
                                for (i in checkboxItems){
                                    Log.d("appTest", "checkboxItems $i")
                                }
                                if (checkboxItems != null) {
                                    for(check in checkboxItems){
                                        displaySelected += "$check "
                                        userCheckcategory.add(check)

                                    }
                                    Log.d("appTest", "displaySelected $displaySelected")
                                    binding.favoriteCategory.hint = displaySelected
                                }
                            }
                        }
                        )
                checkboxItems.clear()
                show()
            }.setCanceledOnTouchOutside(true)
        }



        //모든 EditText 값 > 0 일때 다음 화면으로 넘어가도록 한다.
        var textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (newEmail.length() > 0 && inputNewpassword.length() > 0 && checkPassword.length() > 0){
                    nextButton.isEnabled = true
                    nextButton.setBackgroundColor(Color.parseColor("#B4D480"));

                    if(inputName.length() > 0){
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
        inputName.addTextChangedListener(textWatcher)

        // 다음으로 버튼 클릭
        nextButton.setOnClickListener {
            Log.d("appTest", "${inputNewpassword.text}  ${checkPassword.text}")
            if(inputNewpassword.text.toString() != checkPassword.text.toString()){
                Toast.makeText(baseContext, "비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else{
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
        }
        // 회원가입 완료
        nextSuccess.setOnClickListener {
            // db에 회원 정보 옮기는 작업 수행
            val email = binding.newEmail.text.toString()
            val password = binding.inputNewpassword.text.toString()
            // DB에서 해당 이메일로 회원가입한 정보가 있는지 확인하기

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
            "uid" to auth?.currentUser?.uid.toString(),
            "email" to binding.newEmail.text.toString(), // read, write 권한을 가지도록 하기위해 설정
            "nickname" to binding.inputName.text.toString(),
            "residence" to binding.residence.selectedItem.toString(),
            "category" to userCheckcategory,
            "imageFile" to null
        )

        MyApplication.db.collection("user").document(auth?.currentUser?.uid.toString()) // 이미지, 글에 대한 정보가 이 테이블에 저장
            .set(data) // 데이터 업로드
            .addOnSuccessListener{
                Log.d("appTest", "data save success")
            }
            .addOnFailureListener{
                Log.d("appTest", "data save error")
            }
        binding.newEmail.text.clear()
        binding.inputNewpassword.text.clear()
    }
}