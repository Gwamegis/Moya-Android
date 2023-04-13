package com.soi.moya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.soi.moya.databinding.ActivityMainBinding

class SongListViewAdapter(val songList: MutableList<String>): BaseAdapter() {

    override fun getCount(): Int {
        return songList.size
    }

    override fun getItem(position: Int): Any {
        return songList[position]
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
        title.text = songList[position]

        return convertView
    }
}