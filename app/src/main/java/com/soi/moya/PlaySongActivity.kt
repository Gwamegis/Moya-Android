package com.soi.moya

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.soi.moya.databinding.ActivityPlaySongBinding

class PlaySongActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaySongBinding

    private var mediaPlayer: MediaPlayer? = null
    private var subColor: Int = 0
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        url = intent.getStringExtra("url")!!

        setContentView(R.layout.activity_play_song)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_song)

        settingUI()

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
            mediaPlayer?.seekTo(0)
            binding.playSongButton.setImageResource(R.drawable.baseline_play_circle_24)
        } else {
            mediaPlayer?.start()
            binding.playSongButton.setImageResource(R.drawable.baseline_stop_circle_24)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    @SuppressLint("DiscouragedApi")
    private fun settingUI() {

        val sharedPrefs = getSharedPreferences("selected_team", Context.MODE_PRIVATE)
        val selectedTeam = sharedPrefs.getString("selected_team", "") ?: "doosan"

        subColor = resources.getIdentifier("${selectedTeam}_sub", "color", "com.soi.moya")

        binding.playSongLayout.setBackgroundColor(ContextCompat.getColor(this, subColor))
        binding.songTitle.text = intent.getStringExtra("title")
        binding.songLyric.text = intent.getStringExtra("lyrics")?.replace("\\n", "\n")

        window.statusBarColor = ContextCompat.getColor(this, subColor)
    }
}

