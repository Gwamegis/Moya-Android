package com.soi.moya

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class SearchFragment : Fragment() {

    private lateinit var viewModel: MusicViewModel
    private lateinit var adapter: SongListViewAdapter
    private var musicData: List<MusicModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val listView = view.findViewById<ListView>(R.id.searchResultListView)
        val hintText = view.findViewById<TextView>(R.id.searchHintTextView)
        val emptyText = view.findViewById<TextView>(R.id.emptyListTextView)

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
                if (query.isNullOrBlank()) {
                    emptyText.visibility = View.GONE
                    listView.visibility = View.GONE
                    hintText.visibility = View.VISIBLE
                } else {
                    adapter.filter.filter(query)
                    val filteredResult = musicData.filter { it.title.contains(query, true) }
                    emptyText.visibility = if (filteredResult.isEmpty()) View.VISIBLE else View.GONE
                    listView.visibility = if (filteredResult.isEmpty()) View.GONE else View.VISIBLE
                    hintText.visibility = View.GONE
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    emptyText.visibility = View.GONE
                    listView.visibility = View.GONE
                    hintText.visibility = View.VISIBLE
                } else {
                    adapter.filter.filter(newText)
                    listView.visibility = View.VISIBLE
                    val filteredResult = musicData.filter { it.title.contains(newText, true) }
                    emptyText.visibility = if (filteredResult.isEmpty()) View.VISIBLE else View.GONE
                    hintText.visibility = View.GONE
                }
                return true
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
    }

}