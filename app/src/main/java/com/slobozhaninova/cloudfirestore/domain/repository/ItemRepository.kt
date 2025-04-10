package com.slobozhaninova.cloudfirestore.domain.repository

import com.slobozhaninova.cloudfirestore.domain.model.ItemData
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    suspend fun addItem(bookTitle: String, authorName: String, rating: Int): Result<Unit>
    suspend fun getUserItems(): Flow<List<ItemData>>
    suspend fun deleteItem(itemId: String): Result<Unit>
}