package com.soi.moya.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.soi.moya.ui.listItem_menu.ListItemMenuViewModel
import com.soi.moya.ui.music_list.MusicListViewModel
import com.soi.moya.ui.music_storage.MusicStorageViewModel
import com.soi.moya.ui.notice.NoticeBottomSheetViewModel
import com.soi.moya.ui.search.SearchViewModel
import com.soi.moya.ui.select_team.SelectTeamViewModel

object AppViewModelProvider {
    private val application = Application()
    val Factory = viewModelFactory {
        initializer {
            MusicListViewModel(application = application)
        }
        initializer {
            MusicStorageViewModel(
                moyaApplication().container.itemsRepository,
                application = application
            )
        }
        initializer {
            SelectTeamViewModel(application = application)
        }
        initializer {
            SearchViewModel(application = application)
        }
        initializer {
            NoticeBottomSheetViewModel(application = application)
        }
        initializer {
            ListItemMenuViewModel(
                moyaApplication().container.itemsRepository,
                application = application
            )
        }
    }
}

fun CreationExtras.moyaApplication(): MoyaApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoyaApplication)