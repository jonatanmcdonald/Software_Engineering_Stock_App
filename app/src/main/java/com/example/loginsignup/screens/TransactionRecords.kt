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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.loginsignup.components.NormalTextComponent
import com.example.loginsignup.viewModels.UserViewModel

@Composable
fun TransactionRecords() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Transaction History",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Scrollable list container (empty for now)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Placeholder: empty items to show the layout
            items(5) { index ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shadowElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Date", fontSize = 12.sp)
                        Text(text = "Description", fontSize = 16.sp)
                        Text(text = "Amount", fontSize = 14.sp)
                    }
                }
            }
        }
    }
/*
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text="Transaction Records",
                color = Color.Yellow,
                fontSize = 24.sp

            )
            Spacer(modifier = Modifier.height(20.dp))


            Button(
                onClick = {

                },modifier = Modifier
                    .fillMaxWidth()      // full width
                    .height(56.dp),      // large height
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E0C7), // teal accent
                    contentColor = Color.Black          // text color
                ), contentPadding = PaddingValues(vertical = 12.dp)
            ){
                Text(text = "Transaction History", fontSize = 18.sp)
            }
        }
    }
*/
}



@Preview
@Composable
fun DefaultPreviewOfTransactionRecords() {
    // replace with your actual theme composable
    TransactionRecords()

}