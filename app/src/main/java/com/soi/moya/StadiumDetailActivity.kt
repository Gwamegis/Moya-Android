package com.soi.moya

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class StadiumDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stadium_detail)

        val stadiumName = intent.getStringExtra("stadiumName").toString()
        val stadiumImageName = intent.getStringExtra("imageName").toString()

        val stadiumTitle = findViewById<TextView>(R.id.stadiumTitleTextView)
        val stadiumImage = findViewById<ImageView>(R.id.stadiumImageView)

        stadiumTitle.setText(stadiumName)
        val id = resources.getIdentifier(stadiumImageName, "drawable", "com.soi.moya")
        stadiumImage.setImageResource(id)

    }

}