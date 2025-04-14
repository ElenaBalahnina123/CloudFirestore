package com.slobozhaninova.cloudfirestore.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.slobozhaninova.cloudfirestore.datasource.AuthorDataSource
import com.slobozhaninova.cloudfirestore.datasource.BookDataSource
import com.slobozhaninova.cloudfirestore.datasource.FirebaseAuthDataSource
import com.slobozhaninova.cloudfirestore.datasource.ItemDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
    fun provideAuth() : FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideAuthRepository(authDataSource: FirebaseAuthDataSource): AuthRepository {
        return AuthRepositoryImpl(authDataSource)
    }

    @Provides
    @Singleton
    fun provideAuthorRepository(authorDataSource: AuthorDataSource): AuthorRepository {
        return AuthorRepositoryImpl(authorDataSource)
    }

    @Provides
    @Singleton
    fun provideBookRepository(bookDataSource: BookDataSource): BookRepository {
        return BookRepositoryImpl(bookDataSource)
    }

    @Provides
    @Singleton
    fun provideItemRepository(itemDataSource: ItemDataSource, bookRepository: BookRepository, authorRepository: AuthorRepository, authRepository: AuthRepository): ItemRepository {
        return ItemRepositoryImpl(itemDataSource,bookRepository,authorRepository,authRepository)
    }

}