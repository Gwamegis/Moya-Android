package com.soi.moya

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.core.content.ContextCompat


class StadiumFragment : Fragment() {

    val listItem = arrayOf("고척 스카이돔", "광주 기아 챔피언스 필드", "대구 삼성 라이온즈 파크",
                                "사직 야구장", "수원 KT 위즈 파크", "울산 문수 야구장", "인천 SSG 랜더스 필드",
                                "잠실 야구장", "창원 NC 파크", "한화생명 이글스 파크")

    val imageNameList = arrayOf("gocheok_sky_dome", "gwangju_kia_champions_field", "daegu_samsung_lions_park",
        "sajik_baseball_stadium", "suwon_kt_wiz_park", "ulsan_munsu_baseball_stadium", "incheon_ssg_landers_field",
        "seoul_sports_complex_baseball_stadium", "changwon_nc_park", "daejeon_hanwha_life_eagles_park")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stadium, container, false)
        val listView = view.findViewById<ListView>(R.id.stadiumListView)
//        val adapter = SongListViewAdapter(listItem)
//        val adapter = ArrayAdapter(requireContext(), R.layout.stadium_listview_item, listItem)
        val adapter = StadiumArrayAdapter(requireContext(), R.layout.stadium_listview_item, listItem)
        listView.adapter = adapter


        // activity 이동
        listView.setOnItemClickListener { adapterView, view, i, l ->
//            val clickItem = listItem[i]
            val intent = Intent(requireContext(), StadiumDetailActivity::class.java)
            intent.putExtra("stadiumName", listItem[i])
            intent.putExtra("imageName", imageNameList[i])
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
    }

}