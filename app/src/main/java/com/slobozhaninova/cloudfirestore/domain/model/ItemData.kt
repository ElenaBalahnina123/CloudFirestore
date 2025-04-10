package com.slobozhaninova.cloudfirestore.domain.model

data class ItemData(
    val id: String = "",
    val bookId: String = "",
    val authorId: String = "",
    val userId : String = "",
    val rating: Int = -1
)