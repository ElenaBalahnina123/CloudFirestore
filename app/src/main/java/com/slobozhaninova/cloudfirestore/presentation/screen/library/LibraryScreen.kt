package com.slobozhaninova.cloudfirestore.presentation.screen.library

import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.slobozhaninova.cloudfirestore.domain.model.Author
import com.slobozhaninova.cloudfirestore.domain.model.Book
import com.slobozhaninova.cloudfirestore.domain.model.ItemData
import com.slobozhaninova.cloudfirestore.domain.model.ItemWithDetails
import com.slobozhaninova.cloudfirestore.ui.theme.CloudFirestoreTheme

class PreviewParameterLibraryScreen() : PreviewParameterProvider<List<ItemWithDetails>> {
    override val values: Sequence<List<ItemWithDetails>>
        get() = sequenceOf(
            listOf(ItemWithDetails(ItemData(rating = 5), Book(title = "fddfd"), Author(name = "fdfd")),
                ItemWithDetails(ItemData(rating = 5), Book(title = "fddfd"), Author(name = "fdfd")))
        )
}

@Composable
fun LibraryScreen(
    @PreviewParameter(PreviewParameterLibraryScreen::class)
    itemsWithDetails: List<ItemWithDetails>,
    loading: Boolean,
    error: String?,
    addItem: (String, String, Int) -> Unit ,
    signOut: ()-> Unit,
    deleteItem : (String) -> Unit
    )
{

    var showAddDialog by remember { mutableStateOf(false) }
    var bookTitle by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }

    var expanded by remember { mutableStateOf(false) }
    val ratings = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)


    CloudFirestoreTheme() {
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Создать книгу") },
                text = {
                    Column {
                        TextField(
                            value = bookTitle,
                            onValueChange = { bookTitle = it },
                            label = { Text("Название") }
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        TextField(
                            value = authorName,
                            onValueChange = { authorName = it },
                            label = { Text("Автор") }
                        )

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
                                        text = { Text("$ratingOption") },
                                        onClick = {
                                            rating = ratingOption
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            addItem(bookTitle, authorName, rating)
                            showAddDialog = false
                        }
                    ) {
                        Text("Создать")
                    }
                },
                dismissButton = {
                    Button(onClick = { showAddDialog = false }) {
                        Text("Отменить")
                    }
                }
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Cloud Firestore",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 30.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = signOut) {
                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                        }
                    },
                )
            }
        ) { scaffoldPaddings ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPaddings)
            ) {
                if (itemsWithDetails.isEmpty() && !loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Книг нет")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(itemsWithDetails) { itemWithDetails ->
                            LibraryItem(
                                itemWithDetails = itemWithDetails,
                                onDelete = { deleteItem(itemWithDetails.item.id) }
                            )
                        }
                    }
                }

                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(backgroundColor = androidx.compose.ui.graphics.Color(0xFF03A9F4))
                ) {
                    Text("Создать книгу")
                }

                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally))
                }

                error?.let {
                    Text(
                        text = it,
                        modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
                    )
                }
            }
        }
    }
}