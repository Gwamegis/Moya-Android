package com.soi.moya

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager,
                       lifecycle: Lifecycle,
                       private val musicData: List<MusicModel>) : FragmentStateAdapter(fragmentManager,
    lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TeamSongFragment()
            1 -> PlayerSongFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}