package com.example.loginsignup.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.R
import com.example.loginsignup.components.HeadingTextComponent
import com.example.loginsignup.components.MyTextField
import com.example.loginsignup.viewModels.UserViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

private enum class ForgotStep {
    ENTER_EMAIL,
    ANSWER_SECURITY,
    ENTER_NEW_PASSWORD,
    SUCCESS
}

@Composable
fun ForgotPasswordScreen(
    uvm: UserViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var securityAnswer by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var step by remember { mutableStateOf(ForgotStep.ENTER_EMAIL) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeadingTextComponent(value = "Reset Password")
            Spacer(modifier = Modifier.height(20.dp))

            // Error message
            if (!error.isNullOrBlank()) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            when (step) {
                ForgotStep.ENTER_EMAIL -> {
                    Text(
                        text = "Enter your email address",
                        modifier = Modifier.align(Alignment.Start)
                    )
                    MyTextField(
                        labelValue = stringResource(id = R.string.email),
                        painterResource = painterResource(id = R.drawable.email_symbol),
                        textValue = email,
                        onValueChange = { email = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                error = "Email cannot be empty"
                                return@Button
                            }
                            error = null

                            scope.launch {

                                val exists = uvm.checkEmailExists(email)
                                Log.d("ForgotPasswordScreen", "Checking email: $exists")
                                if (exists) {
                                    error = null
                                    step = ForgotStep.ANSWER_SECURITY
                                } else {
                                    error = "Email not found"
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SkipNext,
                            contentDescription = "Next"
                        )
                        Text(text = "Next")
                    }
                }

                ForgotStep.ANSWER_SECURITY -> {
                    Text(
                        text = "Enter the answer to your security question",
                        modifier = Modifier.align(Alignment.Start)
                    )

                    MyTextField(
                        labelValue = stringResource(id = R.string.security_question),
                        painterResource = painterResource(id = R.drawable.security_symbol),
                        textValue = securityAnswer,
                        onValueChange = { securityAnswer = it },
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (securityAnswer.isBlank()) {
                                error = "Please enter your answer"
                                return@Button
                            }
                            error = null

                            scope.launch {
                                val correct = uvm.verifySecurityAnswer(email, securityAnswer)
                                if (correct) {
                                    step = ForgotStep.ENTER_NEW_PASSWORD
                                } else {
                                    error = "Incorrect answer. Please try again."
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SkipNext,
                            contentDescription = "Submit"
                        )
                        Text(text = "Submit")
                    }
                }

                ForgotStep.ENTER_NEW_PASSWORD -> {
                    Text(
                        text = "Enter a new password",
                        modifier = Modifier.align(Alignment.Start)
                    )

                    MyTextField(
                        labelValue = stringResource(id = R.string.password),
                        painterResource = painterResource(id = R.drawable.lock_icon),
                        textValue = newPassword,
                        onValueChange = { newPassword = it },
                        // if your MyTextField supports password mode, pass the right flag here
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {

                            error = null

                            scope.launch {
                                val ok = uvm.resetPassword(email, newPassword)
                                if (ok) {
                                    step = ForgotStep.SUCCESS
                                } else {
                                    error = "Something went wrong. Please try again."
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "Reset Password")
                    }
                }

                ForgotStep.SUCCESS -> {
                    Spacer(modifier = Modifier.height(40.dp))
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(80.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Password reset successfully!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You can now close this page and log in with your new password.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
