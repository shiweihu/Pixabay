package com.shiweihu.pixabayapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentsAdapter(activity: FragmentActivity):FragmentStateAdapter(activity) {

    private val fragments:Map<Int,() -> Fragment> = mapOf(
        PHOTOS to {PhotosFragmentGroup()},
        VIDEO to {VideoFragmentGroup()}
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

    companion object{
        const val PHOTOS = 0
        const val VIDEO = 1
    }


}