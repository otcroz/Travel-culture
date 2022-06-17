package com.example.travelcultureapplicaiton

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelcultureapplicaiton.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding : FragmentHomeBinding
    private lateinit var call: Call<responseInfo_area>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 바인딩 설정
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        var areaNum = 1
        var categoryNum = "A0202"

        callData(areaNum, categoryNum)

        // 유저 정보 업로드(이메일, 추후에 닉네임으로 변경)
        //binding.username.text =

        return binding.root
    }

    private fun callData(areaNum: Int, categoryNum: String){
        val returnType = arguments?.getString("returnType")
        call = MyApplication.networkSetvice_area.getAreaXmlList(
            "CDNRFWzcqVNIQ++7vj9QCBoCKvsk5fAEh/nT6XXO+49SR7SN2qEWcX9vTorvWC1Zsgn1VGftwEZslejzAUs/ww==",
            1,
            10,
            "ETC",
            "TravelCultureApp",
            "O",
            areaNum,
            "A02",
            categoryNum,
            12,
        )
        //서버로부터 전달받은 내용 처리
        call?.enqueue(object: Callback<responseInfo_area> {
            override fun onResponse(call: Call<responseInfo_area>, response: Response<responseInfo_area>) {
                Log.d("appTest", "$call / $response")
                if(response.isSuccessful){
                    Log.d("appTest", "$response")
                    val areaAdapter = AdapterHomeRecommand(activity as Context, response.body()!!.body!!.items!!.item)
                    binding.homeRecommandRecyclerView.layoutManager =  GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false);
                    binding.homeRecommandRecyclerView.adapter = areaAdapter

                    // 리사이클러뷰 이벤트 처리
                    areaAdapter.setItemClickListener(object: AdapterHomeRecommand.OnItemClickListener{
                        override fun onClick(v: View, position: Int) {
                            // 클릭 시 이벤트 작성
                            val uniqueContent = response.body()!!.body!!.items!!.item
                            Log.d("appTest", "${uniqueContent[position]!!.contentid}")

                            val uniqueContentNum = uniqueContent[position]!!.contentid

                            val intent = Intent(activity, DetailActivity::class.java)
                            //contentID 넘기기
                            intent.putExtra("contentID", uniqueContentNum)
                            startActivity(intent)
                        }
                    })
                }
            }

            override fun onFailure(call: Call<responseInfo_area>, t: Throwable) {
                Log.d("appTest", "onFailure")
                Log.d("appTest", "$t")
            }

        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}