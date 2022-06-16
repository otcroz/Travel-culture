package com.example.travelcultureapplicaiton

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelcultureapplicaiton.databinding.ItemPostRecommendBinding

class ViewHolderRecommand(val binding: ItemPostRecommendBinding) : RecyclerView.ViewHolder(binding.root)
class AdapterRecommand(val context: Context, val itemList: MutableList<ListRecommandData>): RecyclerView.Adapter<ViewHolderRecommand>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRecommand {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ViewHolderRecommand(ItemPostRecommendBinding.inflate(layoutInflater))
        }

        override fun onBindViewHolder(holder: ViewHolderRecommand, position: Int) {
            val data = itemList.get(position)
            holder.binding.run{
                itemNicknameView.text = data.nickname
                itemDateView.text = data.date
                itemContentView.text = data.content
            }

            // 이미지를 storage로부터 app에 가져온다.
            // 다운받은 이미지에 대한 정보 확보
            val imageRef = MyApplication.storage.reference.child("postImages/${data.docId}.jpg")
            imageRef.downloadUrl.addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    Glide.with(context)
                        .load(task.result) // 이미지에 대한 정보 로드
                        .into(holder.binding.itemImageView)
                }
            }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }


    }