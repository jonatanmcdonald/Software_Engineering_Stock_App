package com.example.loginsignup.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.example.loginsignup.R
import com.example.loginsignup.data.models.Stocks
import com.example.loginsignup.navigation.MainDest
import com.example.loginsignup.screens.WatchListUi

//welcome message
@Composable
fun NormalTextComponent(value: String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        )
    ,   color = Color.Black,
        textAlign = TextAlign.Center
    )
}

//create account
@Composable
fun HeadingTextComponent(value: String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        )
        ,   color = Color.Black,
        textAlign = TextAlign.Center
    )
}


/*
//for textfield inputs
@Composable
fun MyTextField(labelValue: String, painterResource: Painter) {

    val textValue = remember {
        mutableStateOf("")

    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = labelValue) },
        value = textValue.value,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Green,
            focusedLabelColor = Color.Green,
            cursorColor = Color.Green,
            unfocusedContainerColor = Color.LightGray

        ),
        keyboardOptions = KeyboardOptions.Default,
        onValueChange = {
            textValue.value = it
        },
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "user_name_icon")
        }
    )
}*/

@Composable
fun MyTextField(labelValue: String,
                painterResource: Painter,
                textValue: String,
                onValueChange: (String) -> Unit)
{


    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = labelValue) },
        value = textValue,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Green,
            focusedLabelColor = Color.Green,
            cursorColor = Color.Green,
            unfocusedContainerColor = Color.LightGray

        ),
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "user_name_icon")
        }
    )
}

/*
@Composable
fun PasswordTextFieldComponent(labelValue: String, painterResource: Painter) {
    val password = remember {
        mutableStateOf("")
    }

    val passwordVisible = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = labelValue) },
        value = password.value,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Green,
            focusedLabelColor = Color.Green,
            cursorColor = Color.Green,
            unfocusedContainerColor = Color.LightGray

        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = {
            password.value = it
        },
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "user_name_icon")
        },

        trailingIcon = {

            val iconImage = if(passwordVisible.value){
                Icons.Filled.Visibility
            } else{
                Icons.Filled.VisibilityOff
            }

            val description = if(passwordVisible.value){
                stringResource(id = R.string.hide_password)
            } else{
                stringResource(id = R.string.show_password)
            }

            IconButton(onClick = {passwordVisible.value = !passwordVisible.value}) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },

        visualTransformation = if(passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation()
    )
}*/


@Composable
fun PasswordTextFieldComponent(labelValue: String,
                               painterResource: Painter,
                               password: String,
                               onPasswordChange: (String) -> Unit)
{

    val passwordVisible = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = labelValue) },
        value = password,
        onValueChange = onPasswordChange,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Green,
            focusedLabelColor = Color.Green,
            cursorColor = Color.Green,
            unfocusedContainerColor = Color.LightGray

        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "user_name_icon")
        },

        trailingIcon = {

            val iconImage = if(passwordVisible.value){
                Icons.Filled.Visibility
            } else{
                Icons.Filled.VisibilityOff
            }

            val description = if(passwordVisible.value){
                stringResource(id = R.string.hide_password)
            } else{
                stringResource(id = R.string.show_password)
            }

            IconButton(onClick = {passwordVisible.value = !passwordVisible.value}) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },

        visualTransformation = if(passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun AppBottomBar(
    tabs: List<String>,
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {

    val scheme = MaterialTheme.colorScheme

    NavigationBar(

        containerColor = Color.Black,            // bar background
        contentColor = scheme.onSurfaceVariant,     // default content tint
        tonalElevation = 6.dp,                      // subtle elevation
        modifier = Modifier

            //.shadow(8.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            //.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            //.border(1.dp, scheme.outlineVariant, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        tabs.forEach { route ->
            val selected = currentRoute == route

            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(route) },
                icon = {
                    // Example with badges + different icons for selected state
                    BadgedBox(
                        badge = {
                            if (route == MainDest.WATCHLIST /* && unreadCount > 0 */) {
                                Badge { Text("0") } // or empty Badge() dot
                            }
                        }
                    ) {
                        if (route == MainDest.HOME) {
                            Icon(
                                imageVector = if (selected) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        } else if (route == MainDest.WATCHLIST) {
                            Icon(
                                imageVector = if (selected) Icons.Filled.Preview else Icons.Outlined.Preview,
                                contentDescription = "Watchlist"
                            )
                        } else {
                            Icon(
                                imageVector = if (selected) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        }
                    }
                },
                label = {
                    Text(
                        when (route) {
                            MainDest.HOME -> "Home"
                            MainDest.WATCHLIST -> "Watchlist"
                            else -> "Profile"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                // In M3 labels show by default; you can force-hide on unselected:
                alwaysShowLabel = false,

                colors = NavigationBarItemDefaults.colors(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWatchlistItemDialog(
    stockList: List<Stocks>,
    searchQuery: String,
    onDismissRequest: () -> Unit,
    onSave: (WatchListUi) -> Unit,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onStockSelected: (Stocks) -> Unit,
) {

    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedStock by remember { mutableStateOf<Stocks?>(null) }

    val isDropdownExpanded = stockList.isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Add to Watchlist") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Display Name (e.g., Apple Inc.)") }, singleLine = true
                )
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
                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    label = { Text("Note (optional)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && note.isNotBlank(),
                onClick = {
                    selectedStock?.let {
                        val newItem = WatchListUi(name, it, note)
                        onSave(newItem)
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    )
}




