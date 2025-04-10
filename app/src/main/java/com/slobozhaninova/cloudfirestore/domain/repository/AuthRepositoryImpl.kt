package com.slobozhaninova.cloudfirestore.domain.repository

import com.slobozhaninova.cloudfirestore.data.remote.datasource.FirebaseAuthDataSource
import com.slobozhaninova.cloudfirestore.domain.model.Email
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource
) : AuthRepository {
    override suspend fun signUpWithEmail(email: String, password: String): Result<Unit> =
        authDataSource.signUpWithEmail(email, password)

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        authDataSource.signInWithEmail(email, password)

    override fun signOut() = authDataSource.signOut()

    override fun getCurrentUser(): Flow<Email?> = authDataSource.getCurrentUser()

    override suspend fun sendEmailVerification(): Result<Unit> = authDataSource.sendEmailVerification()

    override suspend fun checkEmailVerification(): Boolean = authDataSource.checkEmailVerification()

}

