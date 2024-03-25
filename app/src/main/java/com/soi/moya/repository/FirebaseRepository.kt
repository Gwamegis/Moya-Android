package com.soi.moya.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.soi.moya.util.UiState

class FirebaseRepository<T>(
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val clazz: Class<T>
) {
    fun getData(collection: String, result: (UiState<List<T>>) -> Unit) {
        database.collection(collection)
            .orderBy("title")
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(clazz)
                result.invoke(
                    UiState.Success(data)
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

    fun getSingleData(collection: String, result: (UiState<T>) -> Unit) {
        database.collection(collection)
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(clazz).firstOrNull()
                data?.let {
                    result.invoke(
                        UiState.Success(it)
                    )
                } ?: run {
                    result.invoke(
                        UiState.Failure(
                            "No data found"
                        )
                    )
                }
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
}
