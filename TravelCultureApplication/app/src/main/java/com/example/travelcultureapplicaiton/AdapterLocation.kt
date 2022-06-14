package com.example.travelcultureapplicaiton

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelcultureapplicaiton.databinding.ItemListBinding
import com.example.travelcultureapplicaiton.databinding.ItemListLocateBinding

class ViewHolderLocation(val binding: ItemListLocateBinding): RecyclerView.ViewHolder(binding.root) // 뷰 홀더에 대한 생성
class AdapterLocation(val context: Context, val datas:MutableList<myItem_locate>?):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderLocation(ItemListLocateBinding.inflate(LayoutInflater.from(parent.context), parent,false ))
    }

    override fun getItemCount(): Int {
        Log.d("appTest", "${datas?.size}")
        return datas?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ViewHolderLocation).binding
        val model = datas!![position]
        Glide.with(binding.root)
            .load(model.firstimage2)
            .override(150,200)
            .into(binding.itemImageLocate)
        binding.itemTitleLocate.text = model.title
        binding.itemAddrLocate.text = model.addr1

        // (1) 리스트 내 항목 클릭 시 onClick() 호출
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener



}