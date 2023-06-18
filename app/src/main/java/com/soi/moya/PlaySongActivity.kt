package com.soi.moya

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import com.soi.moya.databinding.ActivityPlaySongBinding
import java.util.concurrent.TimeUnit

class PlaySongActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaySongBinding

    private var mediaPlayer: MediaPlayer? = null
    private var subColor: Int = 0
    private var pointColor: Int = 0
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
            startSeekBarUpdate()
        }

        mediaPlayer?.setOnErrorListener { mp, what, extra ->
            // 오류 발생 시 처리하는 부분
            Toast.makeText(this, "노래 재생 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            stopSeekBarUpdate()
            mediaPlayer?.release()
            mediaPlayer = null
            true
        }

        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.seekTo(0)
            mediaPlayer?.start()
        }

        binding.playSongButton.setOnClickListener {
            onTappedPlayButton()
        }

        binding.seekBar.setOnClickListener {
            if (mediaPlayer !== null) mediaPlayer?.pause()
        }

        var color = ContextCompat.getColor(this, pointColor)
        binding.seekBar.progressTintList = ColorStateList.valueOf(color)
        binding.seekBar.thumbTintList = ColorStateList.valueOf(color)
        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && mediaPlayer != null && mediaPlayer?.isPlaying == true) mediaPlayer?.seekTo(progress)
                binding.songCurrentTime.text = formatDuration(mediaPlayer!!.currentPosition)
            }

            override fun onStartTrackingTouch(seekBarseekBarseekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        // scoll 위치에 따른 gradient
        binding.scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            val scrollView = binding.scrollView
            val scrollViewHeight = scrollView.height
            val contentHeight = binding.songLyricLayout.height

            if (binding.songLyric.height <= scrollViewHeight) {
                // 스크롤뷰의 높이보다 TextView가 작을 때
                binding.scrollTopOfGradientView.visibility = View.GONE
                binding.scrollBottomOfGradientView.visibility = View.GONE
            } else {
                // 스크롤뷰의 높이보다 TextView가 클 때
                scrollView.viewTreeObserver.addOnScrollChangedListener {
                    val scrollY = scrollView.scrollY
                    if (scrollY == 0) {
                        binding.scrollTopOfGradientView.visibility = View.GONE
                        binding.scrollBottomOfGradientView.visibility = View.VISIBLE
                    } else if (scrollY + scrollViewHeight == contentHeight) {
                        binding.scrollTopOfGradientView.visibility = View.VISIBLE
                        binding.scrollBottomOfGradientView.visibility = View.GONE
                    } else {
                        binding.scrollTopOfGradientView.visibility = View.VISIBLE
                        binding.scrollBottomOfGradientView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun onTappedPlayButton() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            binding.playSongButton.setImageResource(R.drawable.baseline_play_circle_24)
        } else {
            mediaPlayer?.start()
            binding.playSongButton.setImageResource(R.drawable.baseline_pause_circle_24)
        }
    }

    override fun onResume() {
        super.onResume()
        startSeekBarUpdate()
    }

    override fun onPause() {
        super.onPause()
        stopSeekBarUpdate()
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
        pointColor = resources.getIdentifier("${selectedTeam}_point", "color", "com.soi.moya")
        // gradient view
        val gradientSubColor = ContextCompat.getColor(this, subColor)
        val alphaValue = 1
        val alphaColor = ColorUtils.setAlphaComponent(gradientSubColor, alphaValue)
        val topOfGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(alphaColor, gradientSubColor)
        )
        val bottomOfGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(alphaColor, gradientSubColor)
        )

        binding.playSongLayout.setBackgroundColor(ContextCompat.getColor(this, subColor))
        binding.songTitle.text = intent.getStringExtra("title")
        binding.songLyric.text = intent.getStringExtra("lyrics")?.replace("\\n", "\n")
        binding.scrollTopOfGradientView.background = topOfGradientDrawable
        binding.scrollBottomOfGradientView.background = bottomOfGradientDrawable

        window.statusBarColor = ContextCompat.getColor(this, subColor)
    }

    private fun startSeekBarUpdate() {
        if (mediaPlayer?.isPlaying == true) {
            binding.seekBar.post(updateSeekBar)
        }
    }

    private fun stopSeekBarUpdate() {
        binding.seekBar.removeCallbacks(updateSeekBar)
    }

    private val updateSeekBar: Runnable = object: Runnable {
        override fun run() {
            try {
                binding.seekBar.max = mediaPlayer?.duration ?: 0
                binding.songEndTime.text = formatDuration((mediaPlayer?.duration ?: 0))
                binding.seekBar.progress = mediaPlayer?.currentPosition ?: 0
                binding.songCurrentTime.text = formatDuration(mediaPlayer?.currentPosition ?: 0)
                binding.seekBar.postDelayed(this, 1000)
            } catch(e: Exception) {
                binding.seekBar.progress = 0
            }
        }
    }
    private fun formatDuration(duration: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

