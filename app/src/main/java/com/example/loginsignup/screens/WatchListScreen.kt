package com.example.loginsignup.screens


import com.example.loginsignup.data.db.entity.WatchList
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.components.AddWatchlistItemDialog
import com.example.loginsignup.viewModels.WatchListViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue

import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import com.example.loginsignup.data.db.entity.Note
import java.util.Locale

data class WatchRow(
    val ticker: String
)

data class WatchUi(
    val id: Long = 0L,
    val name: String = "",
    val noteId: Long? = null,
    val imageUrl: String? = null,
    val content: String? = null,
    val ticker: String = "",
    val price: Double = 0.0,        // latest close (or open if close missing)
    val change: Double? = null,       // price - previous price
    val changePercent: Double? = null,
    val hasAlert: Boolean = false,
    val alertParameter: String = "",
    val alertPrice: Double = 0.0,
    val isUp: Boolean? = false         // null if change unknown
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchListScreen(
    userId: Int,
    wvm: WatchListViewModel = viewModel(),
   // onViewDetails: (String) -> Unit
) {
    LaunchedEffect(userId) { wvm.startWatchlistPriceUpdate(userId) }
    val rows by wvm.watchRows.collectAsState()
   // Log.d("WatchList",rows.toString())
    val scope = rememberCoroutineScope()
    val stockList by wvm.stockList.collectAsState()
    val isLoading by wvm.isLoading.collectAsState()
    val searchQuery by wvm.searchQuery.collectAsState()
    var showDialog by remember { mutableStateOf(false)}
    var editing by remember {mutableStateOf<WatchRow?>(null)}

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(100.dp),
                windowInsets = WindowInsets(0), // bigger than default
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    //containerColor = Color(0xFF1E2746),           // new color
                    containerColor = Color(0xFF0F1115),           // new color
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),               // take full app bar height
                        contentAlignment = Alignment.Center // center vertically + horizontally
                    ) {
                        Text("My Watchlist", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color.Blue,
                contentColor = Color.White,
                onClick = {
                    wvm.onSearchQueryChanged("")
                    showDialog = true
                }
            ) { Icon(Icons.Default.Add, contentDescription = "Add") }
        },

    ) { inner ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (rows.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No items yet.\nTap + to add your first stock.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rows, key = { it.id }) { item ->
                        WatchCard(
                            symbol = item.ticker,
                            isUp = item.isUp,
                            price = item.price,
                            change = item.change,
                            changePercent = item.changePercent,
                            noteContent = item.content,
                            noteImageUrl = item.imageUrl,
                            onDelete = { wvm.delete(item.id) },
                            hasAlert = item.hasAlert,
                            alertParameter = item.alertParameter,
                            alertPrice = item.alertPrice,
                            onUpsertNote ={ content ->
                                scope.launch {
                                    wvm.upsertNoteContent(
                                        Note(
                                            content = content,
                                            watchlistId = item.id,
                                            imageUrl = item.imageUrl,
                                            timestamp = System.currentTimeMillis()
                                        )
                                    )
                                }
                            },
                            onUpsertAlert = {string, double ->},

                        )

                    }
                }
            }
        }

        if (showDialog) {
            val initial = editing
            AddWatchlistItemDialog(
                onDismissRequest = { showDialog = false },

                // Prefill when editing
                initialStock = initial?.ticker,
                confirmLabel = if (initial == null) "Add" else "Update",

                stockList = stockList,
                isLoading = isLoading,
                searchQuery = searchQuery,
                onQueryChange = wvm::onSearchQueryChanged,
                onStockSelected = wvm::onStockSelected,
                onSave = { ui ->
                    // Persist: map UI -> entity
                    scope.launch {
                        wvm.upsertByUserAndStock(
                            WatchList(
                                userId = userId,
                                stockId = wvm.getStockId(ui)// save stock
                            )
                        )
                    }
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchCard(
    symbol: String,
    price: Double?,
    change: Double?,
    changePercent: Double?,
    isUp: Boolean?,
    noteID: Long? = null,
    noteContent: String? = null,
    noteImageUrl: String? = null,
    onDelete: () -> Unit,
    hasAlert: Boolean = false,          // from DB: does this watch have an alert
    alertParameter: String = "",        // e.g. "Less Than"
    alertPrice: Double = 0.0,           // alert threshold
    alertActive: Boolean = true,       // alert is active
    onUpsertNote: (String) -> Unit,
    onUpsertAlert: (String, Double) -> Unit,   // persist alert
    onToggleAlertActive: (Boolean) -> Unit = {} //  toggle alert active
) {
    val alertOptions = listOf("Less Than", "Greater Than", "Equal To")

    // ===== NOTE STATE =====
    var showNoteEditor by remember { mutableStateOf(false) }
    var editableNoteText by remember(noteContent) {
        mutableStateOf(noteContent.orEmpty())
    }

    // ===== ALERT STATE =====
    var showAlertEditor by remember { mutableStateOf(false) }
    var alertMenuExpanded by remember { mutableStateOf(false) }

    var editableAlertParameter by remember(alertParameter) {
        mutableStateOf(alertParameter.ifBlank { alertOptions.first() })
    }

    // keep price as text while editing to avoid crashes on invalid input
    var editableAlertValueText by remember(alertPrice) {
        mutableStateOf(
            if (alertPrice == 0.0) "" else alertPrice.toString()
        )
    }
    var alertValueError by remember { mutableStateOf<String?>(null) }

    val color = when (isUp) {
        true  -> Color(0xFF16A34A) // green
        false -> Color(0xFFDC2626) // red
        null  -> Color(0xFF9AA4B2) // neutral
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF171A21)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            // ===== HEADER: SYMBOL + PRICE =====
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        symbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF9AA4B2)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = price?.let { String.format(Locale.getDefault(), "%.2f", it) } ?: "--",
                        style = MaterialTheme.typography.titleLarge,
                        color = color
                    )
                    if (change != null) {
                        Text(
                            text = "(" + String.format(Locale.getDefault(), "%.2f", change) + ")",
                            style = MaterialTheme.typography.bodyMedium,
                            color = color
                        )
                    }
                    if (changePercent != null) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f%%", changePercent),
                            style = MaterialTheme.typography.bodySmall,
                            color = color
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF8C8C)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            // ===== NOTE EDITOR (BOTTOM) =====
            if (showNoteEditor) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = editableNoteText,
                    onValueChange = { editableNoteText = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 5
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { showNoteEditor = false }) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            showNoteEditor = false
                            onUpsertNote(editableNoteText.trim())
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF171A21),
                            contentColor = Color(0xFFB3C5FF)
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
            // ===== NOTE DISPLAY / ADD / EDIT =====
            if (!noteContent.isNullOrBlank() && !showNoteEditor) {
                Text(
                    text = "Note",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDEE4EA)
                )
                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1F2430), shape = MaterialTheme.shapes.medium)
                        .padding(10.dp)
                ) {
                    Text(
                        text = noteContent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFDEE4EA)
                    )
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = {
                    editableNoteText = noteContent
                    showNoteEditor = true
                }) {
                    Text("Edit", color = Color(0xFFB3C5FF))
                }
            } else if (noteContent.isNullOrBlank() && !showNoteEditor) {
                Text(
                    text = "No note yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDEE4EA),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(4.dp))

                TextButton(onClick = {
                    editableNoteText = ""
                    showNoteEditor = true
                }, modifier = Modifier.align(Alignment.End)) {
                    Text("Add Note", color = Color(0xFFB3C5FF))
                }
            }

            // ===== ALERT DISPLAY / ADD / EDIT BUTTON =====
            Spacer(Modifier.height(8.dp))
            // ===== ALERT EDITOR =====
            if (showAlertEditor) {
                Spacer(Modifier.height(8.dp))

                Text(
                    "Price Alert",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDEE4EA)
                )
                Spacer(Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = alertMenuExpanded,
                    onExpandedChange = { alertMenuExpanded = !alertMenuExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editableAlertParameter,
                        onValueChange = { /* read-only, controlled by dropdown */ },
                        label = { Text("Condition") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = alertMenuExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = alertMenuExpanded,
                        onDismissRequest = { alertMenuExpanded = false },
                        modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = true)
                    ) {
                        alertOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    editableAlertParameter = option
                                    alertMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = editableAlertValueText,
                    onValueChange = { text ->
                        editableAlertValueText = text
                        alertValueError = null
                    },
                    label = { Text("Alert Price") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = alertValueError != null
                )

                if (alertValueError != null) {
                    Text(
                        text = alertValueError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        showAlertEditor = false
                        alertValueError = null
                    }) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val parsed = editableAlertValueText.toDoubleOrNull()
                            if (parsed == null) {
                                alertValueError = "Enter a valid number"
                            } else {
                                showAlertEditor = false
                                alertValueError = null
                                onUpsertAlert(editableAlertParameter, parsed)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF171A21),
                            contentColor = Color(0xFFB3C5FF)
                        )
                    ) {
                        Text("Save")
                    }
                }
            }

            if (!hasAlert && !showAlertEditor) {
                Text(
                    text = "No alert yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDEE4EA),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                TextButton(onClick = { showAlertEditor = true }, modifier = Modifier.align(Alignment.End)) {
                    Text("Add Alert", color = Color(0xFFB3C5FF))

                }
            } else if (hasAlert && !showAlertEditor) {
                Text(
                    text = "Alert: $alertParameter $alertPrice",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFB3C5FF)
                )
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = { showAlertEditor = true }) {
                    Text("Edit Alert", color = Color(0xFFB3C5FF))
                }
            }




        }
    }
}




