package com.soi.moya

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soi.moya.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MusicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MusicViewModel::class.java)

        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId) {

                R.id.home -> replaceFragment(HomeFragment())
                R.id.search -> replaceFragment(SearchFragment())
                R.id.stadium -> replaceFragment(StadiumFragment())

                else -> {

                }
            }
            true
        }

        // firestore
        val db = Firebase.firestore
        val musicList = mutableListOf<MusicModel>()
        db.collection("Doosan")
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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}