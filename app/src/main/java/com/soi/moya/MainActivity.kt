package com.soi.moya

import android.content.Context
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soi.moya.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MusicViewModel

    private var pointColor: Int = 0
    private var selectedTeam: String = ""
    private var firebaseTeamName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MusicViewModel::class.java)
        fetchSelectedTeamName()
        replaceFragment(HomeFragment())

        pointColor = resources.getIdentifier("${selectedTeam}_point", "color", "com.soi.moya")
        setNavigationUI()
        fetchFirebaseData()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun String.capitalizeFirst(): String {
        if (isEmpty()) return this
        return this[0].uppercase() + substring(1)
    }

    // 사용자가 선택한 팀의 정보를 가져옵니다.
    private fun fetchSelectedTeamName() {

        val sharedPreferences = getSharedPreferences("selected_team", Context.MODE_PRIVATE)

        selectedTeam = sharedPreferences.getString("selected_team", "") ?: "doosan"
        firebaseTeamName = selectedTeam.capitalizeFirst()

        if (selectedTeam == "lg") { firebaseTeamName = "LG" }
        else if (selectedTeam == "nc") { firebaseTeamName = "NC" }
        else if (selectedTeam == "ssg") { firebaseTeamName = "SSG"}
    }

    // Bottom Navigation UI Setting
    private fun setNavigationUI() {

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            MenuItemCompat.setIconTintList(item, ColorStateList.valueOf(ContextCompat.getColor(this, pointColor)))
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.search -> replaceFragment(SearchFragment())
                R.id.stadium -> replaceFragment(StadiumFragment())
                else -> {}
            }
            true
        }

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(this, pointColor),
                ContextCompat.getColor(this, R.color.darkGray)
            )
        )
        binding.bottomNavigationView.itemIconTintList = colorStateList
        binding.bottomNavigationView.itemTextColor = colorStateList
    }

    // fetch Data
    private fun fetchFirebaseData() {
        // firestore
        val db = Firebase.firestore
        val musicList = mutableListOf<MusicModel>()
        db.collection(firebaseTeamName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val id = document["id"].toString()
                    val info = document["info"].toString()
                    val lyrics = document["lyrics"].toString()
                    val title = document["title"].toString()
                    val type = document["type"] as Boolean
                    val url = document["url"].toString()
                    musicList.add(MusicModel(id, info, lyrics, title, type, url))
                }
                viewModel.setData(musicList)
            }
            .addOnFailureListener { exception ->
                Log.w("firestore", "Error getting documents.")
            }
    }
}