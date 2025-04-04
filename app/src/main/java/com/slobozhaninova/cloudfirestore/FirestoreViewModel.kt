package com.slobozhaninova.cloudfirestore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slobozhaninova.cloudfirestore.domain.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirestoreViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FirestoreUiState())
    val uiState: StateFlow<FirestoreUiState> = _uiState.asStateFlow()

    fun createEmail(email: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        when (val result = repository.createEmail(Email(email = email))) {
            is Result.Success -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentEmailId = result.value,
                        message = "Email успешно создан"
                    )
                }
            }
            is Result.Failure -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun addItem(emailId: String, bookTitle: String, authorName: String, rating: Int) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val itemData = ItemData(
            book = Book(title = bookTitle),
            author = Author(name = authorName),
            rating = rating
        )

        when (val result = repository.createItemData(emailId, itemData)) {
            is Result.Success -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = "Книги успешно загружены"
                    )
                }
            }
            is Result.Failure -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun getItems(emailId: String) = viewModelScope.launch {
        repository.getItemsByEmail(emailId)
            .catch { e ->
                _uiState.update { it.copy(error = e.message ?: "Unknown error") }
            }
            .collect { items ->
                _uiState.update { it.copy(items = items) }
            }
    }
    fun checkAndLoadEmail(email: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, currentEmail = email) }

        // 1. Проверяем существует ли email
        when (val emailCheck = repository.checkEmailExists(email)) {
            is Result.Success -> {
                emailCheck.value?.let { emailId ->
                    // 2. Если существует - загружаем его записи
                    loadEmailRecords(emailId)
                } ?: run {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = "Email не найден, создайте новый"
                        )
                    }
                }
            }
            is Result.Failure -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Email check failed: ${emailCheck.exception.message}"
                    )
                }
            }
        }
    }

    private suspend fun loadEmailRecords(emailId: String) {
        when (val recordsResult = repository.getEmailRecords(emailId)) {
            is Result.Success -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentEmailId = emailId,
                        items = recordsResult.value,
                        message = "Загрузка ${recordsResult.value.size} книг"
                    )
                }
            }
            is Result.Failure -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load records: ${recordsResult.exception.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

data class FirestoreUiState(
    val isLoading: Boolean = false,
    val currentEmail: String? = null,
    val currentEmailId: String? = null,
    val items: List<ItemData> = emptyList(),
    val error: String? = null,
    val message: String? = null
)