package com.slobozhaninova.cloudfirestore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun FirestoreScreen(
    uiState: FirestoreUiState,
    getItems: (String) -> Unit,
    clearError: () -> Unit,
    clearMessage: () -> Unit,
    createEmail: (String) -> Unit,
    checkAndLoadEmail: (String) -> Unit,
    addItem: (String, String, String, Int) -> Unit
) {


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Книги",
                    )
                }
            )
        }
    ) { padding ->

        LaunchedEffect(uiState.currentEmailId) {
            uiState.currentEmailId?.let { emailId ->
                getItems(emailId)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            EmailInputSection(
                onCreateEmail = { email ->
                    createEmail(email)
                },
                onCheckEmail = { email ->
                    checkAndLoadEmail(email)
                }

            )

            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                uiState.error != null -> ErrorDialog(uiState.error, onDismiss = clearError)
                uiState.message != null -> InfoText(uiState.message, onDismiss = clearMessage)
                uiState.items.isNotEmpty() -> ItemsListSection(uiState.items)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.currentEmailId != null) {
                ItemInputSection(
                    onAddItem = { book, author, rating ->
                        addItem(uiState.currentEmailId, book, author, rating)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }
}


@Composable
private fun EmailInputSection(
    onCreateEmail: (String) -> Unit,
    onCheckEmail: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { onCreateEmail(email) },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("Зарегистрироваться")
            }
            Button(onClick = { onCheckEmail(email) }) {
                Text("Войти")
            }
        }
    }
}

@Preview
@Composable
private fun ItemInputSection(onAddItem: (String, String, Int) -> Unit = { _, _, _ -> }) {
    var bookTitle by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    val ratings = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Column {
            OutlinedTextField(
                value = bookTitle,
                onValueChange = { bookTitle = it },
                label = { Text("Название книги") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = authorName,
                onValueChange = { authorName = it },
                label = { Text("Автор") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Рейтинг: $rating")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ratings.forEach { ratingOption ->
                        DropdownMenuItem(
                            content = { Text("$ratingOption") },
                            onClick = {
                                rating = ratingOption
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onAddItem(bookTitle, authorName, rating) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Создать")
            }
        }
    }
}

@Composable
private fun ItemsListSection(
    items: List<ItemData>
) {
    Column {
        Text(
            text = "Список книг:",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when {
            items.isEmpty() -> Text("Нет книг")
            else -> LazyColumn(modifier = Modifier.heightIn(max = 700.dp)) {
                items(items, key = { it.id }) { item ->
                    ItemCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun ItemCard(
    item: ItemData
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Книга: ${item.book.title}")
            Text(text = "Автор: ${item.author.name}")
            Text(text = "Рейтинг: ${item.rating}")
        }
    }
}


@Composable
private fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ошибка") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
private fun InfoText(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Информация") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}