package com.soi.moya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.activityViewModels


class TeamSongFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_team_song, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = SongListViewAdapter(viewModel.teamMusicList)
        val listView = view.findViewById<ListView>(R.id.songListView)
        listView.adapter = adapter
    }

    companion object {
        fun newInstance(): TeamSongFragment {
            return TeamSongFragment()
        }
    }
}