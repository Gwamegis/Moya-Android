package com.soi.moya

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectTeamActivity : AppCompatActivity() {

    private val teams = arrayOf(
        "select_team_doosan", "select_team_hanwha", "select_team_samsung",
        "select_team_lotte", "select_team_lg", "select_team_ssg",
        "select_team_kt", "select_team_nc", "select_team_kiwoom", "select_team_kia"
    )
    private var selectedPosition = -1

    private var selectedTeamName = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_team)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = RecyclerViewAdapter(teams)
        recyclerView.adapter = adapter

        adapter.itemClick = object : RecyclerViewAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                if (selectedPosition != position) {
                    adapter.setSelected(position)
                    selectedTeamName = teams[position]
                    selectedPosition = position
                }
//                selectedTeamName = teams[position]
            }
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)


        val id =
            resources.getIdentifier("select_team_${selectedTeamName}", "drawable", "com.soi.moya")

        val completeButton = findViewById<Button>(R.id.selectedTeamButton)
        completeButton.setOnClickListener {
            finish()
        }
    }
}