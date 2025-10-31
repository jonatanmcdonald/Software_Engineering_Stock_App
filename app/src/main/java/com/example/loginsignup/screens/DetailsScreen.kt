// screens/DetailsScreen.kt
package com.example.loginsignup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.data.db.entity.Transaction
import com.example.loginsignup.viewModels.SearchViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

// If your app already defines DetailsUi elsewhere, remove this duplicate.
data class DetailsUi(
    val country: String? = "",
    val currency: String? = "",
    val exchange: String? = "",
    val finnhubIndustry: String? = "",
    val ipo: String? = "",
    val logo: String? = "",
    val marketCapitalization: Double? = 0.0, // assume USD millions (Finnhub-style); adjust if different
    val name: String? = "",
    val phone: String? = "",
    val shareOutstanding: Double? = 0.0,
    val ticker: String? = "",
    val weburl: String? = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    ticker: String,
    userId: Int,
    onBack: () -> Unit,
    svm: SearchViewModel = viewModel()
) {
    // Kick off any price/details update the VM exposes for this screen
    LaunchedEffect(ticker) {
        // If your VM method accepts ticker, call startDetailsPriceUpdate(ticker)
        // Keeping your original no-arg call in case that's what you implemented:
        svm.startDetailsPriceUpdate(ticker)
    }

    val details by svm.viewPage.collectAsState()     // assumed to expose DetailsUi
    val price by svm.price.collectAsState()          // assumed to expose { price: BigDecimal? }

    // Safely extract latest price as BigDecimal (or $0.00)
    val latestPrice: BigDecimal = remember(price) {
        price.price.toBigDecimal()
    }

    var qtyText by rememberSaveable { mutableStateOf("1") }
    // keep only digits and at most one dot
    fun sanitizeQty(input: String): String {
        var dotSeen = false
        val sb = StringBuilder()
        for (c in input) {
            if (c.isDigit()) sb.append(c)
            else if (c == '.' && !dotSeen) { sb.append('.'); dotSeen = true }
        }
        return sb.toString()
    }
    val qty = remember(qtyText) { qtyText.toBigDecimalOrNull() ?: BigDecimal.ZERO }

    val total: BigDecimal = remember(latestPrice, qty) {
        latestPrice.multiply(qty).setScale(2, RoundingMode.HALF_UP)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = buildString {
                        append(details.name?.takeIf { it.isNotBlank() } ?: ticker)
                        details.ticker?.takeIf { it.isNotBlank() }?.let { append(" ($it)") }
                    }
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Price Card ---
            Card {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Price", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = latestPrice.toMoney(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val ch = price.change?.toBigDecimal()
                    val cp = price.changePercent?.toBigDecimal()

                    val sign = when (price.isUp) {
                        true -> "+"
                        false -> "−"
                        else -> ""
                    }
                    val changeAbs = ch?.abs()?.setScale(2, RoundingMode.HALF_UP)?.toPlainString()
                    val pctAbs = cp?.abs()?.setScale(2, RoundingMode.HALF_UP)?.toPlainString()
                    val color = when (price.isUp) {
                        true -> MaterialTheme.colorScheme.tertiary
                        false -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        text = if (ch == null || cp == null) "—"
                        else "$sign$${changeAbs} (${pctAbs}%)",
                        color = color,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // --- Quick buy calculator ---
            Card {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Quick Calculator", style = MaterialTheme.typography.labelMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = qtyText,
                            onValueChange = { qtyText = sanitizeQty(it) },
                            singleLine = true,
                            label = { Text("Quantity") },
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "× ${latestPrice.toMoney()}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total", style = MaterialTheme.typography.titleMedium)
                        Text(
                            total.toMoney(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                                    val transaction = Transaction(
                                        qty = qty.toInt(),
                                        price = total.toDouble(),
                                        timestamp = System.currentTimeMillis(),
                                        side = "BUY",
                                        symbol = ticker,
                                        fees = 0.0,
                                        userId = userId
                                    )
                                    svm.saveTransaction(transaction)
                                    onBack()
                                  },
                        enabled = qty > BigDecimal.ZERO && latestPrice > BigDecimal.ZERO,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("BUY • ${total.toMoney()}")
                    }
                }
            }

            // --- Company facts ---
            Card {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow("Ticker", details.ticker.orDash())
                    InfoRow("Name", details.name.orDash())
                    InfoRow("Exchange", details.exchange.orDash())
                    InfoRow("Industry", details.finnhubIndustry.orDash())
                    InfoRow("IPO", details.ipo.orDash())
                    InfoRow("Market Cap", details.marketCapitalization.toMarketCap(details.currency ?: "USD"))
                    InfoRow("Shares Out.", details.shareOutstanding?.let { prettyNumber(it) } ?: "—")
                    InfoRow("Currency", details.currency.orDash())
                    InfoRow("Country", details.country.orDash())
                    InfoRow("Phone", details.phone.orDash())
                    InfoRow("Website", details.weburl.orDash())
                }
            }
        }
    }
}

/* ---------- Small UI helpers ---------- */

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun String?.orDash(): String = if (this.isNullOrBlank()) "—" else this

private fun BigDecimal?.toMoney(): String =
    this?.setScale(2, RoundingMode.HALF_UP)?.let { "$" + it.toPlainString() } ?: "—"

private fun Double?.toMarketCap(
    currency: String,
    inputIsMillions: Boolean = true
): String {
    if (this == null || this.isNaN() || this.isInfinite()) return "—"

    // Convert to raw dollars if the source is "millions"
    val dollars = if (inputIsMillions) this * 1_000_000.0 else this
    val v = dollars
    val a = abs(v)
    val sym = symbolFor(currency)

    return when {
        a >= 1e12 -> String.format(Locale.US, "%s%.2fT", sym, v / 1e12)
        a >= 1e9  -> String.format(Locale.US, "%s%.2fB", sym, v / 1e9)
        a >= 1e6  -> String.format(Locale.US, "%s%.2fM", sym, v / 1e6)
        a >= 1e3  -> String.format(Locale.US, "%s%.2fK", sym, v / 1e3)
        else      -> String.format(Locale.US, "%s%.0f",  sym, v)
    }
}
private fun symbolFor(currency: String): String = when (currency.uppercase()) {
    "USD" -> "$"
    "EUR" -> "€"
    "GBP" -> "£"
    else  -> "" // keep generic if unknown
}

private fun prettyNumber(n: Double): String {
    val sign = if (n < 0) "-" else ""
    val a = abs(n)

    val (value, suffix) = when {
        a >= 1e12 -> a / 1e12 to "T"
        a >= 1e9  -> a / 1e9  to "B"
        a >= 1e6  -> a / 1e6  to "M"
        a >= 1e3  -> a / 1e3  to "K"
        else      -> a        to ""
    }

    val num = if (suffix.isEmpty()) {
        String.format(Locale.US, "%.0f", value)
    } else {
        String.format(Locale.US, "%.2f", value)
    }
    return sign + num + suffix
}
