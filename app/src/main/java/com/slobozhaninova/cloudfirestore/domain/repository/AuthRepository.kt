package com.slobozhaninova.cloudfirestore.domain.repository

import com.slobozhaninova.cloudfirestore.domain.model.Email
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit>
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
     fun signOut()
    fun getCurrentUser(): Flow<Email?>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun checkEmailVerification(): Boolean
}