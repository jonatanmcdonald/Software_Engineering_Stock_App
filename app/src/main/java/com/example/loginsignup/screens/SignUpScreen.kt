package com.example.loginsignup.screens

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.R
import com.example.loginsignup.components.HeadingTextComponent
import com.example.loginsignup.components.MyTextField
import com.example.loginsignup.components.NormalTextComponent
import com.example.loginsignup.components.PasswordTextFieldComponent
import com.example.loginsignup.data.db.entity.User
import com.example.loginsignup.viewModels.UserViewModel

// This function checks if the given email address has a valid format.
fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun SignUpScreen(onViewTerms: () -> Unit, // Callback function to navigate to the Terms and Conditions screen.
                 onViewSignIn: () -> Unit, // Callback function to navigate to the Sign In screen.
                 userViewModel: UserViewModel = viewModel(), // Injects the UserViewModel.
                 )
{

    var firstName by remember { mutableStateOf("") } // State for the first name input field.
    var lastName by remember {mutableStateOf("")} // State for the last name input field.
    var email by remember { mutableStateOf("") } // State for the email input field.
    var password by remember { mutableStateOf("") } // State for the password input field.
    var securityAnswer by remember {mutableStateOf("")} // State for the security answer input field.
    var checked by remember { mutableStateOf(false) } // State for the checkbox.
    val scrollState = rememberScrollState() // State for the scroll position of the column.

    val context = LocalContext.current // Gets the current Android context.

    Surface( // A basic Material Design surface.
        modifier = Modifier
            .fillMaxSize() // Fills the maximum available size.
            .background(MaterialTheme.colorScheme.background) // Sets the background color.
            .padding(30.dp) // Adds padding to the surface.
    ) {
        Column(modifier = Modifier.fillMaxSize() // A vertically arranged layout that fills the maximum size.
            .verticalScroll(scrollState) // Makes the column scrollable.
            .background(MaterialTheme.colorScheme.background), // Sets the background color.

            verticalArrangement = Arrangement.spacedBy(5.dp) // Adds spacing between the children.
        ) {
            NormalTextComponent(value = stringResource(id = R.string.hello)) // Displays a normal text component with the hello string.
            HeadingTextComponent(value = stringResource(id = R.string.create_account)) // Displays a heading text component with the create account string.

            Spacer(modifier = Modifier.height(12.dp)) // Adds vertical space.


            MyTextField( // A custom text field for the first name input.

                labelValue = stringResource(id = R.string.first_name), // The label for the text field.
                painterResource(id = R.drawable.user_icon), // The icon for the text field.
                textValue = firstName, // The value of the text field.
                onValueChange = {firstName = it}, // Updates the first name state when the value changes.

            )
            Spacer(modifier = Modifier.height(12.dp)) // Adds vertical space.

            MyTextField( // A custom text field for the last name input.
                labelValue = stringResource(id = R.string.last_name), // The label for the text field.
                painterResource(id = R.drawable.user_icon), // The icon for the text field.
                textValue = lastName, // The value of the text field.
                onValueChange = {lastName = it} // Updates the last name state when the value changes.
            )
            Spacer(modifier = Modifier.height(12.dp)) // Adds vertical space.

            MyTextField( // A custom text field for the email input.
                labelValue = stringResource(id = R.string.email), // The label for the text field.
                painterResource(id = R.drawable.email_symbol), // The icon for the text field.
                textValue = email, // The value of the text field.
                onValueChange = {email = it} // Updates the email state when the value changes.
            )
            Spacer(modifier = Modifier.height(12.dp)) // Adds vertical space.

            PasswordTextFieldComponent( // A custom text field for the password input.
                labelValue = stringResource(id = R.string.password), // The label for the text field.
                painterResource(id = R.drawable.lock_icon), // The icon for the text field.
                password = password, // The value of the text field.
                onPasswordChange = {password = it} // Updates the password state when the value changes.
            )
            Spacer(modifier = Modifier.height(12.dp)) // Adds vertical space.

            MyTextField( // A custom text field for the security question input.
                labelValue = stringResource(id = R.string.security_question), // The label for the text field.
                painterResource(id = R.drawable.security_symbol), // The icon for the text field.
                textValue = securityAnswer, // The value of the text field.
                onValueChange = {securityAnswer = it}, // Updates the security answer state when the value changes.
            )

            Spacer(modifier = Modifier.height(12.dp)) // Adds vertical space.
            Row( // A horizontally arranged layout for the checkbox and the terms and conditions text.
                verticalAlignment = Alignment.CenterVertically, // Vertically centers the children.
                modifier = Modifier.fillMaxWidth() // Fills the maximum available width.
            ) {

                Checkbox( // A checkbox for agreeing to the terms and conditions.
                    checked = checked, // The current state of the checkbox.
                    onCheckedChange = { checked = it } // Updates the state when the checkbox is checked or unchecked.
                )

                Text( // The text for the terms and conditions.
                    text = "Accept Terms and Conditions",
                    fontSize = 16.sp,
                    color = Color(0xFF00E0C7),
                    textDecoration = TextDecoration.Underline, // Underlines the text.
                    modifier = Modifier.clickable { // Makes the text clickable.
                        onViewTerms() // Calls the onViewTerms callback when the text is clicked.
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp)) // Adds vertical space.

            Button( // A button for signing up.
                onClick = { // The action to perform when the button is clicked.
                when { // A when statement to validate the input fields.
                    !isValidEmail(email) -> { // If the email is invalid.
                        Toast.makeText(context, "Email is invalid", Toast.LENGTH_SHORT).show() // Show an error message.
                    }
                    password.isBlank() -> { // If the password is blank.
                        Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show() // Show an error message.
                    }
                    securityAnswer.isBlank() -> { // If the security answer is blank.
                        Toast.makeText(context, "Security Question response cannot be empty", Toast.LENGTH_SHORT).show() // Show an error message.
                    }
                    else -> { // If all input fields are valid.
                        Log.d( // Logs the user's input.
                            "Sign Up Screen",
                            "FirstName: $firstName, LastName: $lastName, Email: $email, Password: $password"
                        )
                        //inserting new user to Database
                        val newUser = User( // Creates a new User object.
                            0,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            password = password,
                            securityAnswer = securityAnswer
                        )

                        userViewModel.signUpUser(newUser) { success -> // Calls the signUpUser function in the ViewModel.
                            if (success) { // If the sign up was successful.
                                Toast.makeText(context, "Sign Up Successful!", Toast.LENGTH_SHORT) // Show a success message.
                                    .show()
                                onViewSignIn()
                            } else { // If the sign up failed.
                                Toast.makeText( // Show an error message.
                                    context,
                                    "Email is already taken!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        }
                    }
                }

            }, modifier = Modifier
                .fillMaxWidth()      // full width
                .height(50.dp),      // large height
                shape = RectangleShape, // Sets the shape of the button.
                colors = ButtonDefaults.buttonColors( // Sets the colors of the button.
                    containerColor = Color(0xFF00E0C7), // teal accent
                    contentColor = Color.Black          // text color
                ), contentPadding = PaddingValues(vertical = 12.dp), // Adds padding to the content of the button.
                enabled = checked // The button is enabled only if the checkbox is checked.
            ){
                    Text(text = "Sign Up", fontSize = 18.sp) // The text to display on the button.
            }
            Spacer(modifier = Modifier.height(12.dp)) // Adds vertical space.
            Text( // A text component for the forgot password button
                text = "Already have an account?",
                fontSize = 16.sp,
                color = Color(0xFF00E0C7),
                textDecoration = TextDecoration.Underline,// The text to display
                modifier = Modifier.clickable { // Makes the text clickable
                    onViewSignIn() // Calls the onForgotPassword callback when the text is clicked
                }
            )




        }

    }
}



@Preview
@Composable
fun DefaultPreviewOfSignUpScreen(){
    SignUpScreen(onViewTerms = {}, onViewSignIn = {})
}

