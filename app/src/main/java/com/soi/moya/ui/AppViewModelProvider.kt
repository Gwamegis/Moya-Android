package com.soi.moya.ui

import android.app.Application
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.soi.moya.ui.music_list.MusicListViewModel
import com.soi.moya.ui.music_storage.MusicStorageViewModel
import com.soi.moya.ui.select_team.SelectTeamViewModel

object AppViewModelProvider {
    private val application = Application()
    val Factory = viewModelFactory {
        initializer {
            MusicListViewModel(application = application)
        }
        initializer {
            MusicStorageViewModel(moyaApplication().container.itemsRepository)
        }

        initializer {
            SelectTeamViewModel(application = application)
        }
    }
}

fun CreationExtras.moyaApplication(): MoyaApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoyaApplication)