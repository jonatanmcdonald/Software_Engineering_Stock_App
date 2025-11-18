package com.example.loginsignup.screens

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
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
import com.example.loginsignup.data.db.entity.User
import com.example.loginsignup.viewModels.UserViewModel

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun SignUpScreen(onViewTerms: () -> Unit,
                 onViewSignIn: () -> Unit,
                 userViewModel: UserViewModel = viewModel())
{

    var firstName by remember { mutableStateOf("") }
    var lastName by remember {mutableStateOf("")}
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp) // space between buttons
        ) {
            NormalTextComponent(value = stringResource(id = R.string.hello))
            HeadingTextComponent(value = stringResource(id = R.string.create_account))

            Spacer(modifier = Modifier.height(20.dp))

            MyTextField(

                labelValue = stringResource(id = R.string.first_name),
                painterResource(id = R.drawable.user_icon),
                textValue = firstName,
                onValueChange = {firstName = it}

            )

            MyTextField(
                labelValue = stringResource(id = R.string.last_name),
                painterResource(id = R.drawable.user_icon),
                textValue = lastName,
                onValueChange = {lastName = it}
            )

            MyTextField(
                labelValue = stringResource(id =R.string.email),
                painterResource(id = R.drawable.email_symbol),
                textValue = email,
                onValueChange = {email = it}
            )

            PasswordTextFieldComponent(
                labelValue = stringResource(id =R.string.password),
                painterResource(id = R.drawable.lock_icon),
                password = password,
                onPasswordChange = {password = it}
            )

            Button(
                onClick = {
                when {
                    !isValidEmail(email) -> {
                        Toast.makeText(context, "Email is invalid", Toast.LENGTH_SHORT).show()
                    }
                    password.isBlank() -> {
                        Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Log.d(
                            "Sign Up Screen",
                            "FirstName: $firstName, LastName: $lastName, Email: $email, Password: $password"
                        )
                        //inserting new user to Database
                        val newUser = User(
                            0,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            password = password,


                        )

                        userViewModel.signUpUser(newUser) { success ->
                            if (success) {
                                Toast.makeText(context, "Sign Up Succesful!", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
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
                .height(56.dp),      // large height
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E0C7), // teal accent
                    contentColor = Color.Black          // text color
                ), contentPadding = PaddingValues(vertical = 12.dp)
            ){
                    Text(text = "Sign Up", fontSize = 18.sp)
            }

            Button(onClick = {
                // Check if the user is signed in
                onViewSignIn()
            }, modifier = Modifier
                .fillMaxWidth()      // full width
                .height(56.dp),      // large height
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E0C7), // teal accent
                    contentColor = Color.Black          // text color
                ), contentPadding = PaddingValues(vertical = 12.dp)
            )
            {
                Text(text = "Sign In", fontSize = 18.sp)
            }

            Button(onClick = {
                onViewTerms()
            }, modifier = Modifier
                .fillMaxWidth()      // full width
                .height(56.dp),      // large height
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E0C7), // teal accent
                    contentColor = Color.Black          // text color
                ), contentPadding = PaddingValues(vertical = 12.dp)
            )
            {
                Text(text = "Go to Terms and Condition", fontSize = 18.sp)
            }
        }

    }
}



@Preview
@Composable
fun DefaultPreviewOfSignUpScreen(){
    SignUpScreen(onViewTerms = {}, onViewSignIn = {})
}

