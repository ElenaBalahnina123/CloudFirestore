package com.slobozhaninova.cloudfirestore.domain.repository

import com.slobozhaninova.cloudfirestore.domain.model.Author

interface AuthorRepository {
    suspend fun getOrCreateAuthor(name: String): Result<Author>
    suspend fun getAuthorById(id: String): Result<Author>
}