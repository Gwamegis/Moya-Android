package com.soi.moya

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class SearchFragment : Fragment() {

    private lateinit var viewModel: MusicViewModel
    private lateinit var adapter: SongListViewAdapter
    private var musicData: List<MusicModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val listView = view.findViewById<ListView>(R.id.searchResultListView)

        viewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)
        adapter = SongListViewAdapter(emptyList())
        listView.adapter = adapter

        viewModel.fetchData().observe(viewLifecycleOwner, Observer { data ->
            musicData = data
            adapter = SongListViewAdapter(musicData)
            listView.adapter = adapter
        })

        listView.setOnItemClickListener { adapterView, view, i, l ->
            val musicModel = adapter.getItem(i) as MusicModel
            val intent = Intent(requireContext(), PlaySongActivity::class.java)
            intent.putExtra("title", musicModel.title)
            intent.putExtra("lyrics", musicModel.lyrics)
            intent.putExtra("url", musicModel.url)
            startActivity(intent)
        }

        val searchView = view.findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                val titleList = musicData.map { it.title }
                if (titleList.contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    adapter.filter.filter("")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        return view
    }
}