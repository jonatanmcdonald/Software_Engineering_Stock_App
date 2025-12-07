package com.example.loginsignup.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.R
import com.example.loginsignup.components.HeadingTextComponent
import com.example.loginsignup.components.MyTextField
import com.example.loginsignup.components.NormalTextComponent
import com.example.loginsignup.components.PasswordTextFieldComponent
import com.example.loginsignup.viewModels.UserViewModel

@Composable
fun LogInScreen(onSignedIn: () -> Unit, // Callback for when the user successfully signs in
                onForgotPassword: () -> Unit, // Callback for when the user clicks the forgot password button
                userViewModel: UserViewModel = viewModel() // Injects the UserViewModel
)
{
    var email by remember { mutableStateOf("") } // State for the email input field
    var password by remember { mutableStateOf("") } // State for the password input field

    val loginResult by userViewModel.loginResult.observeAsState() // Observes the login result from the ViewModel
    val context = LocalContext.current // Gets the current Android context

/*
    LaunchedEffect(loginResult) {
        if (loginResult == true) {
            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
            onSignedIn()
        } else if (loginResult == false) {
            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT). show()
        }*/
    LaunchedEffect(loginResult) { // A coroutine that runs when the loginResult changes
        when (loginResult) { // A when statement to handle the different login results
            true -> { // If the login was successful
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show() // Show a success message
                onSignedIn() // Call the onSignedIn callback
        }
            false -> { // If the login failed
                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT). show() // Show an error message
            }
            null -> { } // If the login result is null, do nothing
    }
}

    Surface( // A basic Material Design surface
        modifier = Modifier
            .fillMaxSize() // Fills the maximum available size
            .background(MaterialTheme.colorScheme.background) // Sets the background color
            .padding(28.dp) // Adds padding
    ) {
        Column(modifier = Modifier.fillMaxSize() // A vertically arranged layout that fills the maximum size
            .background(MaterialTheme.colorScheme.background), // Sets the background color
            verticalArrangement = Arrangement.spacedBy(12.dp) // Adds spacing between the children
        ) {
            NormalTextComponent(value = stringResource(id = R.string.hello)) // Displays a normal text component with the hello string
            HeadingTextComponent(value = stringResource(id = R.string.log_in)) // Displays a heading text component with the log in string

            Spacer(modifier = Modifier.height(20.dp)) // Adds vertical space

            MyTextField( // A custom text field for the email input
                labelValue = stringResource(id = R.string.email), // The label for the text field
                painterResource(id = R.drawable.email_symbol), // The icon for the text field
                textValue = email, // The value of the text field
                onValueChange = { email = it } // Updates the email state when the value changes
            )

            PasswordTextFieldComponent( // A custom text field for the password input
                labelValue = stringResource(id = R.string.password), // The label for the text field
                painterResource(id = R.drawable.lock_icon), // The icon for the text field
                password = password, // The value of the text field
                onPasswordChange = { password = it } // Updates the password state when the value changes
            )



            Button( // A button for logging in
                onClick = { // The action to perform when the button is clicked
                    //Log.d("Sign Up Screen",Email: $email, Password: $password")
                    userViewModel.login(email, password) // Calls the login function in the ViewModel

                },modifier = Modifier
                        .fillMaxWidth()      // full width
                    .height(56.dp),      // large height
                shape = RectangleShape, // Sets the shape of the button
                colors = ButtonDefaults.buttonColors( // Sets the colors of the button
                    containerColor = Color(0xFF00E0C7), // teal accent
                    contentColor = Color.Black          // text color
                ), contentPadding = PaddingValues(vertical = 12.dp) // Adds padding to the content of the button
            ){
                Text(text = "Log In", fontSize = 18.sp) // The text to display on the button
            }

            Spacer(modifier = Modifier.height(20.dp)) // Adds vertical space

            Text( // A text component for the forgot password button
                text = "Forgot your password?", // The text to display
                modifier = Modifier.clickable { // Makes the text clickable
                    onForgotPassword() // Calls the onForgotPassword callback when the text is clicked
                }
            )


        }
    }
}


@Preview
@Composable
fun DefaultPreviewOfLogInScreen() {
    LogInScreen(onSignedIn = {}, onForgotPassword = {})
}