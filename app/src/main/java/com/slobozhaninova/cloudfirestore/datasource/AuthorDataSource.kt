package com.slobozhaninova.cloudfirestore.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.slobozhaninova.cloudfirestore.domain.model.Author
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthorDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val authorsCollection = firestore.collection("authors")

    suspend fun getOrCreateAuthor(name: String): Result<Author> {
        return try {
            // Check if author exists
            val query = authorsCollection
                .whereEqualTo("name", name)
                .limit(1)
                .get()
                .await()

            val author = if (query.documents.isNotEmpty()) {
                query.documents[0].toObject(Author::class.java)!!
            } else {
                // Create new author
                val docRef = authorsCollection.document()
                val newAuthor = Author(
                    id = docRef.id,
                    name = name
                )
                docRef.set(newAuthor).await()
                newAuthor
            }
            Result.success(author)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAuthorById(id: String): Result<Author> {
        return try {
            val snapshot = authorsCollection.document(id).get().await()
            val author = snapshot.toObject(Author::class.java)
                ?: return Result.failure(Exception("Author not found"))
            Result.success(author)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}