package com.example.travelcultureapplicaiton

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyApplication : MultiDexApplication() {
    companion object {
        lateinit var auth: FirebaseAuth
        var email: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
    }
}