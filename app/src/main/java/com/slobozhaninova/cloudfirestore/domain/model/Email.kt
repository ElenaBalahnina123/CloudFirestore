package com.slobozhaninova.cloudfirestore.domain.model

data class Email(
    val id: String = "",
    val email: String = "",
    val password : String = "",
    val isEmailVerified: Boolean = false
)