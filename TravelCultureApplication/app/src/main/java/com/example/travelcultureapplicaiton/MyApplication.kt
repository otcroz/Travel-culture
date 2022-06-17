package com.example.travelcultureapplicaiton

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
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
        // 네트워크 연결(행사 정보)
        var networkServiceXml : NetworkService // 검색
        var networkService_location : NetworkLocationService // 위치기반
        var networkSetvice_detail: NetworkDetailService // 등록 정보(detail)
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val retrofitXml: Retrofit
            get() = Retrofit.Builder()
                .baseUrl("http://api.visitkorea.or.kr/")
                .addConverterFactory(TikXmlConverterFactory.create(parser))
                .build()
        init{
            networkServiceXml = retrofitXml.create(NetworkService::class.java) // 검색
            networkService_location = retrofitXml.create(NetworkLocationService::class.java) // 위치기반
            networkSetvice_detail = retrofitXml.create(NetworkDetailService::class.java) // 상세 정보
        }

        // 파이어베이스: 정보
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