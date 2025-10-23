package com.example.loginsignup.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.R
import com.example.loginsignup.components.HeadingTextComponent
import com.example.loginsignup.components.MyTextField
import com.example.loginsignup.components.NormalTextComponent
import com.example.loginsignup.components.PasswordTextFieldComponent
import com.example.loginsignup.viewModels.UserViewModel

@Composable
fun LogInScreen(onSignedIn: () -> Unit,
                onViewTerms: () -> Unit,
                //stockAppViewModel: StockAppViewModel? = null
                userViewModel: UserViewModel = viewModel()
)
{
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("")}

    val loginResult by userViewModel.loginResult.observeAsState()
    val context = LocalContext.current


    LaunchedEffect(loginResult) {
        if (loginResult == true) {
            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
            onSignedIn()
        } else if (loginResult == false) {
            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT). show()
        }
}

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NormalTextComponent(value = stringResource(id = R.string.hello))
            HeadingTextComponent(value = stringResource(id = R.string.log_in))

            Spacer(modifier = Modifier.height(20.dp))

            MyTextField(
                labelValue = stringResource(id = R.string.email),
                painterResource(id = R.drawable.email_symbol),
                textValue = email,
                onValueChange = { email = it })


            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                painterResource(id = R.drawable.lock_icon),
                password = password,
                onPasswordChange = { password = it }
            )

            Button(
                onClick = {
                    //Log.d("Sign Up Screen",Email: $email, Password: $password")
                    userViewModel.login(email, password)

                    //uncomment for testing homescreen
                    onSignedIn()
            },
                modifier = Modifier.fillMaxWidth()
                ) {
                Text(text = "Log In")
            }
        }
    }
}


@Preview
@Composable
fun DefaultPreviewOfLogInScreen() {
    LogInScreen(onSignedIn = {}, onViewTerms = {})
}