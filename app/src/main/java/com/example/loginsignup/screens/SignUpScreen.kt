package com.example.loginsignup.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginsignup.R
import com.example.loginsignup.components.HeadingTextComponent
import com.example.loginsignup.components.MyTextField
import com.example.loginsignup.components.NormalTextComponent
import com.example.loginsignup.components.PasswordTextFieldComponent

/*
@Composable
fun SignUpScreen(navController: NavHostController) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NormalTextComponent(value = stringResource(id = R.string.hello))
            HeadingTextComponent(value = stringResource(id = R.string.create_account))
            Spacer(modifier = Modifier.height(20.dp))
            MyTextField(
                labelValue = stringResource(id = R.string.first_name),
                painterResource(id = R.drawable.user_icon))

            MyTextField(
                labelValue = stringResource(id =R.string.last_name),
                painterResource(id = R.drawable.user_icon))

            MyTextField(
                labelValue = stringResource(id =R.string.email),
                painterResource(id = R.drawable.email_symbol))


            PasswordTextFieldComponent(
                labelValue = stringResource(id =R.string.password),
                painterResource(id = R.drawable.lock_icon)
            )



            Button(onClick = {
                navController.navigate("TermsAndConditionsScreen")
            }) {
                Text(text = "Go to Terms and Condition", fontSize = 20.sp)
            }
        }

    }
}*/

@Composable
fun SignUpScreen(onSignedIn: () -> Unit, onViewTerms: () -> Unit) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember {mutableStateOf("")}
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                labelValue = stringResource(id =R.string.last_name),
                painterResource(id = R.drawable.user_icon),
                textValue = lastName,
                onValueChange = {lastName = it})


            MyTextField(
                labelValue = stringResource(id =R.string.email),
                painterResource(id = R.drawable.email_symbol),
                textValue = email,
                onValueChange = {email = it})


            PasswordTextFieldComponent(
                labelValue = stringResource(id =R.string.password),
                painterResource(id = R.drawable.lock_icon),
                password = password,
                onPasswordChange = {password = it}
            )

            Button(onClick = {
                Log.d("Sign Up Screen", "FirstName: $firstName, LastName: $lastName, Email: $email, Password: $password")
            }){
                    Text(text = "Sign Up")
            }

            Button(onClick = {
                onViewTerms()
            }) {
                Text(text = "Go to Terms and Condition", fontSize = 20.sp)
            }

            Button(onClick = {
                onSignedIn()
            }) {
                Text(text = "Sign In", fontSize = 20.sp)
            }
        }

    }
}



@Preview
@Composable
fun DefaultPreviewOfSignUpScreen(){
    SignUpScreen(onSignedIn = {}, onViewTerms = {})
}