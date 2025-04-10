package com.slobozhaninova.cloudfirestore.data.remote.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.slobozhaninova.cloudfirestore.domain.model.Email
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

     fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): Flow<Email?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toEmail())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    private fun FirebaseUser.toEmail(): Email = Email(
        id = uid,
        email = email ?: "",
        isEmailVerified = isEmailVerified
    )

     suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

     suspend fun checkEmailVerification(): Boolean {
        return try {
            firebaseAuth.currentUser?.reload()?.await()
            firebaseAuth.currentUser?.isEmailVerified ?: false
        } catch (e: Exception) {
            false
        }
    }
}


