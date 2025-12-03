package com.example.loginsignup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.viewModels.PortfolioViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TransactionScreen(
    userId: Int,
    pvm: PortfolioViewModel = viewModel()
) {
    val transactions by pvm.getTransForUser(userId).collectAsState(initial = emptyList())

    //Search Query State
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val filteredTransactions = remember(transactions, searchQuery)
    {
        if (searchQuery.isBlank()) {
            transactions
        } else {
            val q = searchQuery.trim().lowercase()
            transactions.filter { tx ->
                val symbolMatch = tx.symbol.lowercase().contains(q)
                val dateString = dateFormatter.format(tx.timestamp)
                val dateMatch = dateString.lowercase().contains(q)

                symbolMatch || dateMatch
            }
        }
    }


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

        //Search Field
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            singleLine = true,
            placeholder = { Text("Search by symbol or date (e.g. AAPL, 2025-01-03") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // Scrollable list container (empty for now)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Placeholder: empty items to show the layout
            items(filteredTransactions) { tx ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp),
                    color = if (tx.side == "SELL") Color(0xFFEF5350) else Color(0xFF66BB6A),
                    shadowElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        val dateString = dateFormatter.format(tx.timestamp)
                        Text(text = "Date: $dateString", fontSize = 12.sp)
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