package com.example.travelcultureapplicaiton

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.*
import com.kakao.sdk.user.UserApiClient


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingAppFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)

        val favoriteCategory: MultiSelectListPreference? = findPreference("category2")
        val checkLocation: ListPreference? = findPreference("location1")
        val logoutPreference: Preference? = findPreference("logout_mode")
        checkLocation?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()

        favoriteCategory?.summaryProvider =
            Preference.SummaryProvider<MultiSelectListPreference> { preference ->
                val array = mutableSetOf("")
                val t1 = preference.getPersistedStringSet(array)
                if (t1.toString().isEmpty()) {
                    "카테고리를 설정해주세요."
                } else {
                    Log.d("appTest", "$t1")
                    "$t1"
                }
            }

        val eventHandler = object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if(p1 == DialogInterface.BUTTON_POSITIVE){
                    // 앱 로그아웃
                    MyApplication.auth.signOut()
                    MyApplication.email = null
                    UserApiClient.instance.logout { error ->
                        if(error != null){
                            Toast.makeText(activity, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(activity, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                        }
                    }
                    activity?.supportFragmentManager
                    //(activity as SettingActivity).finishAffinity();
                    val fragmentManager: FragmentManager = activity!!.supportFragmentManager
                    (activity as SettingActivity).finish()

                    // 저장된 로그인 정보 삭제
                } else if(p1 == DialogInterface.BUTTON_NEGATIVE)
                    Log.d("appTest", "negative button")
            }
        }

        logoutPreference?.setOnPreferenceClickListener { preference ->
            Log.d("appTest","setOnPreferenceClickListener")
            AlertDialog.Builder(activity).run {
                setTitle("앱 로그아웃")
                setIcon(android.R.drawable.ic_dialog_info)
                setMessage("정말 로그아웃하시겠습니까?")
                setPositiveButton("네", eventHandler)
                setNegativeButton("아니오", eventHandler)
                setCancelable(false)
                show()
            }.setCanceledOnTouchOutside(false) // 메시지 값 출력
            true
        }
    }

}