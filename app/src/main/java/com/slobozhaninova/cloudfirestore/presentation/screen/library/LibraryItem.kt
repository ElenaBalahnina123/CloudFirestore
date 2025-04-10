package com.slobozhaninova.cloudfirestore.presentation.screen.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.slobozhaninova.cloudfirestore.domain.model.Author
import com.slobozhaninova.cloudfirestore.domain.model.Book
import com.slobozhaninova.cloudfirestore.domain.model.ItemData
import com.slobozhaninova.cloudfirestore.domain.model.ItemWithDetails

class PreviewParameterLibrary() : PreviewParameterProvider<ItemWithDetails> {
    override val values: Sequence<ItemWithDetails>
        get() = sequenceOf(
            ItemWithDetails(ItemData(rating = 5), Book(title = "fddfd"), Author(name = "fdfd")),
        )
}

@Preview
@Composable
fun LibraryItem(
    @PreviewParameter(PreviewParameterLibrary::class)
    itemWithDetails: ItemWithDetails,
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            itemWithDetails.book?.let {
                Text(text = it.title)
            }
            itemWithDetails.author?.let {
                Text(text = it.name)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${itemWithDetails.item.rating}")

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}