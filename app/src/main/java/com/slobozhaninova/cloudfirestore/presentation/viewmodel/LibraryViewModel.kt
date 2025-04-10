package com.slobozhaninova.cloudfirestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slobozhaninova.cloudfirestore.domain.model.ItemWithDetails
import com.slobozhaninova.cloudfirestore.domain.repository.AuthRepository
import com.slobozhaninova.cloudfirestore.domain.repository.AuthorRepository
import com.slobozhaninova.cloudfirestore.domain.repository.BookRepository
import com.slobozhaninova.cloudfirestore.domain.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _itemsWithDetails = MutableStateFlow<List<ItemWithDetails>>(emptyList())
    val itemsWithDetails: StateFlow<List<ItemWithDetails>> = _itemsWithDetails

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            itemRepository.getUserItems().collect { items ->
                _loading.value = true
                val itemsWithDetails = items.map { item ->
                    val book = bookRepository.getBookById(item.bookId).getOrNull()
                    val author = authorRepository.getAuthorById(item.authorId).getOrNull()
                    ItemWithDetails(item, book, author)
                }
                _itemsWithDetails.value = itemsWithDetails
                _loading.value = false
            }
        }
    }

    fun addItem(bookTitle: String, authorName: String, rating: Int) = viewModelScope.launch {
        _loading.value = true
        _error.value = null
        val result = itemRepository.addItem(bookTitle, authorName, rating)
        _loading.value = false
        if (result.isFailure) {
            _error.value = result.exceptionOrNull()?.message ?: "Failed to add item"
        }
    }

    fun deleteItem(itemId: String) = viewModelScope.launch {
        _loading.value = true
        _error.value = null
        val result = itemRepository.deleteItem(itemId)
        _loading.value = false
        if (result.isFailure) {
            _error.value = result.exceptionOrNull()?.message ?: "Failed to delete item"
        }
    }

    fun signOut() = authRepository.signOut()
}