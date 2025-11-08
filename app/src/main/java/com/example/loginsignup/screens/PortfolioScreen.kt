// screens/PortfolioScreen.kt
package com.example.loginsignup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.TableInfo
import com.example.loginsignup.viewModels.PortfolioViewModel
import java.util.Locale
import kotlin.math.absoluteValue

// ---------- UI row model ----------
data class LivePortfolio(
    val id: Long = 0L,
    val ticker: String,
    val qty: Double,
    val avgCost: Double,
    val costBasis: Double,
    val realizedPnl: Double,
    val last: Double? = null,          // live price per share
    val prevClose: Double? = null,     // inferred when possible
    val unrealizedPnl: Double? = null, // (last - avgCost) * qty
    val dayChangePct: Double? = null,  // percent (e.g., 1.23 for +1.23%)
    val marketValue: Double? = null,   // qty * last
    val totalPnl: Double? = null,      // realized + unrealized
    val dayChange: Double? = null      // change_per_share * qty
)

// ---------- Screen ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    userId: Int,
    pvm: PortfolioViewModel = viewModel(),
    onNavigateToSearch: () -> Unit
) {
    // start live updates for this user
    LaunchedEffect(userId) { pvm.startPortfolioValueUpdate(userId) }

    val rows by pvm.portfolioRows.collectAsState(emptyList())
    val isLoading by pvm.isLoading.collectAsState()

    // aggregate summary
    val totalValue = remember(rows) { rows.sumOf { it.marketValue ?: 0.0 } }
    val totalUnreal = remember(rows) { rows.sumOf { it.unrealizedPnl ?: 0.0 } }
    val totalRealized = remember(rows) { rows.sumOf { it.realizedPnl } }
    val totalPnl = totalRealized + totalUnreal
    val dayPnl = remember(rows) { rows.sumOf { it.dayChange ?: 0.0 } }

    var selectedStock by remember { mutableStateOf<LivePortfolio?>(null)}

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(100.dp),
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0F1115),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("My Portfolio", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White,
                onClick = onNavigateToSearch
            ) { Icon(Icons.Default.Add, contentDescription = "Add") }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .background(Color(0xFF0B0E13))
        ) {
            // Summary header
            SummaryStrip(
                totalValue = totalValue,
                totalPnl = totalPnl,
                dayPnl = dayPnl
            )

            if (rows.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isLoading) "Loading…" else "No positions yet.\nTap + to add your first stock",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFE5E7EB),
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
                        PortfolioCard(item) { clickedRow ->
                            selectedStock = clickedRow
                        }
                    }
                }

                if (selectedStock != null) {
                    SellStockDialog(
                        stock = selectedStock!!,
                        onDismiss = {selectedStock = null},
                        onConfirm = { qty ->
                            pvm.sellStock(selectedStock!!.id, qty)
                            selectedStock = null
                        }
                    )
                }

            }
        }
    }
}

// ---------- Summary ----------
@Composable
private fun SummaryStrip(
    totalValue: Double,
    totalPnl: Double,
    dayPnl: Double
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121623)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Stat("Total Value", money(totalValue), Color(0xFFE5E7EB))
            Stat(
                "Total P&L",
                money(totalPnl),
                if (totalPnl >= 0) Color(0xFF16A34A) else Color(0xFFDC2626)
            )
            Stat(
                "Day P&L",
                money(dayPnl),
                if (dayPnl >= 0) Color(0xFF16A34A) else Color(0xFFDC2626)
            )
        }
    }
}

@Composable
private fun Stat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color(0xFF9AA4B2), style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        Text(value, color = color, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

// ---------- Row Card ----------
@Composable
private fun PortfolioCard(
    row: LivePortfolio,
    onClick: (LivePortfolio) -> Unit
) {
    val isUp = row.unrealizedPnl?.let { it >= 0.0 }
    val color = when (isUp) {
        true  -> Color(0xFF16A34A)
        false -> Color(0xFFDC2626)
        null  -> Color(0xFF9AA4B2)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171A21)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(row) }
    ) {
        Column(Modifier.padding(16.dp)) {

            // Top row: Ticker + Price + Day %
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(row.ticker, style = MaterialTheme.typography.titleLarge, color = Color(0xFFE5E7EB))
                    Text(
                        "Qty ${fmtQty(row.qty)} • Avg ${money(row.avgCost)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9AA4B2)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = row.last?.let { money(it) } ?: "--",
                        style = MaterialTheme.typography.titleLarge,
                        color = color
                    )
                    Text(
                        text = row.dayChangePct?.let { pct(it) } ?: "—",
                        style = MaterialTheme.typography.bodySmall,
                        color = color
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Bottom row: Market Value, Unrealized, Total
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LabeledValue("Value", row.marketValue?.let { money(it) } ?: "--", Color(0xFFE5E7EB))
                LabeledValue(
                    "Unrealized",
                    row.unrealizedPnl?.let { money(it) } ?: "--",
                    if ((row.unrealizedPnl ?: 0.0) >= 0) Color(0xFF16A34A) else Color(0xFFDC2626)
                )
                LabeledValue(
                    "Total P&L",
                    row.totalPnl?.let { money(it) } ?: "--",
                    if ((row.totalPnl ?: 0.0) >= 0) Color(0xFF16A34A) else Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
private fun LabeledValue(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, color = Color(0xFF9AA4B2), style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(4.dp))
        Text(value, color = valueColor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SellStockDialog(
    stock: LivePortfolio,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Double) -> Unit
) {
    var qtyToSell by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sell ${stock.ticker}") },
        text = {
            Column {
                Text("Available: ${fmtQty(stock.qty)} shares")
                TextField(
                    value = qtyToSell,
                    onValueChange = {qtyToSell = it},
                    label = { Text("Quantity to sell")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = qtyToSell.toDoubleOrNull()
                    if (qty != null && qty > 0 && qty <= stock.qty) {
                        onConfirm(qty)
                        onDismiss()
                    }
                }
            ) { Text("Sell") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ---------- Formatters ----------
private fun money(v: Double): String = String.format(Locale.US, "$%.2f", v)
private fun pct(p: Double): String {
    val value = if (p.absoluteValue <= 1.0) p * 100.0 else p
    return String.format(Locale.US, "%.2f%%", value)
}
private fun fmtQty(q: Double): String = if (q % 1.0 == 0.0) q.toInt().toString() else String.format(Locale.US, "%.3f", q)
