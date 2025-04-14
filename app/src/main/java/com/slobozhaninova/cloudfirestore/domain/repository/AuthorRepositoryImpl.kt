package com.slobozhaninova.cloudfirestore.domain.repository

import com.slobozhaninova.cloudfirestore.datasource.AuthorDataSource
import com.slobozhaninova.cloudfirestore.domain.model.Author
import javax.inject.Inject

class AuthorRepositoryImpl @Inject constructor(
    private val authorDataSource: AuthorDataSource
) : AuthorRepository {
    override suspend fun getOrCreateAuthor(name: String): Result<Author> =
        authorDataSource.getOrCreateAuthor(name)

    override suspend fun getAuthorById(id: String): Result<Author> =
        authorDataSource.getAuthorById(id)
}