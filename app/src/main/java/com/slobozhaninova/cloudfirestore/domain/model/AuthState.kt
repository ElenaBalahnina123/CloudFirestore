package com.slobozhaninova.cloudfirestore.domain.model

sealed class AuthState {
    object Initial : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: Email) : AuthState()
    object SignUpSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}