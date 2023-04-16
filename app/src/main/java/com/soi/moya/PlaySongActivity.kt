package com.soi.moya

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Audio.Media
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.soi.moya.databinding.ActivityPlaySongBinding

class PlaySongActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaySongBinding

    private var mediaPlayer: MediaPlayer? = null

    private var subColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_song)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_song)

        val sharedPrefs = getSharedPreferences("selected_team", Context.MODE_PRIVATE)
        val selectedTeam = sharedPrefs.getString("selected_team", "") ?: "doosan"
        subColor = resources.getIdentifier("${selectedTeam}_sub", "color", "com.soi.moya")
        binding.playSongLayout.setBackgroundColor(ContextCompat.getColor(this, subColor))
        window.statusBarColor = ContextCompat.getColor(this, subColor)

        val url = intent.getStringExtra("url")!!
        binding.songTitle.text = intent.getStringExtra("title")
        binding.songLyric.text = intent.getStringExtra("lyrics")?.replace("\\n", "\n")

        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(url)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            mediaPlayer?.start()
        }

        binding.playSongButton.setOnClickListener {
            onTappedPlayButton()
        }
    }

    private fun onTappedPlayButton() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            binding.playSongButton.setImageResource(R.drawable.baseline_play_circle_24)
        } else {
            mediaPlayer?.start()
            binding.playSongButton.setImageResource(R.drawable.baseline_stop_circle_24)
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

