package com.example.travelcultureapplicaiton

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.travelcultureapplicaiton.databinding.ActivityDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailBinding
    private lateinit var call: Call<responseInfo_detail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)


        // 리사이클러뷰에서 이벤트 발생 시 데이터 정보 가져옴
        var contentID = intent.getIntExtra("contentID", 0)
        Log.d("appTest", "$contentID")



        // DATA API
        //val returnType = arguments?.getString("returnType")
        call = MyApplication.networkSetvice_detail.getDetailXmlList(
            "CDNRFWzcqVNIQ++7vj9QCBoCKvsk5fAEh/nT6XXO+49SR7SN2qEWcX9vTorvWC1Zsgn1VGftwEZslejzAUs/ww==",
            1,
            10,
            "ETC",
            "TravelCultureApp",
            contentID,
            "Y",
            "Y",
            "Y",
            "Y",
            "Y"
        )
        //서버로부터 전달받은 내용 처리
        call?.enqueue(object: Callback<responseInfo_detail> {
            override fun onResponse(call: Call<responseInfo_detail>, response: Response<responseInfo_detail>) {
                Log.d("appTest", "$call / $response")
                if(response.isSuccessful){
                    Log.d("appTest", "$response")

                    // 불러온 데이터 적용하기
                    val detailData = response.body()!!.body!!.items!!.item
                    Log.d("appTest", "${response.body()!!.body!!.items!!.item}")
                    binding.detailName.text = detailData[0].title
                    binding.detailContent.text = detailData[0].overview
                    binding.detailLocation.text = detailData[0].addr1
                    Glide.with(binding.root)
                        .load(detailData[0].firstimage)
                        .override(300,200)
                        .into(binding.detailImage)

                }
            }

            override fun onFailure(call: Call<responseInfo_detail>, t: Throwable) {
                Log.d("appTest", "onFailure")
                Log.d("appTest", "$t")
            }

        })


        
        setContentView(binding.root)
    }
}