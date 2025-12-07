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

private val MaxMenuHeight = 320.dp // Defines the maximum height for the dropdown menu.

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for using experimental Material 3 APIs.
@Composable // Marks this function as a composable, allowing it to be used in UI.
fun SearchScreen(
    svm: SearchViewModel = viewModel(), // Injects the SearchViewModel.
    onNavigateToDetails: (String) -> Unit, // Callback function to navigate to the details screen.
    onBack: () -> Unit // Callback function to navigate back.
) {
    val stockList by svm.stockList.collectAsState() // Collects the list of stocks from the ViewModel as state.
    val isLoading by svm.isLoading.collectAsState() // Collects the loading state from the ViewModel.
    val searchQuery by svm.searchQuery.collectAsState() // Collects the search query from the ViewModel.

    var expanded by remember { mutableStateOf(false) } // State to manage the expansion of the dropdown menu.
    var hasFocus by remember { mutableStateOf(false) } // State to track if the search field has focus.

    // This effect runs when stockList or hasFocus changes.
    // It controls the visibility of the dropdown menu.
    LaunchedEffect(stockList, hasFocus) {
        expanded = hasFocus && stockList.isNotEmpty()
    }

    Column { // A vertical layout for the screen content.
        ExposedDropdownMenuBox( // A box that provides a dropdown menu.
            expanded = expanded, // Controls whether the menu is expanded.
            onExpandedChange = { wantOpen -> // Callback for when the expansion state changes.
                // Only allow opening the menu if there are results and the field has focus.
                expanded = wantOpen &&  hasFocus && stockList.isNotEmpty()
            }
        ) {
            OutlinedTextField( // A text field with an outline.
                value = searchQuery, // The current value of the search query.
                onValueChange = { svm.onSearchQueryChanged(it) }, // Callback when the search query changes.
                label = { Text("Search symbol or name") }, // The label for the text field.
                singleLine = true, // Restricts the input to a single line.
                trailingIcon = { // An icon displayed at the end of the text field.
                    if (isLoading) { // If data is loading, show a progress indicator.
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else { // Otherwise, show a search icon.
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth() // Makes the text field fill the available width.
                    .menuAnchor() // Anchors the dropdown menu to this text field.
                    .onFocusChanged { hasFocus = it.isFocused } // Updates the focus state.
            )

            ExposedDropdownMenu( // The dropdown menu itself.
                expanded = expanded, // Controls whether the menu is visible.
                onDismissRequest = { expanded = false }, // Callback when the menu is dismissed.
                modifier = Modifier
                    .exposedDropdownSize(matchTextFieldWidth = true) // Matches the width of the menu to the text field.
                    .heightIn(max = MaxMenuHeight) // Constrains the height of the menu.
            ) {
                stockList.forEach { stock -> // Iterates over the list of stocks.
                    DropdownMenuItem( // An item in the dropdown menu.
                        text = { Text("${stock.name} (${stock.ticker})") }, // The text to display for the stock.
                        onClick = { // Callback when the item is clicked.
                            onNavigateToDetails(stock.ticker) // Navigates to the details screen for the selected stock.
                            svm.onStockSelected(stock) // Notifies the ViewModel that a stock has been selected.
                            expanded = false // Closes the dropdown menu.
                        }
                    )
                }
            }
        }
    }
}
