package com.soi.moya

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

    private var pointColor: Int = 0
    private var backgroundColor: Int = 0
    private var subColor: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        tabs = view.findViewById(R.id.tabLayout)
        val backgroundView = view.findViewById<View>(R.id.backgroundView)

        val prefs = requireContext().getSharedPreferences("selected_team", Context.MODE_PRIVATE)
        val selectedTeamName = prefs.getString("selected_team", "")
        fetchColor(selectedTeamName ?: "hanwha")
        backgroundView.setBackgroundResource(subColor)
        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), pointColor))
        tabs.setBackgroundColor(ContextCompat.getColor(requireContext(), subColor))


        val selectedTeam = arguments?.getString("selectedTeam")
        fetchColor(selectedTeam!!)

        tabs.setTabTextColors(Color.parseColor("#66ffffff"), Color.parseColor("#ffffff"))

        viewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)

        viewModel.fetchData().observe(viewLifecycleOwner, Observer { musicList ->
            val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, musicList)
            viewPager.adapter = adapter

            TabLayoutMediator(tabs, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "팀 응원가"
                    1 -> tab.text = "선수 응원가"
                }
            }.attach()
        })

        val mainBanner = view.findViewById<ImageView>(R.id.mainBannerImage)
        mainBanner.setOnClickListener {
            val intent = Intent(requireActivity(), SelectTeamActivity::class.java)
            startActivity(intent)
        }

        val mainBannerImageSrc =
            resources.getIdentifier("main_banner_${selectedTeamName}", "drawable", "com.soi.moya")
        mainBanner.setImageResource(mainBannerImageSrc)

        mainBanner.clipToOutline = true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private const val ARG_DATA = "data"

        fun newInstance(data: MutableList<MusicModel>) = HomeFragment().apply {
            arguments = bundleOf(ARG_DATA to data)
        }
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), subColor)
    }

    @SuppressLint("DiscouragedApi")
    private fun fetchColor(selectedTeam: String) {
        pointColor = resources.getIdentifier("${selectedTeam}_point", "color", "com.soi.moya")
        backgroundColor =
            resources.getIdentifier("${selectedTeam}_background", "color", "com.soi.moya")
        subColor = resources.getIdentifier("${selectedTeam}_sub", "color", "com.soi.moya")
    }
}

