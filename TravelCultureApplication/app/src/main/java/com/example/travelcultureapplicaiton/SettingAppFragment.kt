package com.example.travelcultureapplicaiton

import android.app.*
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.*
import com.example.travelcultureapplicaiton.Constant.Companion.NOTIFICATION_ID
import com.example.travelcultureapplicaiton.MyApplication.Companion.db
import com.kakao.sdk.user.UserApiClient
import java.util.*


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

    private var alarmManager: AlarmManager? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
        setPreferencesFromResource(R.xml.setting, rootKey)

        //설정값 가져오기
        alarmManager = requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager?
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity as SettingActivity)

        val nicknamePreference: EditTextPreference? = findPreference("username")
        val favoriteCategory: MultiSelectListPreference? = findPreference("category2")
        val checkLocation: ListPreference? = findPreference("location1")
        val logoutPreference: Preference? = findPreference("logout_mode")
        val withdrawPreference: Preference? = findPreference("withdraw_mode")
        val locate_alarmPreference: SwitchPreference? = findPreference("noti_sound1")
        val category_alarmPreference: SwitchPreferenceCompat? = findPreference("noti_sound2")

        setAlarm(6,51)

        // 지역 알림
        if(locate_alarmPreference?.isChecked == true){

        } else{
            cancelAlarm()
        }
        
        // 초기값 설정
        var updateNickname = EditTextPreference.SimpleSummaryProvider.getInstance()
        nicknamePreference?.summaryProvider = updateNickname

        var updateCheck = ListPreference.SimpleSummaryProvider.getInstance()
        checkLocation?.summaryProvider = updateCheck

        favoriteCategory?.summaryProvider =
            Preference.SummaryProvider<MultiSelectListPreference> { preference ->
                val array = mutableSetOf("")
                val t1 = preference.getPersistedStringSet(array)
                if (t1.toString().isEmpty()) {
                    "카테고리를 설정해주세요."
                } else {
                    Log.d("appTest", "$t1")
                    val convertList = t1.toList()
                    "$t1"
                }
            }
        true

        // 닉네임
        nicknamePreference?.setOnPreferenceChangeListener { preference, newValue ->
            Log.d("appTest", "preference: $preference newValue: $newValue")
            var updateNickname = EditTextPreference.SimpleSummaryProvider.getInstance()
            nicknamePreference?.summaryProvider = updateNickname
            db.collection("user").document(MyApplication.auth?.currentUser?.uid.toString()).update("nickname", newValue)

            true
        }

        // 거주지역
        checkLocation?.setOnPreferenceChangeListener { preference, newValue ->
            Log.d("appTest", "preference: $preference newValue: $newValue")
            var updateCheck = ListPreference.SimpleSummaryProvider.getInstance()
            checkLocation?.summaryProvider = updateCheck
            db.collection("user").document(MyApplication.auth?.currentUser?.uid.toString()).update("residence", newValue)

            true
        }

        // 카테고리
        favoriteCategory?.setOnPreferenceChangeListener { preference, newValue ->
            Log.d("appTest", "preference: $preference newValue: $newValue")
            favoriteCategory?.summaryProvider =
                Preference.SummaryProvider<MultiSelectListPreference> { preference ->
                    val array = mutableSetOf("")
                    val t1 = preference.getPersistedStringSet(array)
                    if (t1.toString().isEmpty()) {
                        "카테고리를 설정해주세요."
                    } else {
                        Log.d("appTest", "$t1")
                        val convertList = t1.toList()
                        db.collection("user").document(MyApplication.auth?.currentUser?.uid.toString()).update("category", convertList)
                        "$t1"
                    }
                }
            true
        }

        // 로그아웃 이벤트 핸들러
        val eventLogout = object : DialogInterface.OnClickListener{
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
                    val fragmentManager: FragmentManager = activity!!.supportFragmentManager
                    (activity as SettingActivity).finish()
                    val intent = Intent(activity, SplashActivity::class.java)
                    startActivity(intent)

                    // 저장된 로그인 정보 삭제(프리퍼런스에 저장된 값 지우기)
                } else if(p1 == DialogInterface.BUTTON_NEGATIVE)
                    Log.d("appTest", "negative button")
            }
        }

        // 로그아웃
        logoutPreference?.setOnPreferenceClickListener { preference ->
            Log.d("appTest","setOnPreferenceClickListener")
            AlertDialog.Builder(activity).run {
                setTitle("앱 로그아웃")
                setIcon(android.R.drawable.ic_dialog_info)
                setMessage("정말 로그아웃하시겠습니까?")
                setPositiveButton("네", eventLogout)
                setNegativeButton("아니오", null)
                setCancelable(false)
                show()
            }.setCanceledOnTouchOutside(false) // 메시지 값 출력
            true
        }

        // 회원탈퇴 이벤트 핸들러
        val eventWithDraw = object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if(p1 == DialogInterface.BUTTON_POSITIVE){
                    // 1. DB 정보 삭제
                    Log.d("appTest", "${MyApplication.auth?.currentUser?.uid.toString()}")
                    db.collection("user").document(MyApplication.auth?.currentUser?.uid.toString())
                        .delete()
                        .addOnSuccessListener {
                            Log.d("appTest", "DocumentSnapshot successfully deleted!")
                            // 2. 앱 탈퇴
                            MyApplication.email = null
                            MyApplication.auth.currentUser!!.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(activity, "회원탈퇴 성공", Toast.LENGTH_SHORT).show()
                                    }
                                    else{
                                        Toast.makeText(activity, "회원탈퇴 실패", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            // 화면 이동
                            activity?.supportFragmentManager
                            val fragmentManager: FragmentManager = activity!!.supportFragmentManager
                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e -> Log.w("appTest", "Error deleting document", e) }

                    
                } else if(p1 == DialogInterface.BUTTON_NEGATIVE)
                    Log.d("appTest", "negative button")
            }
        }


        // 회원 탈퇴
        withdrawPreference?.setOnPreferenceClickListener { preference ->
            Log.d("appTest","setOnPreferenceClickListener")
            AlertDialog.Builder(activity).run {
                setTitle("앱 회원 탈퇴")
                setIcon(android.R.drawable.ic_dialog_info)
                setMessage("정말 탈퇴하시겠습니까?")
                setPositiveButton("네", eventWithDraw)
                setNegativeButton("아니오", null)
                setCancelable(false)
                show()
            }.setCanceledOnTouchOutside(false) // 메시지 값 출력
            true
        }
    }

    private fun cancelAlarm() {
        val receiverIntent = Intent(activity, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, receiverIntent, 0)
        alarmManager?.cancel(pendingIntent)
    }

    private fun setAlarm(hour: Int, minute: Int) {
        //옵션값에 따라서, 푸시 설정이 되지 않을 수 있도록 함
//        if (!pushAvailable.value) return

        //AlarmReceiver에 값 전달
        val receiverIntent = Intent(activity, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(activity as SettingActivity, 0, receiverIntent, PendingIntent.FLAG_MUTABLE)

        //alarm 등록 전, 이전 push cancel
        alarmManager?.cancel(pendingIntent)

        // Set the alarm to start at time and minute
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (calendar.time < Date()) { //설정한 시간에 따라, 알람이 설정이 안될 수 있으므로 달 첫번째 부터의 시간을 설정
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}