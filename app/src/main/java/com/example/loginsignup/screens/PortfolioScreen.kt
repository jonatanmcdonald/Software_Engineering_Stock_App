// screens/PortfolioScreen.kt
package com.example.loginsignup.screens

import android.util.Log
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.data.db.entity.Alert
import com.example.loginsignup.data.db.entity.Transaction
import com.example.loginsignup.viewModels.PortfolioViewModel
import kotlinx.coroutines.launch
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
    val dayChange: Double? = null,      // change_per_share * qty
    val hasAlert: Boolean = false,
    val alertParameter: String = "",
    val alertPrice: Double = 0.0,
    val alertActive: Boolean = true
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
    val scope = rememberCoroutineScope()
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
                        PortfolioCard(
                            item,
                            item.hasAlert,
                            item.alertParameter,
                            item.alertPrice,
                            item.alertActive,
                            onUpsertAlert = { parameter, price, active ->
                               scope.launch {
                                   pvm.upsertAlert(
                                       Alert(
                                           symbol = item.ticker,
                                           userId = userId,
                                           triggerPrice = price,
                                           triggerParent = "Portfolio",
                                           runCondition = parameter,
                                           isActive = active
                                       )
                                   )
                               }
                            },
                            onToggleAlertActive = { isActive ->
                                scope.launch {
                                    pvm.toggleAlertActive("Portfolio", userId, item.ticker, isActive)
                                }
                            }


                        ) { clickedRow ->
                            selectedStock = clickedRow

                        }
                    }
                }

                if (selectedStock != null) {
                    SellStockDialog(
                        stock = selectedStock!!,
                        onDismiss = {selectedStock = null},
                        onConfirm = { qty ->
                            val pricePerShare = selectedStock!!.last!!
                            val transaction = Transaction(
                                qty = qty.toInt(),
                                price = pricePerShare,
                                timestamp = System.currentTimeMillis(),
                                side = "SELL",
                                symbol = selectedStock!!.ticker,
                                fees = 0.0,
                                userId = userId
                            )
                            pvm.saveSellTransaction(transaction)
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
@OptIn(ExperimentalMaterial3Api::class)
private fun PortfolioCard(
    row: LivePortfolio,
    hasAlert: Boolean = false,
    alertParameter: String = "",
    alertPrice: Double = 0.0,
    alertActive: Boolean = true,
    onUpsertAlert: (String, Double, Boolean) -> Unit,
    onToggleAlertActive: (Boolean) -> Unit,
    onClick: (LivePortfolio) -> Unit
) {
    val alertOptions = listOf("LESS_THAN", "GREATER_THAN", "EQUAL_TO")
    val isUp = row.unrealizedPnl?.let { it >= 0.0 }
    val priceColor = when (isUp) {
        true  -> Color(0xFF16A34A)
        false -> Color(0xFFDC2626)
        null  -> Color(0xFF9AA4B2)
    }

    val pnl = row.unrealizedPnl

    Log.d("PortfolioCard", "PortfolioCard: $pnl")

    val color = when {
        pnl == null -> Color(0xFF9AA4B2)         // no data / neutral
        pnl > 0.0   -> Color(0xFF16A34A)         // green for gain
        pnl < 0.0   -> Color(0xFFDC2626)         // red for loss
        else        -> Color.White               // exactly 0.0 → neutral/white
    }


    // ===== ALERT STATE =====
    var showAlertEditor by remember { mutableStateOf(false) }
    var alertMenuExpanded by remember { mutableStateOf(false) }

    var editableAlertParameter by remember(alertParameter) {
        mutableStateOf(alertParameter.ifBlank { alertOptions.first() })
    }
    var alertActiveParameter by remember(alertActive) {
        mutableStateOf(alertActive)
    }

    // keep price as text while editing to avoid crashes on invalid input
    var editableAlertValueText by remember(alertPrice) {
        mutableStateOf(
            if (alertPrice == 0.0) "" else alertPrice.toString()
        )
    }
    var alertValueError by remember { mutableStateOf<String?>(null) }

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
                        color = priceColor
                    )
                    Text(
                        text = row.dayChangePct?.let { pct(it) } ?: "—",
                        style = MaterialTheme.typography.bodySmall,
                        color = priceColor
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
                    color
                )
                LabeledValue(
                    "Total P&L",
                    row.totalPnl?.let { money(it) } ?: "--",
                    color
                )
            }

            // ===== ALERT DISPLAY / ADD / EDIT BUTTON =====
            Spacer(Modifier.height(8.dp))
            // ===== ALERT EDITOR =====
            if (showAlertEditor) {
                Spacer(Modifier.height(8.dp))

                Text(
                    "Percent Change Alert",
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
                    label = { Text("Alert Percentage") },
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
                            if (parsed == null || parsed > 100.00) {
                                alertValueError = "Enter a valid percentage"
                            }else {
                                showAlertEditor = false
                                alertValueError = null
                                onUpsertAlert(editableAlertParameter, parsed, alertActiveParameter)
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

            Spacer(Modifier.height(10.dp))

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
                    text = "Alert",
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
                        text = "$alertParameter $alertPrice% of average cost value",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB3C5FF)
                    )
                }
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (alertActiveParameter) "Alert is ACTIVE" else "Alert is PAUSED",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (alertActiveParameter) Color(0xFF4ADE80) else Color(0xFF9AA4B2)
                    )

                    Switch(
                        checked = alertActiveParameter,
                        onCheckedChange = { newValue ->
                            alertActiveParameter = newValue
                            onToggleAlertActive(newValue)
                        }
                    )
                }

                TextButton(onClick = { showAlertEditor = true }) {
                    Text("Edit Alert", color = Color(0xFFB3C5FF))
                }
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
