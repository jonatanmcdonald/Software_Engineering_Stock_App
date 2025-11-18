package com.example.loginsignup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.viewModels.PortfolioViewModel

@Composable
fun TransactionScreen(
    userId: Int,
    pvm: PortfolioViewModel = viewModel()
) {
    val transactions by pvm.getTransForUser(userId).collectAsState(initial = emptyList())

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
            items(transactions) { tx ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp),
                    color = if (tx.side == "SELL") Color(0xFFEF5350) else Color(0xFF66BB6A),
                    shadowElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Date: ${
                            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
                                .format(tx.timestamp)
                        }", fontSize = 12.sp)
                        Text("Symbol: ${tx.symbol} | Side : ${tx.side}", fontSize = 16.sp)
                        Text("Qty: ${tx.qty} | Price: $${"%.2f".format(tx.price)}", fontSize = 14.sp)
                    }
                }
            }
        }
    }

}



@Preview
@Composable
fun DefaultPreviewOfTransactionRecords() {
    // replace with your actual theme composable
    TransactionScreen(
        userId = 0
    )

}