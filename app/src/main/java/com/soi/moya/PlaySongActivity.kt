package com.soi.moya

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.soi.moya.databinding.ActivityPlaySongBinding

class PlaySongActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaySongBinding

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_song)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_song)

        binding.songTitle.text = intent.getStringExtra("title")
        binding.songLyric.text = intent.getStringExtra("lyrics")?.replace("\\n", "\n")

        mediaPlayer = MediaPlayer.create(this, R.raw.test)
        mediaPlayer?.start()

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

