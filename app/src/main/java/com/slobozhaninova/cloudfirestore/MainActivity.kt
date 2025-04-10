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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.getValue
import com.slobozhaninova.cloudfirestore.presentation.screen.auth.AuthScreen
import com.slobozhaninova.cloudfirestore.presentation.screen.auth.RegisterScreen
import com.slobozhaninova.cloudfirestore.presentation.screen.auth.VerifyEmailScreen
import com.slobozhaninova.cloudfirestore.presentation.screen.library.LibraryScreen
import com.slobozhaninova.cloudfirestore.presentation.viewmodel.AuthViewModel
import com.slobozhaninova.cloudfirestore.presentation.viewmodel.LibraryViewModel
import com.slobozhaninova.cloudfirestore.ui.theme.CloudFirestoreTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CloudFirestoreTheme {
                AppNavigation()
            }
        }
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {
        composable("auth") {
            val viewModel = hiltViewModel<AuthViewModel>()
            val authState by viewModel.authState.collectAsState()
            val loading by viewModel.loading.collectAsState()
            AuthScreen(
                onSignInSuccess = {
                    if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {
                        navController.navigate("library")
                    } else {
                        navController.navigate("verify")
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                authState = authState,
                loading = loading,
                checkEmailVerification = viewModel::checkEmailVerification,
                signIn = viewModel::signIn
            )
        }
        composable("register") {
            val viewModel = hiltViewModel<AuthViewModel>()
            val loading by viewModel.loading.collectAsState()
            val registrationState by viewModel.registrationState.collectAsState()
            RegisterScreen(
                loading = loading,
                registrationState = registrationState,
                onBack = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("verify") },

            )
        }
        composable("verify") {
            VerifyEmailScreen(
                onVerified = { navController.navigate("library") },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.popBackStack("auth", inclusive = false)
                }
            )
        }
        composable("library") {
            val viewModel = hiltViewModel<LibraryViewModel>()
            val itemsWithDetails by viewModel.itemsWithDetails.collectAsState()
            val loading by viewModel.loading.collectAsState()
            val error by viewModel.error.collectAsState()
            LibraryScreen(
                itemsWithDetails = itemsWithDetails,
                loading = loading,
                error = error,
                addItem = viewModel::addItem,
                signOut = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("auth") },
                deleteItem = viewModel::deleteItem
            )
        }
    }
}