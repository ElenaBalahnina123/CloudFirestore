package com.slobozhaninova.cloudfirestore.domain.repository

import com.slobozhaninova.cloudfirestore.datasource.BookDataSource
import com.slobozhaninova.cloudfirestore.domain.model.Book
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookDataSource: BookDataSource
) : BookRepository {
    override suspend fun getOrCreateBook(title: String, authorName: String): Result<Book> {
        // In a real app, you might want to get/create author first
        // This is simplified - you'd need to coordinate with AuthorRepository
        return bookDataSource.getOrCreateBook(title, authorName)
    }

    override suspend fun getBookById(id: String): Result<Book> =
        bookDataSource.getBookById(id)

    override fun getAllBooks(): Flow<List<Book>> =
        bookDataSource.getAllBooks()
}