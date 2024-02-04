package com.soi.moya.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.soi.moya.models.Music
import com.soi.moya.util.UiState


class MusicRepository(
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    fun getMusics(teamName: String, result: (UiState<List<Music>>) -> Unit) {
        database.collection(teamName)
            .get()
            .addOnSuccessListener {
                val musics = arrayListOf<Music>()
                for (document in it) {
                    val music = document.toObject(Music::class.java)
                    musics.add(music)
                }
                result.invoke(
                    UiState.Success(musics)
                )
            }
            .addOnFailureListener { it ->
                it.localizedMessage?.let { errorMessage ->
                    result.invoke(
                        UiState.Failure(
                            errorMessage
                        )
                    )
                }
            }
    }

    fun getAppversionNotice() {
        database.collection("AndroidVersion")
            .get()
            .addOnSuccessListener {
            }
    }
}