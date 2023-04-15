package com.soi.moya

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val MUSIC_LIST = "musicList"
private lateinit var viewModel: MusicViewModel

class HomeFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabs: TabLayout
    private lateinit var viewModel: MusicViewModel
    private val musicViewModel: MusicViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val pointColor = ContextCompat.getColor(requireContext(), R.color.dosan_point)
        viewPager = view.findViewById(R.id.viewPager)
        tabs = view.findViewById(R.id.tabLayout)

        tabs.setSelectedTabIndicatorColor(pointColor)
        tabs.setTabTextColors(Color.parseColor("#66ffffff"), Color.parseColor("#ffffff"))

        viewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)

        viewModel.fetchData().observe(viewLifecycleOwner, Observer { musicList ->
            val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, musicList)
            viewPager.adapter = adapter

            TabLayoutMediator(tabs, viewPager) { tab, position ->
                when(position) {
                    0 -> tab.text = "팀 응원가"
                    1 -> tab.text = "선수 응원가"
                }
            }.attach()
        })

        val mainBanner = view.findViewById<ImageView>(R.id.mainBannerImage)

        mainBanner.clipToOutline = true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchData().observe(viewLifecycleOwner, Observer { musicList ->
            val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, musicList)
            viewPager.adapter = adapter
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                when(position) {
                    0 -> tab.text = "팀 응원가"
                    1 -> tab.text = "선수 응원가"
                }
            }.attach()
        })
    }

    companion object {
        private const val ARG_DATA = "data"

        fun newInstance(data: MutableList<MusicModel>) = HomeFragment().apply {
            arguments = bundleOf(ARG_DATA to data)
        }
    }
}

