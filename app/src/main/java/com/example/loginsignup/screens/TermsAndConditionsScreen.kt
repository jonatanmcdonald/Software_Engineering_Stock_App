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
fun TermsAndConditionsScreen(onBack: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp)

    ) {
        Column(modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp)
            
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            HeadingTextComponent(value = stringResource(id = R.string.TermsAndConditionPage))

            Button(onClick = {
                onBack()
            }, modifier = Modifier
                .fillMaxWidth()      // full width
                .height(56.dp),      // large height
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E0C7), // teal accent
                    contentColor = Color.Black          // text color
                ), contentPadding = PaddingValues(vertical = 12.dp)
            ){
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
    TermsAndConditionsScreen(onBack = {})
}