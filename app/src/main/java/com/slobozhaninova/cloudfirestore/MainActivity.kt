package com.slobozhaninova.cloudfirestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.slobozhaninova.cloudfirestore.ui.theme.CloudFirestoreTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CloudFirestoreTheme {
                val viewModel = hiltViewModel<FirestoreViewModel>()
                val uiState by viewModel.uiState.collectAsState()

                FirestoreScreen(
                    uiState = uiState,
                    getItems = viewModel::getItems,
                    clearError = viewModel::clearError,
                    clearMessage = viewModel::clearMessage,
                    createEmail = viewModel::createEmail,
                    checkAndLoadEmail = viewModel::checkAndLoadEmail,
                    addItem = viewModel::addItem,

                )
            }
        }
    }
}
