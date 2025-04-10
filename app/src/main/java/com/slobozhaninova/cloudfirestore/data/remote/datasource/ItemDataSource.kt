package com.slobozhaninova.cloudfirestore.data.remote.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.slobozhaninova.cloudfirestore.domain.model.ItemData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ItemDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun getItemsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("items")

    suspend fun addItem(bookId: String, authorId: String, rating: Int): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            val docRef = getItemsCollection(userId).document()
            val item = ItemData(
                id = docRef.id,
                bookId = bookId,
                authorId = authorId,
                rating = rating,
                userId = userId
            )
            docRef.set(item).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserItems(userId: String): Flow<List<ItemData>> = callbackFlow {
        val listener = getItemsCollection(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { it.toObject(ItemData::class.java) } ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    suspend fun deleteItem(userId: String, itemId: String): Result<Unit> {
        return try {
            getItemsCollection(userId).document(itemId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}