package com.example.loginsignup.screens

import com.example.loginsignup.data.StockAppViewModel
import com.example.loginsignup.data.WatchList
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.loginsignup.components.AddWatchlistItemDialog
import com.example.loginsignup.data.models.Stocks
import com.example.loginsignup.viewModels.WatchListViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


data class WatchListUi(val name: String, val stock: Stocks, val note: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchListScreen(
    userId: String,
    vm: StockAppViewModel = viewModel(),
    wvm: WatchListViewModel = viewModel()
) {
    val rows by vm.getAllForUser(userId).observeAsState(emptyList())
    val scope = rememberCoroutineScope()
    val stockList by wvm.stockList.collectAsState()
    val isLoading by wvm.isLoading.collectAsState()
    val searchQuery by wvm.searchQuery.collectAsState()
    var showDialog by remember { mutableStateOf(false)}

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(100.dp),
                windowInsets = WindowInsets(0), // bigger than default
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1E2746),           // new color
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
                onClick = { showDialog = true }
            ) { Icon(Icons.Default.Add, contentDescription = "Add") }
        },

    ) { inner ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .background(Color(0xFF0F1115))
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
                    items(items = rows, key = { it.stock }) { item ->
                        WatchCard(
                            name = item.name,
                            symbol = item.stock,  // persisted as symbol
                            note = item.note ?: "",
                            onEdit = {
                                // simple inline edit: open dialog pre-filled
                                showDialog = true

                                // you could pass current item into dialog via remember(...)
                            },

                            onDelete = {
                                scope.launch {
                                    vm.deleteWatchListItem(userId, item.stock)
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AddWatchlistItemDialog(
                onDismissRequest = { showDialog = false },

                stockList = stockList,
                isLoading = isLoading,
                searchQuery = searchQuery,
                onQueryChange = wvm::onSearchQueryChanged,
                onStockSelected = wvm::onStockSelected,
                onSave = { ui ->
                    // Persist: map UI -> entity
                    scope.launch {
                        vm.upsertByUserAndStock(
                            WatchList(
                                userId = userId,
                                name = ui.name,
                                note = ui.note.ifBlank { null },
                                stock = ui.stock.symbol, // save symbol
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
    name: String,
    symbol: String,
    note: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                    Text(name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text(symbol, style = MaterialTheme.typography.titleSmall, color = Color(0xFF9AA4B2))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color(0xFFB3C5FF))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color(0xFFFF8C8C))
                    }
                }
            }
            if (note.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(note, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFDEE4EA))
            }
        }
    }
}



