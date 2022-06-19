package com.example.travelcultureapplicaiton

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.travelcultureapplicaiton.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kakao.sdk.common.util.Utility


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreference: SharedPreferences

    private val fl: FrameLayout by lazy {
        findViewById(R.id.nav_host_fragment_activity_main)
    }
    private val bn: BottomNavigationView by lazy {
        findViewById(R.id.nav_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val keyHash = Utility.getKeyHash(this)
        //Log.d("appTest", keyHash)

        // 프래그먼트 연결하기
        // 화면 뜨는 것의 원인, 이거 어떻게 해결 안되나? 처음 실행할 때만 실행되도록..
        //supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, HomeFragment()).commit()


        setSupportActionBar(binding.toolbar)

        // 바텀 내비게이션 아이템 클릭 리스너 설정
        bn.setOnItemSelectedListener{
            replaceFragment(
                when (it.itemId){
                    R.id.navigation_home -> HomeFragment()
                    R.id.navigation_map ->MapFragment()
                    R.id.navigation_list -> ListFragment()
                    else -> CourseFragment()
                }

            )
            true
        }
        bn.selectedItemId = R.id.navigation_home
    }

    private fun getUID(): String? {
        val sharedPreferences: SharedPreferences = getSharedPreferences("uid", MODE_PRIVATE)
        val uid = sharedPreferences.getString("uid", "")

        return uid
    }

    override fun onStart() { // mainActivity에서 다른 activity로 이동하여 다른 작업 후 다시 돌아올 때 실행하는 메서드
        super.onStart()
        val uid = getUID()
        if(MyApplication.checkAuth() || MyApplication.email != null || uid != null){ // 검증된 유저인지 확인
            Log.d("appTest", "${MyApplication.checkAuth()}  ${MyApplication.email}")
            setSupportActionBar(binding.toolbar)

            // 바텀 내비게이션 아이템 클릭 리스너 설정
            bn.setOnItemSelectedListener{
                replaceFragment(
                    when (it.itemId){
                        R.id.navigation_home -> HomeFragment()
                        R.id.navigation_map -> MapFragment()
                        R.id.navigation_list -> ListFragment()
                        else -> CourseFragment()
                    }
                )
                true
            }

        } else{
            finish()
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() { // 액티비티가 중단되었다가 다시 실행될 때 호출
        super.onResume()
        bn.setOnItemSelectedListener{
            replaceFragment(
                when (it.itemId){
                    R.id.navigation_home -> HomeFragment()
                    R.id.navigation_map ->MapFragment()
                    R.id.navigation_list -> ListFragment()
                    else -> CourseFragment()
                }
            )
            true
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(fl.id, fragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // 옵션 메뉴 추가
        //리소스로 만든 메뉴 적용
        menuInflater.inflate(R.menu.menu_main_setting, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings -> {
                // 설정 화면으로 이동
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)

            }
        }

        return super.onOptionsItemSelected(item)
    }
}