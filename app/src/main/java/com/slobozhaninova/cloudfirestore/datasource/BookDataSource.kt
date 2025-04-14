package com.slobozhaninova.cloudfirestore.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.slobozhaninova.cloudfirestore.domain.model.Book
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BookDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val booksCollection = firestore.collection("books")

    suspend fun getOrCreateBook(title: String, authorId: String): Result<Book> {
        return try {
            // Check if book with same title and author exists
            val query = booksCollection
                .whereEqualTo("title", title)
                .whereEqualTo("authorId", authorId)
                .limit(1)
                .get()
                .await()

            val book = if (query.documents.isNotEmpty()) {
                query.documents[0].toObject(Book::class.java)!!
            } else {
                // Create new book
                val docRef = booksCollection.document()
                val newBook = Book(
                    id = docRef.id,
                    title = title,
                    authorId = authorId
                )
                docRef.set(newBook).await()
                newBook
            }
            Result.success(book)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookById(id: String): Result<Book> {
        return try {
            val snapshot = booksCollection.document(id).get().await()
            val book = snapshot.toObject(Book::class.java)
                ?: return Result.failure(Exception("Book not found"))
            Result.success(book)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllBooks(): Flow<List<Book>> = callbackFlow {
        val listener = booksCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val books = snapshot?.documents?.mapNotNull { it.toObject(Book::class.java) } ?: emptyList()
            trySend(books)
        }
        awaitClose { listener.remove() }
    }
}