package com.slobozhaninova.cloudfirestore.domain.model

data class ItemWithDetails(
    val item: ItemData,
    val book: Book?,
    val author: Author?
)