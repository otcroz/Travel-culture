package com.example.travelcultureapplicaiton

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ViewHolderLocation).binding
        val model = datas!![position]
        Glide.with(binding.root)
            .load(model.firstimage2)
            .override(150,200)
            .into(binding.itemImageLocate)
        binding.itemTitleLocate.text = model.title
        binding.itemAddrLocate.text = model.addr1
    }

    override fun getItemCount(): Int {
        Log.d("appTest", "${datas?.size}")
        return datas?.size ?: 0
    }

}