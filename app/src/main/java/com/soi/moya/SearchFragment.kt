package com.soi.moya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView

class SearchFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val result = arrayOf("abc", "Anvi", "ajsi", "sjti", "wneit", "vnditl", "djiwtk")
        val resultAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, result)
        val listView = view.findViewById<ListView>(R.id.searchResultListView)
        listView.adapter = resultAdapter

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                if (result.contains(query)) {
                    resultAdapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(nextText: String?): Boolean {
                resultAdapter.filter.filter(nextText)
                return false
            }

        })

        return view
    }
}