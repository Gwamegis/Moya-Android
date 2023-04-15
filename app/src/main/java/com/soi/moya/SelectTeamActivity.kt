package com.soi.moya

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectTeamActivity : AppCompatActivity() {

    private var selectedPosition = -1
    private var selectedTeamName = ""
    private val teams = arrayOf(
        "doosan", "hanwha", "samsung", "lotte", "lg", "ssg", "kt", "nc", "kiwoom", "kia")

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_team)

        val completeButton = findViewById<Button>(R.id.selectedTeamButton)

        // Shared Preferences
        val sharedPref = getSharedPreferences("selected_team", Context.MODE_PRIVATE)
        val teamInfo = sharedPref.getString("selected_team", "")
        val editor = sharedPref.edit()

        // recyclerview
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = RecyclerViewAdapter(teams)

        // Shared Preference에서 기본 팀 정보 호출
        if (teamInfo != "") {
            val index = teams.indexOf(teamInfo)
            selectedPosition = index
            adapter.setSelected(index)
        }

        recyclerView.adapter = adapter

        adapter.itemClick = object : RecyclerViewAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                if (selectedPosition != position) {
                    adapter.setSelected(position)
                    selectedTeamName = teams[position]
                    selectedPosition = position
                }
            }
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        completeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            editor.putString("selected_team", selectedTeamName)
            editor.apply()
            startActivity(intent)
            finish()
        }
    }
}