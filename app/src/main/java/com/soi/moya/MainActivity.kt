package com.soi.moya

import android.content.Context
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soi.moya.databinding.ActivityMainBinding
import com.soi.moya.BuildConfig
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MusicViewModel

    private var pointColor = 0
    private var selectedTeam = ""
    private var firebaseTeamName = ""
    private var backButtonPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MusicViewModel::class.java)
        fetchSelectedTeamName()
        replaceFragment(HomeFragment())

        pointColor = resources.getIdentifier("${selectedTeam}_point", "color", "com.soi.moya")
        setNavigationUI()
        fetchVersionData()
//        fetchFirebaseData()
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
    private fun fetchVersionData() {
        val db = Firebase.firestore
        db.collection("AndroidVersion")
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val version = documentSnapshot.getString("version")
                    val features = documentSnapshot.get("feature") as? List<String>

                    if (version != null && features != null) {
                        processVersionData(version, features)
                    }
                } else {
                    Log.w("firebase", "Error null documents.")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("firebase", "Error getting documents.")
            }
    }

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

    private fun processVersionData(version: String, features: List<String>) {
        if (!isRecentVersion(version)) {
            Log.d("새로운 기능이 나왔음", "hello world!")
            val bottomSheetFragment = HalfModalBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, "HalfModalBottomSheet")

        }
        for(feature in features) {
            Log.d("new feature", "feature: $feature")
        }
        fetchFirebaseData()
    }

    private fun isRecentVersion(version: String): Boolean {
        val currentVersion = BuildConfig.VERSION_NAME
        Log.d("versions", "$version, $currentVersion")
        val versionParts = version.split(".")
        val currentVersionParts = currentVersion.split(".")

        for (i in 0 until max(versionParts.size, currentVersionParts.size)) {
            val newVersionName = if (i < versionParts.size) versionParts[i] else ""
            val currentVersionName =
                if (i < currentVersionParts.size) currentVersionParts[i] else ""

            if (newVersionName != currentVersionName) {
                return false
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backButtonPressedTime + 1500) {
            backButtonPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "종료하시려면, 한 번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }
    }
}