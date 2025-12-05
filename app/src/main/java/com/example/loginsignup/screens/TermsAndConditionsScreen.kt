package com.example.loginsignup.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginsignup.R
import com.example.loginsignup.components.HeadingTextComponent


@Composable
fun TermsAndConditionsScreen(onBack: () -> Unit) { // A composable function for the Terms and Conditions screen.
    Surface(
        modifier = Modifier
            .fillMaxSize() // Fills the maximum available size.
            .background(MaterialTheme.colorScheme.background) // Sets the background color.
            .padding(28.dp) // Adds padding.

    ) {
        Column(modifier = Modifier.fillMaxSize() // A vertically arranged layout that fills the maximum size.
            .background(MaterialTheme.colorScheme.background), // Sets the background color.
            verticalArrangement = Arrangement.spacedBy(12.dp) // Adds spacing between the children.
            
        ) {
            Spacer(modifier = Modifier.height(20.dp)) // Adds vertical space.

            HeadingTextComponent(value = stringResource(id = R.string.TermsAndConditionPage)) // Displays a heading text component with the terms and conditions string.

            Button(onClick = { // A button to navigate back to the previous screen.
                onBack()
            }, modifier = Modifier
                .fillMaxWidth() // Fills the maximum available width.
                .height(56.dp), // Sets the height of the button.
                shape = RectangleShape, // Sets the shape of the button.
                colors = ButtonDefaults.buttonColors( // Sets the colors of the button.
                    containerColor = Color(0xFF00E0C7),
                    contentColor = Color.Black
                ), contentPadding = PaddingValues(vertical = 12.dp) // Adds padding to the content of the button.
            ){
                Text(text = "Go to SignUpScreen", fontSize = 20.sp) // The text to display on the button.


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
    TermsAndConditionsScreen(onBack = {})
}