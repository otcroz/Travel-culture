package com.example.travelcultureapplicaiton


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelcultureapplicaiton.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient


class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 페이지 이동
        binding.gotoSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
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

                                // sharedPreferences에 uid 저장
                                val sharedPreferences = getSharedPreferences("uid", MODE_PRIVATE) // test 이름의 기본모드 설정
                                val editor = sharedPreferences.edit() //sharedPreferences를 제어할 editor를 선언
                                editor.putString("uid", MyApplication.auth?.currentUser?.uid.toString()) // key,value 형식으로 저장
                                editor.commit() //최종 커밋. 커밋을 해야 저장이 된다.


                                // 메인 페이지로 이동
                                val intent = Intent(this, MainActivity::class.java)
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
        binding.kakaoLoginBtn.setOnClickListener {
            // 토큰 정보 보기
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.e("appTest", "토큰 정보 보기 실패", error)
                }
                else if (tokenInfo != null) {
                    Log.i("appTest", "토큰 정보 보기 성공")
                    finish() // MainActivity 로 이동
                }
            }
            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("appTest", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("appTest", "카카오계정으로 로그인 성공 ${token.accessToken}")

                    // 사용자 정보 요청 (기본)
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e("appTest", "사용자 정보 요청 실패", error)
                        }
                        else if (user != null) {
                            Log.i("appTest", "사용자 정보 요청 성공" +
                                    "\n회원번호: ${user.id}" +
                                    "\n이메일: ${user.kakaoAccount?.email}" +
                                    "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                    "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")

                            // 카카오 회원 전용 회원가입 페이지로 이동
//                            val intent = Intent(this, RegisterKakaoActivity::class.java)
//                            intent.putExtra("uid", user.id)
//                            intent.putExtra("email", user.kakaoAccount?.email)
//                            intent.putExtra("nickname", user.kakaoAccount?.profile?.nickname)
//                            intent.putExtra("profileImage", user.kakaoAccount?.profile?.thumbnailImageUrl)
//                            startActivity(intent)

                            // sharedPreferences에 uid 저장
                            if(user.id != null){ 
                                val sharedPreferences = getSharedPreferences("uid", MODE_PRIVATE) // test 이름의 기본모드 설정
                                val editor = sharedPreferences.edit() //sharedPreferences를 제어할 editor를 선언
                                editor.putString("uid", user.id.toString()) // key,value 형식으로 저장
                                editor.commit() //최종 커밋. 커밋을 해야 저장이 된다.
                                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                            
                            // 메인 페이지로 이동
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){ // 카카오톡으로 로그인
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else  { // 사용자 계정에 카카오톡이 설치되어 있지 않은 경우, 카카오계정으로 로그인
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }
}