package com.slobozhaninova.cloudfirestore.domain

import com.slobozhaninova.cloudfirestore.Author
import com.slobozhaninova.cloudfirestore.Book
import com.slobozhaninova.cloudfirestore.Email
import com.slobozhaninova.cloudfirestore.ItemData
import com.slobozhaninova.cloudfirestore.Result
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    suspend fun checkEmailExists(email: String): Result<String?>
    suspend fun getEmailRecords(emailId: String): Result<List<ItemData>>
    suspend fun createEmail(email: Email): Result<String>
    suspend fun createOrGetAuthor(author: Author): Result<String>
    suspend fun createOrGetBook(book: Book): Result<String>
    suspend fun createItemData(emailId: String, itemData: ItemData): Result<String>
    fun getItemsByEmail(emailId: String): Flow<List<ItemData>>
}