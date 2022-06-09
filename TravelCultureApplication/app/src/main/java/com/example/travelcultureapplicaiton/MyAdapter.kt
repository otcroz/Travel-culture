package com.example.travelcultureapplicaiton

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.travelcultureapplicaiton.databinding.ItemListBinding

class MyViewHolder(val binding: ItemListBinding): RecyclerView.ViewHolder(binding.root) // 뷰 홀더에 대한 생성
class MyAdapter(val context: Context, val datas:MutableList<myItem>?):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemListBinding.inflate(LayoutInflater.from(parent.context), parent,false ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        val model = datas!![position]
        //binding.itemImage.src = model.firstimage2
        binding.itemTitle.text = model.title
        binding.itemStartdate.text = model.eventstartdate
        binding.itemAddr.text = model.addr1
    }

    override fun getItemCount(): Int {
        Log.d("appTest", "${datas?.size}")
        return datas?.size ?: 0
    }

}