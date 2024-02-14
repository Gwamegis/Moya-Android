package com.soi.moya.ui

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.soi.moya.R
import com.soi.moya.base.BaseComposeActivity
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.ui.bottom_nav.BottomNavScreen
import com.soi.moya.ui.theme.MoyaTheme

class MainActivity : BaseComposeActivity() {
    private var backPressedTime: Long = 0
    private val toast: Toast by lazy {
        Toast.makeText(baseContext, getString(R.string.back_btn_pressed), Toast.LENGTH_SHORT)
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val userPreferences = remember { UserPreferences(context) }
        val selectedTeam = userPreferences.getSelectedTeam.collectAsState(initial = "doosan").value

        MoyaTheme(team = Team.valueOf(selectedTeam ?: "doosan")) {
            BottomNavScreen()
        }

        val callback = createOnBackPressedCallback()
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun createOnBackPressedCallback(): OnBackPressedCallback {
        return object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    toast.cancel()
                    finish()
                } else {
                    toast.show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        }
    }
}

