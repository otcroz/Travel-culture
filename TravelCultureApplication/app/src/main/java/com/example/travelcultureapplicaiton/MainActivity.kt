package com.example.travelcultureapplicaiton

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.travelcultureapplicaiton.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

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
        setSupportActionBar(binding.toolbar)

        // 가져온 유저 정보 프래그먼트에 옮기기
//        var homeFragment = HomeFragment()
//        var bundle = Bundle()
//        bundle.putString("userData", intent.getStringExtra("userData").toString())
//        homeFragment.arguments = bundle

        // 프래그먼트 연결하기
        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_activity_main, HomeFragment()).commit()

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