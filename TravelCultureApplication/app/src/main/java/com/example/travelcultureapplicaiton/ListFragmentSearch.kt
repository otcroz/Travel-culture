package com.example.travelcultureapplicaiton

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelcultureapplicaiton.databinding.FragmentListSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragmentSearch.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragmentSearch : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var call: Call<responseInfo>
    lateinit var binding : FragmentListSearchBinding

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
        binding = FragmentListSearchBinding.inflate(inflater, container, false)

        //검색 메뉴에 대한 코드
        var searchViewTextListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
                override fun onQueryTextSubmit(s: String): Boolean {
                    return false
                }

                //텍스트 입력/수정시에 호출
                override fun onQueryTextChange(s: String): Boolean {
                    // 데이터를 가져온다.
                    val returnType = arguments?.getString("returnType")
                    call = MyApplication.networkServiceXml.getXmlList(
                        "CDNRFWzcqVNIQ++7vj9QCBoCKvsk5fAEh/nT6XXO+49SR7SN2qEWcX9vTorvWC1Zsgn1VGftwEZslejzAUs/ww==",
                        1,
                        10,
                        "ETC",
                        "TravelCultureApp",
                        "O",
                        s
                    )
                    //서버로부터 전달받은 내용 처리
                    call?.enqueue(object: Callback<responseInfo> {
                        override fun onResponse(call: Call<responseInfo>, response: Response<responseInfo>) {
                            Log.d("appTest", "$call / $response")
                            if(response.isSuccessful){
                                Log.d("appTest", "$response")
                                binding.listSearchRecyclerView.layoutManager = LinearLayoutManager(activity)
                                binding.listSearchRecyclerView.adapter = MyAdapter(activity as Context, response.body()!!.body!!.items!!.item)
                            }
                        }

                        override fun onFailure(call: Call<responseInfo>, t: Throwable) {
                            Log.d("appTest", "onFailure")
                            Log.d("appTest", "$t")
                        }

                    })
                    Log.d("appTest", "SearchVies Text is changed : $s")
                    return false
                }
            }
        // 검색메뉴에 대한 코드
        binding.menuSearch.setOnQueryTextListener(searchViewTextListener)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragmentSearch.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragmentSearch().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}