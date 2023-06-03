package com.soi.moya

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import com.soi.moya.databinding.ActivityPlaySongBinding
import java.util.concurrent.TimeUnit

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
            initSeekBar()
        }

        binding.playSongButton.setOnClickListener {
            onTappedPlayButton()
        }

        binding.seekBar.setOnClickListener {
            if (mediaPlayer !== null) mediaPlayer?.pause()
        }

        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
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

    private fun initSeekBar() {
        binding.seekBar.max = mediaPlayer!!.duration
        binding.songEndTime.text = formatDuration(mediaPlayer!!.duration)

        val handler = Handler()
        handler.postDelayed(object: Runnable {
            override fun run() {
                try {
                    binding.seekBar.progress = mediaPlayer!!.currentPosition
                    handler.postDelayed(this, 1000)
                } catch(e: Exception) {
                    binding.seekBar.progress = 0
                }
            }
        }, 0)
    }

    private fun formatDuration(duration: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

