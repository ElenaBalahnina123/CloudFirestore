package com.slobozhaninova.cloudfirestore.domain.repository

import com.slobozhaninova.cloudfirestore.datasource.ItemDataSource
import com.slobozhaninova.cloudfirestore.domain.model.ItemData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemDataSource: ItemDataSource,
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val authRepository: AuthRepository
) : ItemRepository {
    override suspend fun addItem(bookTitle: String, authorName: String, rating: Int): Result<Unit> {
        return try {
            // Get or create author
            val authorResult = authorRepository.getOrCreateAuthor(authorName)
            if (authorResult.isFailure) return Result.failure(authorResult.exceptionOrNull()!!)
            val author = authorResult.getOrNull()!!

            // Get or create book
            val bookResult = bookRepository.getOrCreateBook(bookTitle, author.id)
            if (bookResult.isFailure) return Result.failure(bookResult.exceptionOrNull()!!)
            val book = bookResult.getOrNull()!!

            // Add item
            itemDataSource.addItem(book.id, author.id, rating)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserItems(): Flow<List<ItemData>> {
        val userId = authRepository.getCurrentUser()
            .filterNotNull()
            .firstOrNull()
            ?.id ?: return flowOf(emptyList())
        return itemDataSource.getUserItems(userId)
    }

    override suspend fun deleteItem(itemId: String): Result<Unit> {
        val userId = authRepository.getCurrentUser()
            .filterNotNull()
            .firstOrNull()
            ?.id ?: return Result.failure(Exception("User not authenticated"))
        return itemDataSource.deleteItem(userId, itemId)
    }
}