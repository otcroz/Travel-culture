package com.example.travelcultureapplicaiton

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.travelcultureapplicaiton.databinding.ActivityAddPostBinding
import com.example.travelcultureapplicaiton.databinding.DialogLocationSearchBinding
import com.example.travelcultureapplicaiton.databinding.ItemDialogSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddPostActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddPostBinding
    lateinit var filePath: String
    private lateinit var call: Call<responseInfo>
    private lateinit var dialogBinding: DialogLocationSearchBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPostBinding.inflate(layoutInflater)
        dialogBinding = DialogLocationSearchBinding.inflate(layoutInflater)

        // 위치 검색
        //검색 메뉴에 대한 코드
        var searchViewTextListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
                override fun onQueryTextSubmit(s: String): Boolean {
                    // 데이터를 가져온다.
                    //val returnType = arguments?.getString("returnType")
                    call = MyApplication.networkServiceXml.getXmlList(
                        "CDNRFWzcqVNIQ++7vj9QCBoCKvsk5fAEh/nT6XXO+49SR7SN2qEWcX9vTorvWC1Zsgn1VGftwEZslejzAUs/ww==",
                        1,
                        10,
                        "ETC",
                        "TravelCultureApp",
                        "O",
                        12,
                        s
                    )
                    //서버로부터 전달받은 내용 처리
                    call?.enqueue(object: Callback<responseInfo> {
                        override fun onResponse(call: Call<responseInfo>, response: Response<responseInfo>) {
                            Log.d("appTest", "$call / $response")
                            if(response.isSuccessful){
                                Log.d("appTest", "$response")
                                val dialogAdapter = AdapterDialogSearch(baseContext, response.body()!!.body!!.items!!.item)

                                dialogBinding.dialogSearchRecyclerView.layoutManager = LinearLayoutManager(baseContext)
                                dialogBinding.dialogSearchRecyclerView.adapter = dialogAdapter

                                // 리사이클러뷰 이벤트 처리
                                dialogAdapter.setItemClickListener(object: AdapterDialogSearch.OnItemClickListener{
                                    override fun onClick(v: View, position: Int) {
                                        // 클릭 시 이벤트 작성
                                        val uniqueContent =  response.body()!!.body!!.items!!.item
                                        // !! 클릭했을 때 아이템 배경 색 바꾸기
                                        binding.storeContentID.text = uniqueContent[position].contentid.toString()
                                        binding.addLocate.text = uniqueContent[position].addr1
                                        binding.addResidence.text = uniqueContent[position].title
                                    }
                                })
                            }
                        }

                        override fun onFailure(call: Call<responseInfo>, t: Throwable) {
                            Toast.makeText(baseContext,"해당하는 검색 결과가 없습니다.",  Toast.LENGTH_SHORT).show()
                            Log.d("appTest", "onFailure")
                            Log.d("appTest", "$t")
                        }

                    })

                    return false
                }

                //텍스트 입력/수정시에 호출
                override fun onQueryTextChange(s: String): Boolean {

                    Log.d("appTest", "SearchVies Text is changed : $s")
                    return false
                }
            }
        // 검색메뉴에 대한 코드
        dialogBinding.dialogSearch.setOnQueryTextListener(searchViewTextListener)

        // 커스텀 다이얼로그 작업
        val alert = AlertDialog.Builder(this)
            .setTitle("위치 검색")
            .setView(dialogBinding.root)
            .setPositiveButton("확인", null)
            .create()

        // 위치 레이아웃을 눌렀을 때
        binding.postAreaLocation.setOnClickListener{
            android.app.AlertDialog.Builder(this).run {
                alert.show()
            }
        }

        // 갤러리와 연결
        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode === android.app.Activity.RESULT_OK){
                Glide
                    .with(applicationContext)
                    .load(it.data?.data) // 이미지 위치에 대한 정보를 가져온다.
                    //.apply(RequestOptions().override(250,200))
                    .centerCrop()
                    .into(binding.addImageView) // 이미지를 넣을 위치
                val cursor = contentResolver.query(it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null);
                cursor?.moveToFirst().let { // 사용자가 선택한 이미지에 대한 (0번째 앞의 데이터?) 파일 경로를 가져와 string으로 변환
                    filePath = cursor?.getString(0) as String
                }
            }
        }

        // 갤러리 버튼을 눌렀을 때
        binding.addImageBtn.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        }

        // 포스트 게시 버튼을 눌렀을 때
        binding.addPostRecommend.setOnClickListener {
            if(binding.addImageView.drawable !== null && binding.addEditView.text.isNotEmpty() && binding.addLocate.text !== null){
                saveStore()
            }
            else{
                Toast.makeText(this, "글을 작성해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        setContentView(binding.root)
    }

    private fun saveStore(){ // id, 입력된 내용, 입력 시간 정보
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val name = sharedPreferences.getString("username", "")
        
        //db 불러오기
        MyApplication.db.collection("user").document(MyApplication.auth?.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                val resultItem = result.toObject(UserDataModel::class.java)
                // 이름 반영하기
                var imagePath = resultItem?.imagefile

                // 데이터 추가하기
                val data = mapOf(
                    "email" to MyApplication.email, // read, write 권한을 가지도록 하기위해 설정
                    "content" to binding.addEditView.text.toString(),
                    "date" to dateToString(Date()),
                    "residence" to binding.addResidence.text.toString(),
                    "locate" to binding.addLocate.text.toString(),
                    "contentid" to binding.storeContentID.text,
                    "nickname" to "$name",
                    "imageFile" to "$imagePath"
                )

                MyApplication.db.collection("post") // 파이어스토어에 업데이트
                    .add(data)
                    .addOnSuccessListener {
                        uploadImage(it.id)
                        Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Log.d("mobileApp", "data save error")
                    }
            }
            .addOnFailureListener{
                Toast.makeText(this,"서버 데이터 획득 실패",  Toast.LENGTH_SHORT).show()
            }

    }

    private fun uploadImage(docId: String){
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("postImages/${docId}.jpg") // 스토리지에 저장

        //원본 이미지를 스토리지에 업로드
        val file= Uri.fromFile(File(filePath))
        imageRef.putFile(file)
            .addOnSuccessListener {
                Toast.makeText(this, "save ok..", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Log.d("mobileApp", "file save error")
            }
    }

    fun dateToString(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }
}