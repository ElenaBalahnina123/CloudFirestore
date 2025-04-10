package com.slobozhaninova.cloudfirestore.domain.repository

import com.slobozhaninova.cloudfirestore.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getOrCreateBook(title: String, authorName: String): Result<Book>
    suspend fun getBookById(id: String): Result<Book>
    fun getAllBooks(): Flow<List<Book>>
}


