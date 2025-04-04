package com.slobozhaninova.cloudfirestore

data class Email(
    val id: String = "",
    val email: String = ""
)

data class ItemData(
    val id: String = "",
    val book: Book = Book(),
    val author: Author = Author(),
    val rating: Int = -1
)

data class Author(
    val id: String = "",
    val name: String = ""
)

data class Book(
    val id: String = "",
    val title: String = ""
)