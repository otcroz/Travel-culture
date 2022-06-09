package com.example.travelcultureapplicaiton

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.kakao.sdk.common.KakaoSdk
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MyApplication : MultiDexApplication() {
    companion object {
        // 네트워크 연결(공공데이터)
        var networkServiceXml : NetworkService
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val retrofitXml: Retrofit
            get() = Retrofit.Builder()
                .baseUrl("http://api.visitkorea.or.kr/")
                .addConverterFactory(TikXmlConverterFactory.create(parser))
                .build()
        init{
            networkServiceXml = retrofitXml.create(NetworkService::class.java)
        }

        // 파이어베이스
        lateinit var auth: FirebaseAuth
        var email: String? = null
        lateinit var db: FirebaseFirestore
        lateinit var storage: FirebaseStorage

        fun checkAuth(): Boolean{
            var currentUser = auth.currentUser
            return currentUser?.let{
                email = currentUser.email
                currentUser.isEmailVerified}?: let{
                false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
        KakaoSdk.init(this, "cb840c86473294c4985cbd9d9da13202") // 네이티브 앱 키를 넣어준다.

        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage
    }
}