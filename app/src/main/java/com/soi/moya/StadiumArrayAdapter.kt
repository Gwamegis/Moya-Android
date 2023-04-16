package com.soi.moya

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class StadiumArrayAdapter(context: Context, resource: Int, objects: Array<String>):
    ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.stadium_listview_item, parent, false)
        }

        val textView = view?.findViewById<TextView>(R.id.stadiumListViewItem)
        textView?.text = getItem(position)

        return view!!
    }
}