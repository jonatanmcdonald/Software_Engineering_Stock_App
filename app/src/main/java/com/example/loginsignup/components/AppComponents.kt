package com.example.loginsignup.components

//Imports
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.loginsignup.R
import com.example.loginsignup.data.db.entity.Stock
import com.example.loginsignup.navigation.MainDest

/**
 * A composable function for displaying normal text, typically used for body content.
 *
 * @param value The text to be displayed.
 */
@Composable
fun NormalTextComponent(value: String){
    Text(
        text = value, // The text to be displayed.
        modifier = Modifier
            .fillMaxWidth() // The text will fill the entire width of its container.
            .heightIn(min = 40.dp), // The text will have a minimum height of 40dp.
        style = TextStyle( // The style of the text.
            fontSize = 24.sp, // The font size of the text.
            fontWeight = FontWeight.Normal, // The font weight of the text.
            fontStyle = FontStyle.Normal // The font style of the text.
        )
    ,   color = Color.White, // The color of the text.
        textAlign = TextAlign.Center // The alignment of the text.
    )
}

/**
 * A composable function for displaying heading text, typically used for screen titles.
 *
 * @param value The text to be displayed.
 */
@Composable
fun HeadingTextComponent(value: String){
    Text(
        text = value, // The text to be displayed.
        modifier = Modifier
            .fillMaxWidth() // The text will fill the entire width of its container.
            .heightIn(), // The text will have a minimum height based on its content.
        style = TextStyle( // The style of the text.
            fontSize = 30.sp, // The font size of the text.
            fontWeight = FontWeight.Bold, // The font weight of the text.
            fontStyle = FontStyle.Normal // The font style of the text.
        )
        ,   color = Color.White, // The color of the text.
        textAlign = TextAlign.Center // The alignment of the text.
    )
}


/**
 * A composable function for a custom text field with a leading icon.
 *
 * @param labelValue The label to be displayed for the text field.
 * @param painterResource The painter resource for the leading icon.
 * @param textValue The current value of the text field.
 * @param onValueChange A callback to be invoked when the value of the text field changes.
 * @param modifier A modifier to be applied to the text field.
 */
@Composable
fun MyTextField(labelValue: String,
                painterResource: Painter,
                textValue: String,
                onValueChange: (String) -> Unit,
                modifier: Modifier = Modifier
)
{


    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(), // The text field will fill the entire width of its container.
        label = { Text(text = labelValue) }, // The label for the text field.
        value = textValue, // The current value of the text field.
        onValueChange = onValueChange, // A callback to be invoked when the value of the text field changes.
        colors = TextFieldDefaults.colors( // The colors for the text field.
            focusedContainerColor = Color(0xFF1A1D23),
            unfocusedContainerColor =  Color(0xFF1A1D23),
            focusedIndicatorColor =  Color(0xFF00E0C7),
            unfocusedIndicatorColor = Color(0xFF2B2F36),
            focusedTextColor = Color(0xFFFFFFFF),
            unfocusedTextColor = Color(0xFFFFFFFF),
            cursorColor = Color(0xFF00E0C7),
            focusedLabelColor = Color(0xFF00E0C7),
            unfocusedLabelColor = Color(0xFF9CA3AF)

        ),

        leadingIcon = { // The leading icon for the text field.
            Icon(painter = painterResource, contentDescription = "user_name_icon")
        }
    )
}



/**
 * A composable function for a custom password text field with a leading icon and a trailing icon to toggle password visibility.
 *
 * @param labelValue The label to be displayed for the password text field.
 * @param painterResource The painter resource for the leading icon.
 * @param password The current value of the password text field.
 * @param onPasswordChange A callback to be invoked when the value of the password text field changes.
 */
@Composable
fun PasswordTextFieldComponent(labelValue: String,
                               painterResource: Painter,
                               password: String,
                               onPasswordChange: (String) -> Unit)
{

    val passwordVisible = remember { // A state to track the visibility of the password.
        mutableStateOf(false)
    }

    OutlinedTextField(

        modifier = Modifier.fillMaxWidth(), // The password text field will fill the entire width of its container.
        label = { Text(text = labelValue) }, // The label for the password text field.
        value = password, // The current value of the password text field.
        onValueChange = onPasswordChange, // A callback to be invoked when the value of the password text field changes.
        colors = TextFieldDefaults.colors( // The colors for the password text field.
            focusedContainerColor = Color(0xFF1A1D23),
            unfocusedContainerColor =  Color(0xFF1A1D23),
            focusedIndicatorColor =  Color(0xFF00E0C7),
            unfocusedIndicatorColor = Color(0xFF2B2F36),
            focusedTextColor = Color(0xFFFFFFFF),
            unfocusedTextColor = Color(0xFFFFFFFF),
            cursorColor = Color(0xFF00E0C7),
            focusedLabelColor = Color(0xFF00E0C7),
            unfocusedLabelColor = Color(0xFF9CA3AF)

        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // The keyboard options for the password text field.
        leadingIcon = { // The leading icon for the password text field.
            Icon(painter = painterResource, contentDescription = "user_name_icon")
        },

        trailingIcon = { // The trailing icon for the password text field.

            val iconImage = if(passwordVisible.value){ // The icon to be displayed based on the password visibility.
                Icons.Filled.Visibility
            } else{
                Icons.Filled.VisibilityOff
            }

            val description = if(passwordVisible.value){ // The content description for the icon.
                stringResource(id = R.string.hide_password)
            } else{
                stringResource(id = R.string.show_password)
            }

            IconButton(onClick = {passwordVisible.value = !passwordVisible.value}) { // An icon button to toggle the password visibility.
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },

        visualTransformation = if(passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation() // The visual transformation for the password text field.
    )
}

/**
 * A composable function for the bottom navigation bar of the app.
 *
 * @param tabs The list of routes for the tabs in the bottom navigation bar.
 * @param currentRoute The current route of the app.
 * @param onTabSelected A callback to be invoked when a tab is selected.
 */
@Composable
fun AppBottomBar(
    tabs: List<String>,
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {

    val scheme = MaterialTheme.colorScheme // The color scheme of the app.

    NavigationBar(

        containerColor = Color.Black,            // bar background
        contentColor = scheme.onSurfaceVariant,     // default content tint
        tonalElevation = 6.dp,                      // subtle elevation
        modifier = Modifier

            //.shadow(8.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            //.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            //.border(1.dp, scheme.outlineVariant, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        tabs.forEach { route -> // Iterates over the list of tabs.
            val selected = currentRoute == route // Checks if the current tab is selected.

            NavigationBarItem(
                selected = selected, // Whether the tab is selected.
                onClick = { onTabSelected(route) }, // A callback to be invoked when the tab is selected.
                icon = { // The icon for the tab.
                    // Example with badges + different icons for selected state
                    BadgedBox(
                        badge = { // A badge to be displayed on the icon.
                            if (route == MainDest.WATCHLIST /* && unreadCount > 0 */) {
                               // Badge { Text("0") } // or empty Badge() dot
                            }
                        }
                    ) {
                        when (route) { // A when statement to select the icon based on the route.
                            MainDest.HOME -> { // The home screen.
                                Icon(
                                    imageVector = if (selected) Icons.Filled.Home else Icons.Outlined.Home, // The icon to be displayed.
                                    contentDescription = "Home" // The content description for the icon.
                                )
                            }
                            MainDest.WATCHLIST -> { // The watchlist screen.
                                Icon(
                                    imageVector = if (selected) Icons.Filled.Preview else Icons.Outlined.Preview, // The icon to be displayed.
                                    contentDescription = "Watchlist" // The content description for the icon.
                                )
                            }
                            MainDest.TRANSACTION -> { // The transaction screen.
                                Icon(
                                    imageVector = if (selected) Icons.Filled.CurrencyExchange else Icons.Outlined.CurrencyExchange, // The icon to be displayed.
                                    contentDescription = "Transaction History" // The content description for the icon.
                                )
                            }
                            else -> { // The news screen.
                                Icon(
                                    imageVector = if (selected) Icons.Filled.Campaign else Icons.Outlined.Campaign, // The icon to be displayed.
                                    contentDescription = "News" // The content description for the icon.
                                )
                            }
                        }
                    }
                },
                label = { // The label for the tab.
                    Text(
                        when (route) { // A when statement to select the label based on the route.
                            MainDest.HOME -> "Home"
                            MainDest.WATCHLIST -> "Watchlist"
                            MainDest.TRANSACTION -> "Transaction History"
                            else -> "News"
                        },
                        style = MaterialTheme.typography.labelLarge // The style of the label.
                    )
                },
                // In M3 labels show by default; you can force-hide on unselected:
                alwaysShowLabel = false, // Whether to always show the label.

                colors = NavigationBarItemDefaults.colors( // The colors for the tab.
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent// <- use indicatorColor (not selectedIndicatorColor)
                )

            )
        }
    }
}

/**
 * A composable function for a dialog to add a new item to the watchlist.
 *
 * @param stockList The list of stocks to be displayed in the dropdown menu.
 * @param searchQuery The current search query.
 * @param onQueryChange A callback to be invoked when the search query changes.
 * @param onStockSelected A callback to be invoked when a stock is selected.
 * @param initialName The initial name of the watchlist item.
 * @param initialStock The initial stock of the watchlist item.
 * @param confirmLabel The label for the confirm button.
 * @param isLoading Whether the stock list is currently being loaded.
 * @param onSave A callback to be invoked when the save button is clicked.
 * @param onDismissRequest A callback to be invoked when the dialog is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWatchlistItemDialog(
    stockList: List<Stock>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onStockSelected: (Stock) -> Unit,
    initialName: String? = null,
    initialStock: String? = null,
    confirmLabel: String,
    isLoading: Boolean,
    onSave: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {

    val scroll = rememberScrollState() // A state to track the scroll position of the dropdown menu.

    LaunchedEffect(stockList.size, stockList.firstOrNull()?.ticker, searchQuery) { // A LaunchedEffect to scroll to the top of the dropdown menu when the stock list changes.
        if (stockList.isNotEmpty()) scroll.scrollTo(0)
    }
    val maxMenuHeight = 200.dp // The maximum height of the dropdown menu.

    // NEW: local expansion + focus gating
    var expanded by remember { mutableStateOf(false) } // A state to track the expansion of the dropdown menu.
    var hasFocus by remember { mutableStateOf(false) } // A state to track the focus of the search field.

    // Open only when focused and we have results
    LaunchedEffect(stockList, hasFocus) { // A LaunchedEffect to control the visibility of the dropdown menu.
        expanded = hasFocus && stockList.isNotEmpty()
    }

    LaunchedEffect(initialStock) { // A LaunchedEffect to set the initial search query.
        if (!initialStock.isNullOrBlank() && searchQuery.isBlank()) {
            onQueryChange(initialStock)
        }
    }

    var stockSelected by remember { mutableStateOf(false) }

    val canConfirm = searchQuery.isNotBlank() && stockSelected// Whether the confirm button should be enabled.

    AlertDialog(
        onDismissRequest = onDismissRequest, // A callback to be invoked when the dialog is dismissed.
        title = { Text(if (initialName == null) "Add to Watchlist" else "Edit Watchlist Item") }, // The title of the dialog.
        text = { // The content of the dialog.
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { // A column to arrange the content vertically.

                ExposedDropdownMenuBox( // A box that provides a dropdown menu.
                    expanded = expanded, // Whether the dropdown menu is expanded.
                    onExpandedChange = { wantOpen -> // A callback to be invoked when the expansion state changes.
                        // allow manual toggle, but still require focus + results
                        expanded = wantOpen && hasFocus && stockList.isNotEmpty()
                    },
                    modifier = Modifier.fillMaxWidth() // The modifier for the box.
                ) {
                    OutlinedTextField( // A text field for searching stocks.
                        value = searchQuery, // The current value of the search query.
                        onValueChange = { onQueryChange(it) }, // A callback to be invoked when the search query changes.
                        label = { Text("Search symbol or name") }, // The label for the text field.
                        singleLine = true, // Whether the text field should be a single line.
                        enabled = true, // <-- keep editable even while loading
                        trailingIcon = { // The trailing icon for the text field.
                            if (isLoading) { // If the stock list is currently being loaded.
                                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) // A circular progress indicator.
                            } else { // Otherwise.
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) // The default trailing icon for an exposed dropdown menu.
                            }
                        },
                        modifier = Modifier
                            .menuAnchor() // Anchors the dropdown menu to this text field.
                            .fillMaxWidth() // Makes the text field fill the available width.
                            .onFocusChanged { hasFocus = it.isFocused } // Updates the focus state.
                    )

                    ExposedDropdownMenu( // The dropdown menu itself.
                        expanded = expanded, // Whether the dropdown menu is expanded.
                        onDismissRequest = { expanded = false }, // <-- actually close
                        modifier = Modifier
                            .exposedDropdownSize(matchTextFieldWidth = true) // Matches the width of the menu to the text field.
                            .heightIn(max = maxMenuHeight) // Constrains the height of the menu.
                            .verticalScroll(scroll) // Makes the menu scrollable.
                            .zIndex(1f) // Sets the z-index of the menu.
                    ) {
                        stockList.forEach { stock -> // Iterates over the list of stocks.
                            DropdownMenuItem( // An item in the dropdown menu.
                                text = { Text("${stock.name} (${stock.ticker})") }, // The text to display for the stock.
                                onClick = { // A callback to be invoked when the item is clicked.
                                    onStockSelected(stock) // Notifies that a stock has been selected.
                                    //onQueryChange(stock.ticker)
                                    stockSelected = true
                                    expanded = false // Closes the dropdown menu.
                                }
                            )
                        }
                    }
                }

            }
        },
        confirmButton = { // The confirm button for the dialog.
            TextButton(
                enabled = canConfirm && !isLoading, // Whether the confirm button should be enabled.
                onClick = {
                    onSave(searchQuery.trim()) // A callback to be invoked when the save button is clicked.
                }
            ) { Text(confirmLabel) }
        },
        dismissButton = { // The dismiss button for the dialog.
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    )
}





