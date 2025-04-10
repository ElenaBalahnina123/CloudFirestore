package com.slobozhaninova.cloudfirestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.slobozhaninova.cloudfirestore.domain.model.AuthState
import com.slobozhaninova.cloudfirestore.domain.model.RegistrationState
import com.slobozhaninova.cloudfirestore.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {


    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Новое состояние для регистрации
    internal val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _authState.value = if (user != null) {
                    AuthState.Authenticated(user)
                } else {
                    AuthState.Unauthenticated
                }
            }
        }
    }

    fun checkEmailVerification() = viewModelScope.launch {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.await()

        if (user?.isEmailVerified == true) {
            // Обновляем статус в Firestore
            firestore.collection("users").document(user.uid)
                .update("emailVerified", true)
                .await()
        }
    }

    fun register(email: String, password: String, additionalData: Map<String, Any> = emptyMap()) = viewModelScope.launch {
        _loading.value = true
        _registrationState.value = RegistrationState.Idle

        try {
            // 1. Регистрация в Firebase Auth
            val authResult = authRepository.signUpWithEmail(email, password)
            if (authResult.isFailure) {
                _registrationState.value = RegistrationState.Error(
                    authResult.exceptionOrNull()?.message ?: "Registration failed"
                )
                return@launch
            }

            // 2. Получаем UID созданного пользователя
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                _registrationState.value = RegistrationState.Error("User not created")
                return@launch
            }

            // 3. Создаем запись в Firestore
            val userData = hashMapOf(
                "email" to email,
                "createdAt" to FieldValue.serverTimestamp()
            ).apply {
                putAll(additionalData)
            }

            firestore.collection("users").document(user.uid)
                .set(userData)
                .await()

            // 4. Отправляем письмо подтверждения
            user.sendEmailVerification().await()

            _registrationState.value = RegistrationState.Success
            _authState.value = AuthState.SignUpSuccess
        } catch (e: Exception) {
            _registrationState.value = RegistrationState.Error(e.message ?: "Registration failed")
        } finally {
            _loading.value = false
        }
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        _loading.value = true
        val result = authRepository.signInWithEmail(email, password)

        if (result.isSuccess) {
            // Проверяем верификацию email после успешного входа
            val user = FirebaseAuth.getInstance().currentUser
            user?.reload()?.await() // Обновляем данные пользователя

            if (user?.isEmailVerified == false) {
                // Если email не верифицирован
                _authState.value = AuthState.Error("Email not verified. Please check your inbox.")
                authRepository.signOut() // Разлогиниваем пользователя
            } else {
                // Обновляем статус верификации в Firestore
                user?.uid?.let { uid ->
                    firestore.collection("users").document(uid)
                        .update("emailVerified", true)
                        .await()
                }
            }
        } else {
            _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
        }
        _loading.value = false
    }
}

