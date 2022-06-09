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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.travelcultureapplicaiton.databinding.FragmentListBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding : FragmentListBinding

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
        binding = FragmentListBinding.inflate(inflater, container, false)

        initViewPager() // 뷰 페이저 적용하기

        // 탭 바 적용하기
        val tabLayoutTextArray = arrayOf("거리순","검색","카테고리")
        TabLayoutMediator(binding.tab1, binding.viewpager){
                tab, position -> tab.text = tabLayoutTextArray[position]
        }.attach()
        
        //탭 바 이벤트
        binding.tab1.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                //탭을 이동할 때 이미지 보여줌
                when(tab?.text){
                    // 이미지 적용하기
                    "거리순" -> Glide.with(binding.root)
                        .load("https://t1.daumcdn.net/cfile/blog/272CB84E534BF57928")
                        .override(150,200)
                        .into(binding.listpageImage)
                    "검색" -> Glide.with(binding.root)
                        .load("@drawable/ic_splash_logo")
                        .override(150,200)
                        .into(binding.listpageImage)
                    "카테고리" -> Glide.with(binding.root)
                        .load("@drawable/ic_splash_logo")
                        .override(150,200)
                        .into(binding.listpageImage)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })

        return binding.root
    }

    private fun initViewPager(){
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        //binding.viewpager.offscreenPageLimit = 3;
        binding.viewpager.adapter = FragmentListAdapter(activity as MainActivity)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}