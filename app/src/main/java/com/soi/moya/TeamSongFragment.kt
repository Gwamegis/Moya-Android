package com.soi.moya

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels


class TeamSongFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()
    private var musicData: List<MusicModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_team_song, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        musicData = viewModel.teamMusicList
        val adapter = SongListViewAdapter(musicData)
        val listView = view.findViewById<ListView>(R.id.songListView)
        val footerView = LayoutInflater.from(context).inflate(R.layout.request_song_view, null, false)
        val pointColor = viewModel.pointColor.value

        val songRequestText = footerView.findViewById<TextView>(R.id.songRequestText)
        val sendIcon = footerView.findViewById<ImageView>(R.id.songRequestIcon)
        val songRequestButton = footerView.findViewById<LinearLayout>(R.id.songRequestButton)
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.point_floating_button)
        if (drawable is GradientDrawable) {
            drawable.setStroke(dpToPx(1.6), ContextCompat.getColor(requireContext(), pointColor!!))
            songRequestButton.background = drawable  // 보더 색상 설정
        }

        songRequestButton.background = drawable

        songRequestText.setTextColor(ContextCompat.getColor(requireContext(), pointColor!!))
        sendIcon.setColorFilter(ContextCompat.getColor(requireContext(), pointColor!!))

        listView.addFooterView(footerView)

        listView.adapter = adapter

        listView.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(requireContext(), PlaySongActivity::class.java)
            intent.putExtra("title", musicData[i].title)
            intent.putExtra("lyrics", musicData[i].lyrics)
            intent.putExtra("url", musicData[i].url)
            startActivity(intent)
        }
        songRequestButton.setOnClickListener {
            val webView = WebView(requireContext())
            webView.loadUrl("https://forms.gle/522hhU1Riq5wQhbv7")
        }
    }
    private fun dpToPx(dp: Double): Int {
        val density = resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
}