package com.example.loginsignup.screens // Package declaration for the screen

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

// Enum to represent the steps in the forgot password flow
private enum class ForgotStep {
    ENTER_EMAIL, // Step to enter email
    ANSWER_SECURITY, // Step to answer security question
    ENTER_NEW_PASSWORD, // Step to enter new password
    SUCCESS // Step for success message
}

@Composable
fun ForgotPasswordScreen(
    uvm: UserViewModel = viewModel() // Inject the UserViewModel
) {
    var email by remember { mutableStateOf("") } // State for email input
    var securityAnswer by remember { mutableStateOf("") } // State for security answer input
    var newPassword by remember { mutableStateOf("") } // State for new password input

    var step by remember { mutableStateOf(ForgotStep.ENTER_EMAIL) } // State to track the current step
    var error by remember { mutableStateOf<String?>(null) } // State for error messages

    val scope = rememberCoroutineScope() // Coroutine scope for launching async operations

    Surface(
        modifier = Modifier
            .fillMaxSize() // Fill the entire screen
            .background(MaterialTheme.colorScheme.background) // Set background color
            .padding(28.dp), // Add padding
        color = MaterialTheme.colorScheme.background // Set surface color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(), // Fill the entire column
            verticalArrangement = Arrangement.spacedBy(12.dp), // Arrange children with spacing
            horizontalAlignment = Alignment.CenterHorizontally // Center children horizontally
        ) {
            HeadingTextComponent(value = "Reset Password") // Display the heading
            Spacer(modifier = Modifier.height(20.dp)) // Add vertical space

            // Error message display
            if (!error.isNullOrBlank()) {
                Text(
                    text = error!!, // Display the error message
                    color = MaterialTheme.colorScheme.error, // Set error text color
                    style = MaterialTheme.typography.bodyMedium, // Set text style
                )
                Spacer(modifier = Modifier.height(8.dp)) // Add vertical space
            }

            // A when statement to control which part of the UI is shown based on the current step.
            when (step) {
                ForgotStep.ENTER_EMAIL -> { // UI for entering email
                    Text(
                        text = "Enter your email address", // Prompt text
                        modifier = Modifier.align(Alignment.Start) // Align to the start
                    )
                    MyTextField(
                        labelValue = stringResource(id = R.string.email), // Label for text field
                        painterResource = painterResource(id = R.drawable.email_symbol), // Icon for text field
                        textValue = email, // Bind to email state
                        onValueChange = { email = it } // Update email state on change
                    )

                    Spacer(modifier = Modifier.height(12.dp)) // Add vertical space

                    Button(
                        onClick = {
                            if (email.isBlank()) { // Validate email input
                                error = "Email cannot be empty" // Set error message
                                return@Button // Exit the onClick lambda
                            }
                            error = null // Clear any previous error

                            scope.launch { // Launch a coroutine
                                val exists = uvm.checkEmailExists(email) // Check if email exists
                                Log.d("ForgotPasswordScreen", "Checking email: $exists") // Log the result
                                if (exists) { // If email exists
                                    error = null // Clear error
                                    step = ForgotStep.ANSWER_SECURITY // Move to the next step
                                } else { // If email does not exist
                                    error = "Email not found" // Set error message
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End) // Align button to the end
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SkipNext, // "Next" icon
                            contentDescription = "Next" // Content description for accessibility
                        )
                        Text(text = "Next") // Button text
                    }
                }

                ForgotStep.ANSWER_SECURITY -> { // UI for answering security question
                    Text(
                        text = "Enter the answer to your security question", // Prompt text
                        modifier = Modifier.align(Alignment.Start) // Align to the start
                    )

                    MyTextField(
                        labelValue = stringResource(id = R.string.security_question), // Label for text field
                        painterResource = painterResource(id = R.drawable.security_symbol), // Icon for text field
                        textValue = securityAnswer, // Bind to securityAnswer state
                        onValueChange = { securityAnswer = it }, // Update securityAnswer state on change
                    )

                    Spacer(modifier = Modifier.height(12.dp)) // Add vertical space

                    Button(
                        onClick = {
                            if (securityAnswer.isBlank()) { // Validate security answer input
                                error = "Please enter your answer" // Set error message
                                return@Button // Exit the onClick lambda
                            }
                            error = null // Clear any previous error

                            scope.launch { // Launch a coroutine
                                val correct = uvm.verifySecurityAnswer(email, securityAnswer) // Verify the security answer
                                if (correct) { // If the answer is correct
                                    step = ForgotStep.ENTER_NEW_PASSWORD // Move to the next step
                                } else { // If the answer is incorrect
                                    error = "Incorrect answer. Please try again." // Set error message
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End) // Align button to the end
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SkipNext, // "Submit" icon
                            contentDescription = "Submit" // Content description for accessibility
                        )
                        Text(text = "Submit") // Button text
                    }
                }

                ForgotStep.ENTER_NEW_PASSWORD -> { // UI for entering new password
                    Text(
                        text = "Enter a new password", // Prompt text
                        modifier = Modifier.align(Alignment.Start) // Align to the start
                    )

                    MyTextField(
                        labelValue = stringResource(id = R.string.password), // Label for text field
                        painterResource = painterResource(id = R.drawable.lock_icon), // Icon for text field
                        textValue = newPassword, // Bind to newPassword state
                        onValueChange = { newPassword = it }, // Update newPassword state on change
                        // if your MyTextField supports password mode, pass the right flag here
                    )

                    Spacer(modifier = Modifier.height(12.dp)) // Add vertical space

                    Button(
                        onClick = {

                            error = null // Clear any previous error

                            scope.launch { // Launch a coroutine
                                val ok = uvm.resetPassword(email, newPassword) // Reset the password
                                if (ok) { // If password reset was successful
                                    step = ForgotStep.SUCCESS // Move to the success step
                                } else { // If password reset failed
                                    error = "Something went wrong. Please try again." // Set error message
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End) // Align button to the end
                    ) {
                        Text(text = "Reset Password") // Button text
                    }
                }

                ForgotStep.SUCCESS -> { // UI for success message
                    Spacer(modifier = Modifier.height(40.dp)) // Add vertical space
                    Icon(
                        imageVector = Icons.Filled.CheckCircle, // Success icon
                        contentDescription = "Success", // Content description for accessibility
                        tint = MaterialTheme.colorScheme.primary, // Set icon color
                        modifier = Modifier.height(80.dp) // Set icon height
                    )
                    Spacer(modifier = Modifier.height(20.dp)) // Add vertical space
                    Text(
                        text = "Password reset successfully!", // Success message
                        style = MaterialTheme.typography.headlineSmall // Set text style
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // Add vertical space
                    Text(
                        text = "You can now close this page and log in with your new password.", // Informative text
                        style = MaterialTheme.typography.bodyMedium // Set text style
                    )
                }
            }
        }
    }
}
