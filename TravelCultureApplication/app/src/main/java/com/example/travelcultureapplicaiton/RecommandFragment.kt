package com.example.travelcultureapplicaiton

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelcultureapplicaiton.databinding.FragmentRecommandBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CourseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CourseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding : FragmentRecommandBinding

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
        binding = FragmentRecommandBinding.inflate(inflater, container, false)
        Log.d("appTest","onCreateView")
        binding.addPostBtn.setOnClickListener{
            // 포스트 작성 화면으로 이동
            val intent = Intent(activity, AddPostActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun getUID(): String? {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("uid",
            Context.MODE_PRIVATE
        )
        val uid = sharedPreferences.getString("uid", "")

        return uid
    }

    override fun onStart() { // mainActivity에서 다른 activity로 이동하여 다른 작업 후 다시 돌아올 때 실행하는 메서드
        super.onStart()
        if(MyApplication.checkAuth() || MyApplication.email != null || getUID() != null) { // 검증된 이메일인지 확인
            makeRecyclerView()
        }
        else { // 검증된 이메일이 아니면 포스트 작성을 하지 못하도록 한다.
            binding.addPostBtn.visibility = View.GONE
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CourseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CourseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun makeRecyclerView(){ // 파이어스토어에 저장된 정보를 가져온다.
        Log.d("appTest","makeRecyclerView")
        MyApplication.db.collection("post")
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<ListRecommandData>()
                for(document in result) { // for 문으로 data를 가져옴, itemList에 내용 저장
                    val item = document.toObject(ListRecommandData::class.java)
                    item.docId = document.id
                    itemList.add(item)
                }
                if(itemList.size == 0){
                    binding.textNull.visibility = View.VISIBLE
                    binding.mainRecyclerView.visibility = View.GONE
                }
                binding.mainRecyclerView.layoutManager = LinearLayoutManager(activity as MainActivity)
                binding.mainRecyclerView.adapter = AdapterRecommand(activity as MainActivity, itemList)
            }
            .addOnFailureListener{
                Log.d("appTest","addOnFailureListener")
                Toast.makeText(activity as MainActivity,"서버 데이터 획득 실패",  Toast.LENGTH_SHORT).show()
            }
    }
}