package com.example.loginsignup.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.viewModels.SearchViewModel

private val MaxMenuHeight = 320.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    svm: SearchViewModel = viewModel(),
    onNavigateToDetails: (String) -> Unit,
    onBack: () -> Unit
) {
    val stockList by svm.stockList.collectAsState()
    val isLoading by svm.isLoading.collectAsState()
    val searchQuery by svm.searchQuery.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }

    // Open/close menu based on focus + results
    LaunchedEffect(stockList, hasFocus) {
        expanded = hasFocus && stockList.isNotEmpty()
    }

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { wantOpen ->
                // Only allow opening if we have results
                expanded = wantOpen &&  hasFocus && stockList.isNotEmpty()
            }
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { svm.onSearchQueryChanged(it) }, // VM drives search
                label = { Text("Search symbol or name") },
                singleLine = true,
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor() // IMPORTANT: inside the Box
                    .onFocusChanged { hasFocus = it.isFocused }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .exposedDropdownSize(matchTextFieldWidth = true)
                    .heightIn(max = MaxMenuHeight)
            ) {
                stockList.forEach { stock ->
                    DropdownMenuItem(
                        text = { Text("${stock.name} (${stock.ticker})") },
                        onClick = {
                            onNavigateToDetails(stock.ticker)
                            svm.onStockSelected(stock) // sets text; VM can suppress extra search
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
