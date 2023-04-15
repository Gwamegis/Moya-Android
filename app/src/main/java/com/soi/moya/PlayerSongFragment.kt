package com.soi.moya

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

private lateinit var viewModel: MusicViewModel
class PlayerSongFragment : Fragment() {

    private var musicData: List<MusicModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_song, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)

        val listView = view.findViewById<ListView>(R.id.songListView)


        viewModel.fetchData().observe(viewLifecycleOwner, Observer { data ->
            musicData = data.filter { musicModel ->
                musicModel.type
            }

            val adapter = SongListViewAdapter(musicData)
            listView.adapter = adapter
        })

        listView.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(requireContext(), PlaySongActivity::class.java)
            intent.putExtra("title", musicData[i].title)
            intent.putExtra("lyrics", musicData[i].lyrics)
            intent.putExtra("url", musicData[i].url)
            startActivity(intent)
        }


        return view
    }
}