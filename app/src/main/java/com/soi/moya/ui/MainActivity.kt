package com.soi.moya.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.soi.moya.base.BaseComposeActivity
import com.soi.moya.data.MusicManager
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.repository.MusicRepositoryImp
import com.soi.moya.ui.bottom_nav.BottomNavScreen
import com.soi.moya.ui.theme.MoyaTheme

class MainActivity : BaseComposeActivity() {
    private lateinit var database: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    @Composable
    override fun Content() {
        database = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        val context = LocalContext.current
        val userPreferences = UserPreferences(context)
        val selectedTeam = userPreferences.getSelectedTeam.collectAsState(initial = "doosan").value
        MusicManager.getInstance()
        MoyaTheme(team = Team.valueOf(selectedTeam ?: "doosan")) {
            BottomNavScreen()
        }
    }
}