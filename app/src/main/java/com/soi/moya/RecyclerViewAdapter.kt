package com.soi.moya

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(val List: Array<String>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {

        if (itemClick != null) {
            holder.itemView.setOnClickListener { v ->
                itemClick!!.onClick(v, position)
            }
        }

        holder.bindItems(List[position])

        if (selectedPosition == position) {
            holder.setSelected()
        } else {
            holder.setUnselected()
        }

    }

    override fun getItemCount(): Int {
        return List.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.selectTeamImage)
        private val selectedImage = itemView.findViewById<ImageView>(R.id.chooseTeam)

        @SuppressLint("DiscouragedApi")
        fun bindItems(item: String) {
            val id = itemView.context.resources.getIdentifier("select_team_${item}", "drawable", "com.soi.moya")
            imageView.setImageResource(id)
        }

        fun setSelected() {
            selectedImage.visibility = View.VISIBLE
        }

        fun setUnselected() {
            selectedImage.visibility = View.GONE
        }

    }

    fun setSelected(position: Int) {
        if (selectedPosition != position) {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    fun setSelectedTeam(teamName: String) {
        val index = List.indexOf(teamName)
        selectedPosition = index
        notifyItemChanged(selectedPosition)
    }
}