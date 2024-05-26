package com.soi.moya.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.soi.moya.ui.listItem_menu.ListItemMenuViewModel
import com.soi.moya.ui.main_activity.MusicViewModel
import com.soi.moya.ui.mini_player.MiniPlayerViewModel
import com.soi.moya.ui.music_list.MusicListViewModel
import com.soi.moya.ui.music_player.MusicPlayerViewModel
import com.soi.moya.ui.music_player.PlaylistViewModel
import com.soi.moya.ui.music_storage.MusicStorageViewModel
import com.soi.moya.ui.notice.NoticeBottomSheetViewModel
import com.soi.moya.ui.search.SearchViewModel
import com.soi.moya.ui.select_team.SelectTeamViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MusicListViewModel(
                application = moyaApplication(),
                storedMusicRepository = moyaApplication().container.itemsRepository
            )
        }
        initializer {
            MusicStorageViewModel(
                moyaApplication().container.itemsRepository,
                application = moyaApplication()
            )
        }
        initializer {
            SelectTeamViewModel(application = moyaApplication())
        }
        initializer {
            SearchViewModel(
                application = moyaApplication(),
                storedMusicRepository = moyaApplication().container.itemsRepository
            )
        }
        initializer {
            NoticeBottomSheetViewModel(application = moyaApplication())
        }
        initializer {
            ListItemMenuViewModel(
                moyaApplication().container.itemsRepository,
                application = moyaApplication()
            )
        }
        initializer {
            MusicViewModel(moyaApplication())
        }
        initializer {
            MiniPlayerViewModel(moyaApplication())
        }
        initializer {
            PlaylistViewModel(
                moyaApplication(),
                moyaApplication().container.itemsRepository
            )
        }
        initializer {
            MusicPlayerViewModel(
                moyaApplication(),
                this.createSavedStateHandle(),
                moyaApplication().container.itemsRepository
            )
        }
    }
}

fun CreationExtras.moyaApplication(): MoyaApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoyaApplication)