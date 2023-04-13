package com.soi.moya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.soi.moya.databinding.ActivityMainBinding

class HomeFragment : Fragment() {

    val listItem = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listItem.add("song1")
        listItem.add("song2")
        listItem.add("song3")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val listView = view.findViewById<ListView>(R.id.songListView)
        val adapter = SongListViewAdapter(listItem)
        listView.adapter = adapter

        return view
    }
}