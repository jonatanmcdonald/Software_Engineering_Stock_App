// This file is entirely commented out and not currently in use.
/*
package com.example.loginsignup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


data class Stock(val name: String, val symbol: String)
data class WatchListItem(val name: String, val stock: Stock, val note: String)

// --- Main Screen --
@Composable
fun WatchListScreen(watchListViewModel: WatchListViewModel = viewModel() ) {

    // State for the list of items displayed on the screen
    val watchListItems = remember { mutableStateListOf<WatchListItem>() }

    val stockList by watchListViewModel.stockList.collectAsState()
    val isLoading by watchListViewModel.isLoading.collectAsState()
    val searchQuery by watchListViewModel.searchQuery.collectAsState()


    // --- SIMPLIFIED DIALOG STATE ---
    // A single boolean to control the dialog's visibility
    var showDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(WindowInsets.systemBars.asPaddingValues()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Button to add a new dialog
            Button(
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                onClick = {
                    // Just set the boolean to true to show the dialog
                    showDialog = true
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(text = "Add New Watchlist Item")
            }

            Spacer(modifier = Modifier.height(16.dp))
            // --- Display Watchlist Items ---
            if (watchListItems.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Items in Watchlist",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(watchListItems) { item ->
                        ListBox(item = item)
                    }
                }
            }
        }

        // --- Dialog Management ---
        // If showDialog is true, compose the dialog
        if (showDialog) {
            AddWatchlistItemDialog(
                stockList = stockList,
                onDismissRequest = { showDialog = false }, // On cancel, set to false
                searchQuery = searchQuery,
                isLoading = isLoading,
                onQueryChange = watchListViewModel::onSearchQueryChanged,
                onStockSelected = watchListViewModel::onStockSelected,
                onSave = { newItem ->
                    watchListItems.add(newItem) // Save the data
                    showDialog = false          // Close the dialog

                }
            )
        }
    }
}
// --- Dialog Composable with Dropdown ---
@OptIn(ExperimentalMaterial3Api::class) // Required for ExposedDropdownMenuBox
@Composable
fun AddWatchlistItemDialog(
    stockList: List<Stock>,
    searchQuery: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onStockSelected: (Stock) -> Unit,
    onDismissRequest: () -> Unit,
    onSave: (WatchListItem) -> Unit
) {
    // State for the text fields within the dialog
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    // --- State for the Dropdown Menu ---
    val isDropdownExpanded = stockList.isNotEmpty()

    var selectedStock by remember { mutableStateOf<Stock?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth().background(Color.Gray),
            elevation = CardDefaults.cardElevation(8.dp),

            ) {
            Column(
                modifier = Modifier.padding(16.dp).background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text("Add to Watchlist", fontSize = 20.sp, modifier = Modifier.padding(top = 5.dp))
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // --- Exposed Dropdown Menu for Stock Selection ---
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            selectedStock = null
                            onQueryChange(it)
                        },

                        label = { Text("Search Stock") },
                        trailingIcon = {
                            if (isLoading) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.width(24.dp)
                                )
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                            }
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = {}
                    ) {
                        stockList.forEach { stock ->
                            DropdownMenuItem(
                                text = { Text("${stock.name} (${stock.symbol})") },
                                onClick = {
                                    selectedStock = stock
                                    onStockSelected(stock)
                                }
                            )
                        }
                    }
                }
                // --- End of Dropdown ---

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        enabled = selectedStock != null && name.isNotBlank(),
                        onClick = {
                            selectedStock?.let{
                                val newItem = WatchListItem(name, it, note)
                                onSave(newItem)
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}


// --- List Item Composable ---
@Composable
fun ListBox(item: WatchListItem) {
    // Card for better UI distinction
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Gray)
        ,
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = item.name, fontSize = 30.sp, color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${item.stock.name} (${item.stock.symbol})", fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Note: ${item.note}", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Thin, fontFamily = FontFamily.Serif)
        }
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun WatchListPreview() {
    WatchListScreen()
}*/