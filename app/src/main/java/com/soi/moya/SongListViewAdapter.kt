package com.soi.moya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.soi.moya.databinding.ActivityMainBinding
import java.util.*

class SongListViewAdapter(val songList: List<MusicModel>): BaseAdapter(), Filterable {

    private var filteredList: List<MusicModel> = songList

    override fun getCount(): Int {
        return filteredList.size
    }

    override fun getItem(position: Int): Any {
        return filteredList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var convertView = convertView

        if (convertView == null) {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.song_listview_item, parent, false)
        }

        val title = convertView!!.findViewById<TextView>(R.id.songListViewItem)
        title.text = songList[position].title

        return convertView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.toLowerCase(Locale.getDefault())
                val filterResults = FilterResults()
                filterResults.values = if (query == null || query.isEmpty()) {
                    songList
                } else {
                    songList.filter { musicModel ->
                        musicModel.title.toLowerCase(Locale.getDefault()).contains(query)
                    }
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<MusicModel>
                notifyDataSetChanged()
            }
        }
    }
}