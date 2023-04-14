package com.soi.moya

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

private const val MUSIC_LIST = "musicList"
private lateinit var viewModel: MusicViewModel

class HomeFragment : Fragment() {
    private lateinit var musicData: List<MusicModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val mainBanner = view.findViewById<ImageView>(R.id.mainBannerImage)

        mainBanner.clipToOutline = true

        val listView = view.findViewById<ListView>(R.id.songListView)

        viewModel.fetchData().observe(viewLifecycleOwner, Observer { data ->
            val adapter = SongListViewAdapter(data)
            listView.adapter = adapter
        })

        listView.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(requireContext(), PlaySongActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {
        private const val ARG_DATA = "data"

        fun newInstance(data: MutableList<MusicModel>) = HomeFragment().apply {
            arguments = bundleOf(ARG_DATA to data)
        }
    }
}