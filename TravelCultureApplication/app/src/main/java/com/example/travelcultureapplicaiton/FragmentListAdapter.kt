package com.example.travelcultureapplicaiton

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentListAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    val fragments: List<Fragment>
    init {
        fragments = listOf(ListFragmentLocate(), ListFragmentSearch()) // 2개의 프레그먼트 등록
    }
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}