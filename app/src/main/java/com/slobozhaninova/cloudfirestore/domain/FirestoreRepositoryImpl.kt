package com.slobozhaninova.cloudfirestore.domain

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.slobozhaninova.cloudfirestore.Author
import com.slobozhaninova.cloudfirestore.Book
import com.slobozhaninova.cloudfirestore.Email
import com.slobozhaninova.cloudfirestore.ItemData
import com.slobozhaninova.cloudfirestore.Result
import com.slobozhaninova.cloudfirestore.getOrThrow
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreRepository {

    override suspend fun createEmail(email: Email): Result<String> {
        return try {
            val docRef = firestore.collection("emails").document()
            val emailWithId = email.copy(id = docRef.id)
            docRef.set(emailWithId).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
    override suspend fun checkEmailExists(email: String): Result<String?> {
        return try {
            val query = firestore.collection("emails")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            if (query.isEmpty) {
                Result.Success(null)
            } else {
                Result.Success(query.documents[0].id)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getEmailRecords(emailId: String): Result<List<ItemData>> {
        return try {
            val snapshot = firestore.collection("emails/$emailId/items")
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ItemData::class.java)?.copy(id = doc.id)
            }
            Result.Success(items)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun createOrGetAuthor(author: Author): Result<String> {
        return try {
            // Проверяем, существует ли автор с таким именем
            val query = firestore.collection("authors")
                .whereEqualTo("name", author.name)
                .limit(1)
                .get()
                .await()

            if (query.documents.isNotEmpty()) {
                // Автор уже существует, возвращаем его ID
                Result.Success(query.documents[0].id)
            } else {
                // Создаем нового автора
                val docRef = firestore.collection("authors").document()
                val authorWithId = author.copy(id = docRef.id)
                docRef.set(authorWithId).await()
                Result.Success(docRef.id)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun createOrGetBook(book: Book): Result<String> {
        return try {
            // Проверяем, существует ли книга с таким названием
            val query = firestore.collection("books")
                .whereEqualTo("title", book.title)
                .limit(1)
                .get()
                .await()

            if (query.documents.isNotEmpty()) {
                // Книга уже существует, возвращаем её ID
                Result.Success(query.documents[0].id)
            } else {
                // Создаем новую книгу
                val docRef = firestore.collection("books").document()
                val bookWithId = book.copy(id = docRef.id)
                docRef.set(bookWithId).await()
                Result.Success(docRef.id)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun createItemData(emailId: String, itemData: ItemData): Result<String> {
        return try {
            // Сначала получаем/создаем автора и книгу
            val authorId = createOrGetAuthor(itemData.author).getOrThrow()
            val bookId = createOrGetBook(itemData.book).getOrThrow()

            // Создаем ItemData с ссылками на автора и книгу
            val itemDataWithRefs = itemData.copy(
                id = "", // будет создан новый ID
                author = itemData.author.copy(id = authorId),
                book = itemData.book.copy(id = bookId)
            )

            val docRef = firestore.collection("emails/$emailId/items").document()
            val finalItem = itemDataWithRefs.copy(id = docRef.id)
            docRef.set(finalItem).await()

            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override fun getItemsByEmail(emailId: String): Flow<List<ItemData>> = callbackFlow {
        val subscription = firestore.collection("emails/$emailId/items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ItemData::class.java)?.copy(id = doc.id)
                    }
                    trySend(items)
                }
            }

        awaitClose { subscription.remove() }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance().apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        }
    }

    @Provides
    @Singleton
    fun provideFirestoreRepository(firestore: FirebaseFirestore): FirestoreRepository {
        return FirestoreRepositoryImpl(firestore)
    }
}