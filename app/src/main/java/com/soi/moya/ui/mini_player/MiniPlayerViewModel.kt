package com.soi.moya.ui.mini_player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.soi.moya.models.UserPreferences

class MiniPlayerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _userPreference = UserPreferences(application)

}