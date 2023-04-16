package com.soi.moya

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.soi.moya.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class SongListViewAdapter(private var songList: List<MusicModel>): BaseAdapter(), Filterable {

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
        title.text = filteredList[position].title

        return convertView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val query = constraint?.toString()?.lowercase()
                val filterResults = FilterResults()

                filterResults.values = songList.filter  {
                    it.title.contains(query!!)
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                filteredList = results?.values as ArrayList<MusicModel>
                notifyDataSetChanged()
            }

        }
    }

}