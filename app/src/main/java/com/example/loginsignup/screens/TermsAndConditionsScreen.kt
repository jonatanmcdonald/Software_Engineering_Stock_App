package com.example.loginsignup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.loginsignup.R
import com.example.loginsignup.components.HeadingTextComponent


@Composable
fun TermsAndConditionsScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(16.dp),

    ) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
            
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            HeadingTextComponent(value = stringResource(id = R.string.TermsAndConditionPage))

            Button(onClick = {
                navController.navigate("SignUpScreen")
            })
            {
                Text(text = "Go to SignUpScreen", fontSize = 20.sp)


            }
        }
    }
}
/*
    Button(onClick = {
        navController.navigate("SignUpScreen")
    }) {
        Text(text = "Go to SignUpScreen", fontSize = 20.sp)
    }
*/



@Preview
@Composable
fun TermsAndConditionsScreenPreview(){
    TermsAndConditionsScreen(navController = rememberNavController())
}