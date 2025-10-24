package com.example.loginsignup.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.loginsignup.screens.WatchRow

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
                        when (route) {
                            MainDest.HOME -> {
                                Icon(
                                    imageVector = if (selected) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            }
                            MainDest.WATCHLIST -> {
                                Icon(
                                    imageVector = if (selected) Icons.Filled.Preview else Icons.Outlined.Preview,
                                    contentDescription = "Watchlist"
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = if (selected) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            }
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
    stockList: List<Stock>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onStockSelected: (Stock) -> Unit,
    initialName: String? = null,
    initialNote: String? = null,
    initialStock: String? = null,
    confirmLabel: String,
    isLoading: Boolean,
    onSave: (WatchRow) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(initialName.orEmpty()) }
    var note by rememberSaveable { mutableStateOf(initialNote.orEmpty()) }

    val scroll = rememberScrollState()

    LaunchedEffect(stockList.size, stockList.firstOrNull()?.ticker, searchQuery) {
        if (stockList.isNotEmpty()) scroll.scrollTo(0)
    }
    val maxMenuHeight = 200.dp

    // NEW: local expansion + focus gating
    var expanded by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }

    // Open only when focused and we have results
    LaunchedEffect(stockList, hasFocus) {
        expanded = hasFocus && stockList.isNotEmpty()
    }

    LaunchedEffect(initialStock) {
        if (!initialStock.isNullOrBlank() && searchQuery.isBlank()) {
            onQueryChange(initialStock)
        }
    }

    val canConfirm = searchQuery.isNotBlank() && name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(if (initialName == null) "Add to Watchlist" else "Edit Watchlist Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { wantOpen ->
                        // allow manual toggle, but still require focus + results
                        expanded = wantOpen && hasFocus && stockList.isNotEmpty()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { onQueryChange(it) },
                        label = { Text("Search symbol or name") },
                        singleLine = true,
                        enabled = true, // <-- keep editable even while loading
                        trailingIcon = {
                            if (isLoading) {
                                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .onFocusChanged { hasFocus = it.isFocused }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }, // <-- actually close
                        modifier = Modifier
                            .exposedDropdownSize(matchTextFieldWidth = true)
                            .heightIn(max = maxMenuHeight)
                            .verticalScroll(scroll)
                            .zIndex(1f)
                    ) {
                        stockList.forEach { stock ->
                            DropdownMenuItem(
                                text = { Text("${stock.name} (${stock.ticker})") },
                                onClick = {
                                    onStockSelected(stock)
                                    onQueryChange(stock.ticker)
                                    expanded = false // close after pick
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = canConfirm && !isLoading,
                onClick = {
                    onSave(
                        WatchRow(
                            name = name,
                            note = note,
                            ticker = searchQuery.trim()
                        )
                    )
                }
            ) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    )
}





