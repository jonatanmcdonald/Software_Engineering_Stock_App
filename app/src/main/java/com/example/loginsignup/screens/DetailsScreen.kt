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
    ticker: String, // The stock ticker symbol to display details for.
    userId: Int, // The ID of the current user.
    onBack: () -> Unit, // Callback function to navigate back.
    svm: SearchViewModel = viewModel() // Injects the SearchViewModel.
) {
    // This effect runs when the ticker symbol changes.
    LaunchedEffect(ticker) {
        // If your VM method accepts ticker, call startDetailsPriceUpdate(ticker)
        // Keeping your original no-arg call in case that's what you implemented:
        svm.startDetailsPriceUpdate(ticker)
    }

    val details by svm.viewPage.collectAsState()     // Collects the stock details from the ViewModel as state.
    val price by svm.price.collectAsState()          // Collects the stock price from the ViewModel as state.

    // Safely extract latest price as BigDecimal (or $0.00)
    val latestPrice: BigDecimal = remember(price) {
        price.price.toBigDecimal()
    }

    var qtyText by rememberSaveable { mutableStateOf("1") } // State for the quantity text field.
    // keep only digits and at most one dot
    fun sanitizeQty(input: String): String { // Sanitizes the quantity input to allow only numbers and a single decimal point.
        var dotSeen = false
        val sb = StringBuilder()
        for (c in input) {
            if (c.isDigit()) sb.append(c)
            else if (c == '.' && !dotSeen) { sb.append('.'); dotSeen = true }
        }
        return sb.toString()
    }
    val qty = remember(qtyText) { qtyText.toBigDecimalOrNull() ?: BigDecimal.ZERO } // Converts the quantity text to a BigDecimal.

    val total: BigDecimal = remember(latestPrice, qty) { // Calculates the total cost of the transaction.
        latestPrice.multiply(qty).setScale(2, RoundingMode.HALF_UP)
    }
    Scaffold( // A basic Material Design layout structure.
        topBar = { // The top app bar of the screen.
            TopAppBar(
                title = { // The title of the app bar.
                    val title = buildString { // Builds the title string.
                        append(details.name?.takeIf { it.isNotBlank() } ?: ticker)
                        details.ticker?.takeIf { it.isNotBlank() }?.let { append(" ($it)") }
                    }
                    Text(title)
                },
                navigationIcon = { // The navigation icon of the app bar.
                    IconButton(onClick = onBack) { // A button to navigate back.
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding -> // The content of the screen.
        Column(
            modifier = Modifier
                .padding(padding) // Applies padding from the Scaffold.
                .padding(16.dp) // Applies additional padding.
                .fillMaxSize(), // Fills the maximum available size.
            verticalArrangement = Arrangement.spacedBy(16.dp) // Adds spacing between the children.
        ) {
            // --- Price Card ---
            Card { // A card to display the stock price.
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Price", style = MaterialTheme.typography.labelMedium) // The title of the card.
                    Text(
                        text = latestPrice.toMoney(), // The current price of the stock.
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val ch = price.change?.toBigDecimal()
                    val cp = price.changePercent?.toBigDecimal()

                    val sign = when (price.isUp) { // Determines the sign of the price change.
                        true -> "+"
                        false -> "−"
                        else -> ""
                    }
                    val changeAbs = ch?.abs()?.setScale(2, RoundingMode.HALF_UP)?.toPlainString()
                    val pctAbs = cp?.abs()?.setScale(2, RoundingMode.HALF_UP)?.toPlainString()
                    val color = when (price.isUp) { // Determines the color of the price change text.
                        true -> MaterialTheme.colorScheme.tertiary
                        false -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        text = if (ch == null || cp == null) "—"
                        else "$sign$${changeAbs} (${pctAbs}%)", // The price change and percentage.
                        color = color,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // --- Quick buy calculator ---
            Card { // A card to calculate the total cost of a purchase.
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Quick Calculator", style = MaterialTheme.typography.labelMedium) // The title of the card.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = qtyText,
                            onValueChange = { qtyText = sanitizeQty(it) }, // Updates the quantity text when the value changes.
                            singleLine = true,
                            label = { Text("Quantity") }, // The label for the text field.
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "× ${latestPrice.toMoney()}", // The current price of the stock.
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total", style = MaterialTheme.typography.titleMedium) // The label for the total cost.
                        Text(
                            total.toMoney(), // The total cost.
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { // The action to perform when the button is clicked.
                                    val transaction = Transaction( // Creates a new transaction object.
                                        qty = qty.toInt(),
                                        price = latestPrice.toDouble(),
                                        timestamp = System.currentTimeMillis(),
                                        side = "BUY",
                                        symbol = ticker,
                                        fees = 0.0,
                                        userId = userId
                                    )
                                    svm.saveTransaction(transaction) // Saves the transaction to the database.
                                    onBack() // Navigates back to the previous screen.
                                  },
                        enabled = qty > BigDecimal.ZERO && latestPrice > BigDecimal.ZERO, // The button is enabled only if the quantity and price are greater than zero.
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("BUY • ${total.toMoney()}") // The text to display on the button.
                    }
                }
            }

            // --- Company facts ---
            Card { // A card to display company facts.
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow("Ticker", details.ticker.orDash()) // Displays the ticker symbol.
                    InfoRow("Name", details.name.orDash()) // Displays the company name.
                    InfoRow("Exchange", details.exchange.orDash()) // Displays the stock exchange.
                    InfoRow("Industry", details.finnhubIndustry.orDash()) // Displays the industry.
                    InfoRow("IPO", details.ipo.orDash()) // Displays the IPO date.
                    InfoRow("Market Cap", details.marketCapitalization.toMarketCap(details.currency ?: "USD")) // Displays the market capitalization.
                    InfoRow("Shares Out.", details.shareOutstanding?.let { prettyNumber(it) } ?: "—") // Displays the number of outstanding shares.
                    InfoRow("Currency", details.currency.orDash()) // Displays the currency.
                    InfoRow("Country", details.country.orDash()) // Displays the country.
                    InfoRow("Phone", details.phone.orDash()) // Displays the phone number.
                    InfoRow("Website", details.weburl.orDash()) // Displays the website.
                }
            }
        }
    }
}

/* ---------- Small UI helpers ---------- */

@Composable
private fun InfoRow(label: String, value: String) { // A composable to display a row of information.
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun String?.orDash(): String = if (this.isNullOrBlank()) "—" else this // Returns a dash if the string is null or blank.

private fun BigDecimal?.toMoney(): String = // Converts a BigDecimal to a money string.
    this?.setScale(2, RoundingMode.HALF_UP)?.let { "$" + it.toPlainString() } ?: "—"

private fun Double?.toMarketCap( // Converts a Double to a market cap string.
    currency: String,
    inputIsMillions: Boolean = true
): String {
    if (this == null || this.isNaN() || this.isInfinite()) return "—"

    // Convert to raw dollars if the source is "millions"
    val dollars = if (inputIsMillions) this * 1_000_000.0 else this
    val v = dollars
    val a = abs(v)
    val sym = symbolFor(currency)

    return when { // Formats the market cap based on its value.
        a >= 1e12 -> String.format(Locale.US, "%s%.2fT", sym, v / 1e12)
        a >= 1e9  -> String.format(Locale.US, "%s%.2fB", sym, v / 1e9)
        a >= 1e6  -> String.format(Locale.US, "%s%.2fM", sym, v / 1e6)
        a >= 1e3  -> String.format(Locale.US, "%s%.2fK", sym, v / 1e3)
        else      -> String.format(Locale.US, "%s%.0f",  sym, v)
    }
}
private fun symbolFor(currency: String): String = when (currency.uppercase()) { // Returns the symbol for a given currency.
    "USD" -> "$"
    "EUR" -> "€"
    "GBP" -> "£"
    else  -> "" // keep generic if unknown
}

private fun prettyNumber(n: Double): String { // Formats a number to be more readable.
    val sign = if (n < 0) "-" else ""
    val a = abs(n)

    val (value, suffix) = when { // Determines the suffix for the number.
        a >= 1e12 -> a / 1e12 to "T"
        a >= 1e9  -> a / 1e9  to "B"
        a >= 1e6  -> a / 1e6  to "M"
        a >= 1e3  -> a / 1e3  to "K"
        else      -> a        to ""
    }

    val num = if (suffix.isEmpty()) { // Formats the number based on the suffix.
        String.format(Locale.US, "%.0f", value)
    } else {
        String.format(Locale.US, "%.2f", value)
    }
    return sign + num + suffix
}
