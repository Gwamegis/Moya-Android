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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

class MainActivity : AppCompatActivity(), HalfModalBottomSheetFragment.OnUpdateSheetRemovedListener, NoticeBottomSheetFragment.OnNoticeSheetRemovedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MusicViewModel

    private var pointColor = 0
    private var selectedTeam = ""
    private var firebaseTeamName = ""
    private var backButtonPressedTime = 0L
    private val PREFS_NAME = "UserPrefs"
    private val KEY_APP_VERSION = "appVersion"

    private var isShowTrafficBottomSheet = false
    private var trafficTitle: String = ""
    private var trafficDescription: String = ""

    private var isShowUpdateBottomSheet = false
    private var newVersion: String = ""
    private var newFeatures = ArrayList<String>()
    private var isRequireUpdate = false

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

    override fun onNoticeSheetRemovedListener() {
        isShowTrafficBottomSheet = false
        if (isShowUpdateBottomSheet) {
            showVersionBottomSheet()
        }
    }

    override fun onUpdateSheetRemovedListener() {
        isShowUpdateBottomSheet = false
        if (isShowTrafficBottomSheet) {
            showTrafficBottomSheet()
        }
        Log.w("isrequiredupdate", isShowTrafficBottomSheet.toString())
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

    private fun fetchFirebaseData() {
        fetchTrafficData {
            fetchVersionData {
                if (isShowUpdateBottomSheet && isRequireUpdate) {
                    showVersionBottomSheet()
                } else if (isShowTrafficBottomSheet) {
                    showTrafficBottomSheet()
                } else if (isShowUpdateBottomSheet) {
                    showVersionBottomSheet()
                } else { }
            }
        }
        fetchMusicData()
    }

    // fetch Data
    private fun fetchTrafficData(callbacks: () -> Unit) {
        val db = Firebase.firestore
        db.collection("Traffic")
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    trafficTitle = documentSnapshot.getString("title") ?: ""
                    trafficDescription = documentSnapshot.getString("description") ?: ""
                    val date = documentSnapshot.getDate("date")
                    Log.w("date", date.toString())

                    if (date != null) {
                        processTrafficData(date)
                    }
                    callbacks()
                } else {
                    Log.w("firebase", "Error null documents.")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("firebase", "Error getting documents.")
            }
    }

    private fun fetchVersionData(callbacks: () -> Unit) {
        val db = Firebase.firestore
        db.collection("AndroidVersion")
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    newVersion = documentSnapshot.getString("version") ?: ""
                    newFeatures = documentSnapshot.get("feature") as? ArrayList<String> ?: ArrayList()
                    isRequireUpdate = documentSnapshot.get("isRequired") as? Boolean ?: false
                    processVersionData()
                } else {
                    Log.w("firebase", "Error null documents.")
                }
                callbacks()
            }
            .addOnFailureListener { exception ->
                Log.w("firebase", "Error getting documents.")
            }
    }

    private fun fetchMusicData() {
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
    private fun processVersionData() {
        if (!checkRecentVersion(newVersion)) {
            val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedVersion = sharedPreferences.getString(KEY_APP_VERSION, "")

            if (savedVersion != newVersion) {
                isShowUpdateBottomSheet = true
            }
        }
    }

    private fun processTrafficData(date: Date) {

        val today = Calendar.getInstance()
        val startOfDay = today.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        val endOfDay = today.apply {
            add(Calendar.DATE, 1)
        }.time

        isShowTrafficBottomSheet = date >= startOfDay && date < endOfDay
        Log.d("isShowTrafficBottomSheet", isShowTrafficBottomSheet.toString())
    }

    private fun showVersionBottomSheet() {
        val bottomSheetFragment = HalfModalBottomSheetFragment()
        val bundle = Bundle()
        bundle.putStringArrayList("features", newFeatures)
        bundle.putString("newVersion", newVersion)
        bundle.putBoolean("isRequired", isRequireUpdate)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(supportFragmentManager, "HalfModalBottomSheet")
    }

    private fun showTrafficBottomSheet() {
        val noticeBottomSheet = NoticeBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("title", trafficTitle)
        bundle.putString("description", trafficDescription)
        noticeBottomSheet.arguments = bundle
        noticeBottomSheet.show(supportFragmentManager, "NoticeBottomSheet")
    }

    private fun checkRecentVersion(version: String): Boolean {
        val currentVersion = BuildConfig.VERSION_NAME
        val versionParts = version.split(".")
        val currentVersionParts = currentVersion.split(".")

        for (i in 0 until max(versionParts.size, currentVersionParts.size)) {
            val newVersionName = if (i < versionParts.size) versionParts[i] else ""
            val currentVersionName = if (i < currentVersionParts.size) currentVersionParts[i] else ""

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