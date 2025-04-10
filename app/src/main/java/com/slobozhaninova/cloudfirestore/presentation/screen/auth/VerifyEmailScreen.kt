package com.slobozhaninova.cloudfirestore.presentation.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.slobozhaninova.cloudfirestore.presentation.viewmodel.AuthViewModel

@Composable
fun VerifyEmailScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onVerified: () -> Unit,
    onLogout: () -> Unit
) {
    var resendLoading by remember { mutableStateOf(false) }
    var resendSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Email,
            contentDescription = "Email",
            modifier = Modifier.size(64.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Подтвердите ваш Email",
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Мы отправили вам письмо с подтверждением на почту. Пожалуйста, подтвердите свой адрес электронной почты, чтобы продолжить.",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                resendLoading = true

                try {
                    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                    resendSuccess = true
                } catch (e: Exception) {
                    // Обработка ошибки
                } finally {
                    resendLoading = false
                }

            },
            enabled = !resendLoading
        ) {
            if (resendLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Text("Повторно отправить письмо с подтверждением")
            }
        }

        if (resendSuccess) {
            Text(
                text = "Письмо с подтверждением отправлено!",
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.checkEmailVerification()
                if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {
                    onVerified()
                }

            }
        ) {
            Text("Я подтвердил свой адрес электронной почты")
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onLogout) {
            Text("Выйти")
        }
    }
}