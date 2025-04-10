package com.slobozhaninova.cloudfirestore.domain.model

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}