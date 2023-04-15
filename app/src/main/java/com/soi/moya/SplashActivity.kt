package com.soi.moya

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPrefs = getSharedPreferences("selected_team", Context.MODE_PRIVATE)
        val teamInfo = sharedPrefs.getString("selected_team", "")

        if (teamInfo == "") {
            val intent = Intent(this, SelectTeamActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Log.d("prefpref", teamInfo.toString())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}