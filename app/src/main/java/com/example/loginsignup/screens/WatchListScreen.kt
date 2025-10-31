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
import androidx.compose.ui.Alignment
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
import java.util.Locale

data class WatchRow(
    val name: String,
    val note: String?,
    val ticker: String
)

data class WatchUi(
    val id: Long = 0L,
    val name: String = "",
    val note: String? = null,
    val ticker: String = "",
    val price: Double = 0.0,        // latest close (or open if close missing)
    val change: Double? = null,       // price - previous price
    val changePercent: Double? = null,
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

                            onDelete = { wvm.delete(item.id) }
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
                initialName = initial?.name,
                initialNote = initial?.note,
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
                                name = ui.name,
                                note = ui.note?.ifBlank { null },
                                stockId = wvm.getStockId(ui.ticker)// save stock
                            )
                        )
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
private fun WatchCard(
    symbol: String,
    price: Double?,
    change: Double?,
    changePercent: Double?,
    isUp: Boolean?,
    onDelete: () -> Unit
) {

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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    //Text(name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text(symbol, style = MaterialTheme.typography.titleLarge, color = Color(0xFF9AA4B2))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {

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

                   // IconButton(onClick = onEdit) {
                    //    Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color(0xFFB3C5FF))
                  //  }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color(0xFFFF8C8C))
                    }
                }
            }
           // if (note.isNotBlank()) {
                //Spacer(Modifier.height(8.dp))
                //Text(note, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFDEE4EA))
           // }
        }
    }
}



