// Package declaration for the screens
package com.example.loginsignup.screens


// Import statements for necessary classes and libraries
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.loginsignup.data.db.entity.Alert
import com.example.loginsignup.data.db.entity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

/**
 * Data class representing a row in the watchlist.
 */
data class WatchRow(
    val ticker: String
)

/**
 * Data class representing a piece of media in the UI.
 */
data class UiMedia(
    val uri: String,
    val type: String
)

/**
 * Data class representing a watchlist item in the UI.
 */
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
    val isUp: Boolean? = false,         // null if change unknown
    val alertActive: Boolean = true,
    val media: List<String> = emptyList(),
)


/**
 * Composable function for the Watchlist screen.
 *
 * @param userId The ID of the user.
 * @param wvm The WatchListViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchListScreen(
    userId: Int,
    wvm: WatchListViewModel = viewModel(),
   // onViewDetails: (String) -> Unit
) {
    // Effect to start live price updates when the screen is first composed for a user.
    LaunchedEffect(userId) { wvm.startWatchlistPriceUpdate(userId) }
    // Collect the list of watchlist rows as a state.
    val rows by wvm.watchRows.collectAsState()
   // Log.d("WatchList",rows.toString())
    // Remember a coroutine scope to launch suspend functions outside of composables.
    val scope = rememberCoroutineScope()
    // Collect the list of searched stocks as a state.
    val stockList by wvm.stockList.collectAsState()
    // Collect the loading state as a state.
    val isLoading by wvm.isLoading.collectAsState()
    // Collect the search query as a state.
    val searchQuery by wvm.searchQuery.collectAsState()
    // State to control the visibility of the 'Add to Watchlist' dialog.
    var showDialog by remember { mutableStateOf(false)}
    // State to hold the watchlist item being edited.
    var editing by remember {mutableStateOf<WatchRow?>(null)}

    // Scaffold provides a consistent layout structure (e.g., top bar, floating action button).
    Scaffold(
        // No insets for the content window.
        contentWindowInsets = WindowInsets(0),
        // Fill the entire screen.
        modifier = Modifier.fillMaxSize(),
        // Define the top app bar.
        topBar = {
            CenterAlignedTopAppBar(
                // Set a fixed height for the app bar.
                modifier = Modifier.height(100.dp),
                // No window insets.
                windowInsets = WindowInsets(0), // bigger than default
                // Define the colors for the app bar.
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    //containerColor = Color(0xFF1E2746),           // new color
                    containerColor = Color(0xFF0F1115),           // new color
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                // Define the title of the app bar.
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
        // Define the floating action button.
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color.Blue,
                contentColor = Color.White,
                onClick = {
                    // Clear the search query and show the 'Add to Watchlist' dialog.
                    wvm.onSearchQueryChanged("")
                    showDialog = true
                }
            ) { Icon(Icons.Default.Add, contentDescription = "Add") }
        },

    ) { inner ->

        // The main content of the screen.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // If the watchlist is empty, show a placeholder text.
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
                // Otherwise, display the watchlist items in a lazy column.
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
                            media = item.media,
                            onDelete = { wvm.delete(item.id) },
                            hasAlert = item.hasAlert,
                            alertParameter = item.alertParameter,
                            alertPrice = item.alertPrice,
                            alertActive = item.alertActive,
                            noteId = item.noteId,

                            onUpsertAlert = { parameter, price, alertActive -> scope.launch {  wvm.upsertAlert(
                                Alert(
                                    symbol = item.ticker,
                                    userId = userId,
                                    triggerPrice = price,
                                    triggerParent = "Watchlist",
                                    runCondition = parameter,
                                    isActive= alertActive
                                )
                            )}},

                            onToggleAlertActive = { isActive ->
                                scope.launch {
                                    wvm.toggleAlertActive("Watchlist", userId, item.ticker, isActive)
                                }
                            },
                            onSaveNoteWithMedia = { content, media, existingNoteId ->
                                scope.launch {
                                    wvm.saveNoteWithMedia(
                                       existingNoteId = existingNoteId,
                                        content = content,
                                        media = media,
                                        watchlistId = item.id,
                                        userId = userId
                                    )
                                }
                            }
                        )

                    }
                }
            }
        }

        // If `showDialog` is true, display the 'Add to Watchlist' dialog.
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

/**
 * Composable function for picking media for a note.
 *
 * @param onMediaSelected Callback for when media is selected.
 */
@Composable
fun NoteMediaPicker(
    onMediaSelected: (UiMedia) -> Unit
) {
    // Get the current context and coroutine scope.
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Remember a launcher for the photo picker.
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        // If a URI is selected, process it.
        if (uri != null) {
            // Determine the media type based on the URI.
            val type = when {
                uri.toString().contains("image") -> "image"
                uri.toString().contains("video") -> "video"
                else -> "unknown"
            }

            // Launch a coroutine in the IO dispatcher to persist the media.
            scope.launch(Dispatchers.IO) {
                try {
                    val persistedUri = persistPickedMediaToInternal(
                        context = context,
                        source = uri,
                        extension = if (type == "video") ".mp4" else ".jpg"
                    )

                    // Switch back to the main dispatcher to update the UI.
                    withContext(Dispatchers.Main) {
                        onMediaSelected(
                            UiMedia(
                                uri = persistedUri.toString(),
                                type = type
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // TODO: optionally show a Snackbar / Toast
                }
            }
        }
    }

    // The button that launches the media picker.
    Button(
        onClick = {
            pickerLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                )
            )
        }
    ) {
        Text(text = "Pick Media")
    }
}

/**
 * Composable function for a single watchlist card.
 *
 * @param symbol The stock symbol.
 * @param price The stock price.
 * @param change The price change.
 * @param changePercent The price change percentage.
 * @param isUp Whether the price is up.
 * @param noteContent The content of the note.
 * @param noteId The ID of the note.
 * @param onSaveNoteWithMedia Callback for saving a note with media.
 * @param media The media attached to the note.
 * @param onDelete Callback for deleting the watchlist item.
 * @param hasAlert Whether the item has an alert.
 * @param alertParameter The alert parameter.
 * @param alertPrice The alert price.
 * @param alertActive Whether the alert is active.
 * @param onUpsertAlert Callback for upserting an alert.
 * @param onToggleAlertActive Callback for toggling an alert's active state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchCard(
    symbol: String,
    price: Double?,
    change: Double?,
    changePercent: Double?,
    isUp: Boolean?,
    noteContent: String? = null,
    noteId: Long? = null,
    onSaveNoteWithMedia:(
        content: String,
        media: List<UiMedia>,
        existingNoteId: Long?
    ) -> Unit,
    media: List<String> = emptyList(),
    onDelete: () -> Unit,
    hasAlert: Boolean = false,          // from DB: does this watch have an alert
    alertParameter: String = "",        // e.g. "Less Than"
    alertPrice: Double = 0.0,           // alert threshold
    alertActive: Boolean = true,       // alert is active
    onUpsertAlert: (String, Double, Boolean) -> Unit,   // persist alert
    onToggleAlertActive: (Boolean) -> Unit = {}, //  toggle alert active

) {
    // Log media URIs for debugging purposes.
    Log.d("WatchList", "Media: $media")
    // Define the available options for price alerts.
    val alertOptions = listOf("LESS_THAN", "GREATER_THAN", "EQUAL_TO")

    // ===== NOTE STATE =====
    // State to control the visibility of the note editor.
    var showNoteEditor by remember { mutableStateOf(false) }
    // State to hold the editable text of the note, initialized with existing content or empty.
    var editableNoteText by remember(noteContent) {
        mutableStateOf(noteContent.orEmpty())
    }


    // State to manage the list of media items (images/videos) associated with the note.
   var localMedia by remember(noteId, media) {

       mutableStateOf(media.map {uri -> UiMedia(uri, "image")})
   }
    // Log the local media state for debugging.
    Log.d("WatchList", "Media: $localMedia")

    // ===== ALERT STATE =====
    // State to control the visibility of the alert editor.
    var showAlertEditor by remember { mutableStateOf(false) }
    // State to manage the expansion of the alert condition dropdown menu.
    var alertMenuExpanded by remember { mutableStateOf(false) }

    // State for the selected alert condition (e.g., "GREATER_THAN").
    var editableAlertParameter by remember(alertParameter) {
        mutableStateOf(alertParameter.ifBlank { alertOptions.first() })
    }
    // State for whether the alert is currently active or paused.
    var alertActiveParameter by remember(alertActive) {
        mutableStateOf(alertActive)
    }

    // State to hold the alert's trigger price as text to allow for flexible user input.
    var editableAlertValueText by remember(alertPrice) {
        mutableStateOf(
            if (alertPrice == 0.0) "" else alertPrice.toString()
        )
    }
    // State to hold any validation error message for the alert price input.
    var alertValueError by remember { mutableStateOf<String?>(null) }

    // Determine the color for price and change indicators based on whether the stock is up, down, or unchanged.
    val color = when (isUp) {
        true  -> Color(0xFF16A34A) // green
        false -> Color(0xFFDC2626) // red
        null  -> Color(0xFF9AA4B2) // neutral
    }

    // The main container for the watchlist item.
    Card(
        // Set the background color of the card.
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF171A21)
        ),
        // Set the elevation (shadow) of the card.
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // Make the card fill the maximum width available.
        modifier = Modifier.fillMaxWidth()
    ) {
        // Column to arrange the card's content vertically.
        Column(Modifier.padding(16.dp)) {

            // ===== HEADER: SYMBOL + PRICE =====
            // Row for the top section of the card, containing symbol, price, and delete button.
            Row(
                // Make the row fill the maximum width.
                Modifier.fillMaxWidth(),
                // Space elements evenly across the row.
                horizontalArrangement = Arrangement.SpaceBetween,
                // Center elements vertically.
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column for the stock symbol, taking up available space.
                Column(Modifier.weight(1f)) {
                    // Display the stock symbol.
                    Text(
                        symbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF9AA4B2)
                    )
                }
                // Row for price, change, and delete icon.
                Row(
                    // Add space between elements in this row.
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    // Center elements vertically.
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display the stock price, formatted to 2 decimal places, or "--" if null.
                    Text(
                        text = price?.let { String.format(Locale.getDefault(), "%.2f", it) } ?: "--",
                        style = MaterialTheme.typography.titleLarge,
                        color = color
                    )
                    // Display the price change if it's not null.
                    if (change != null) {
                        Text(
                            text = "(" + String.format(Locale.getDefault(), "%.2f", change) + ")",
                            style = MaterialTheme.typography.bodyMedium,
                            color = color
                        )
                    }
                    // Display the percentage change if it's not null.
                    if (changePercent != null) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f%%", changePercent),
                            style = MaterialTheme.typography.bodySmall,
                            color = color
                        )
                    }

                    // Add some space before the delete button.
                    Spacer(Modifier.width(8.dp))

                    // Delete button.
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF8C8C)
                        )
                    }
                }
            }


            // ===== Media Display =====
            // Add vertical spacing.
            Spacer(Modifier.height(8.dp))
            // Check if there is any media to display.
            if (localMedia.isNotEmpty()) {
                // Add more vertical spacing.
                Spacer(Modifier.height(8.dp))
                // "Attached Media" title.
                Text(
                    text = "Attached Media",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDEE4EA)
                )
                // Add vertical spacing.
                Spacer(Modifier.height(4.dp))

                // A horizontal row to display media thumbnails.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Loop through each media item.
                    localMedia.forEach { media ->
                        // Log media item for debugging.
                        Log.d("WatchList", media.toString())
                        // Display image media.
                        if (media.type == "image" || media.type == "unknown") {
                            AsyncImage(
                                model = media.uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // simple video placeholder – you can upgrade this later
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Vid",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

                // ===== NOTE EDITOR (BOTTOM) =====
                // Add vertical spacing.
                Spacer(Modifier.height(8.dp))
                // Show the note editor UI if `showNoteEditor` is true.
                if (showNoteEditor) {
                    // Add more vertical spacing.
                    Spacer(Modifier.height(8.dp))
                    // Text field for editing the note content.
                    OutlinedTextField(
                        value = editableNoteText,
                        onValueChange = { editableNoteText = it },
                        label = { Text("Note") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 5
                    )
                    // Add vertical spacing.
                    Spacer(Modifier.height(8.dp))
                    // Row for action buttons (Cancel, Pick Media, Save).
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Cancel button.
                        TextButton(onClick = { showNoteEditor = false }) {
                            Text("Cancel")
                        }

                        // Add space between buttons.
                        Spacer(Modifier.width(8.dp))
                        // Media picker button.
                        NoteMediaPicker { media ->
                            localMedia = localMedia + media
                        }
                        // Add space between buttons.
                        Spacer(Modifier.width(8.dp))
                        // Save button.
                        Button(
                            onClick = {
                                // Hide the editor.
                                showNoteEditor = false
                                // Trigger the save action with the updated content and media.
                                onSaveNoteWithMedia(editableNoteText.trim(), localMedia, noteId)
                            },
                            // Custom button colors.
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
                // If a note exists and the editor is hidden, display the note content.
                if (!noteContent.isNullOrBlank() && !showNoteEditor) {
                    // "Note" title.
                    Text(
                        text = "Note",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFDEE4EA)
                    )
                    // Add vertical spacing.
                    Spacer(Modifier.height(8.dp))

                    // A styled box to display the note content.
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
                    // Add vertical spacing.
                    Spacer(Modifier.height(8.dp))
                    // "Edit" button to show the note editor.
                    TextButton(onClick = {
                        editableNoteText = noteContent
                        showNoteEditor = true
                    }) {
                        Text("Edit", color = Color(0xFFB3C5FF))
                    }
                // If no note exists and the editor is hidden, show an "Add Note" button.
                } else if (noteContent.isNullOrBlank() && !showNoteEditor) {
                    // "No note yet" placeholder text.
                    Text(
                        text = "No note yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFDEE4EA),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Add vertical spacing.
                    Spacer(Modifier.height(4.dp))

                    // "Add Note" button to show the note editor.
                    TextButton(onClick = {
                        editableNoteText = ""
                        showNoteEditor = true
                    }, modifier = Modifier.align(Alignment.End)) {
                        Text("Add Note", color = Color(0xFFB3C5FF))
                    }
                }


            // ===== ALERT DISPLAY / ADD / EDIT BUTTON =====
            // Add vertical spacing.
            Spacer(Modifier.height(8.dp))
            // ===== ALERT EDITOR =====
            // Show the alert editor UI if `showAlertEditor` is true.
            if (showAlertEditor) {
                // Add more vertical spacing.
                Spacer(Modifier.height(8.dp))

                // "Price Alert" title.
                Text(
                    "Price Alert",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDEE4EA)
                )
                // Add vertical spacing.
                Spacer(Modifier.height(4.dp))

                // Dropdown menu box for selecting the alert condition.
                ExposedDropdownMenuBox(
                    expanded = alertMenuExpanded,
                    onExpandedChange = { alertMenuExpanded = !alertMenuExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Text field that displays the selected condition.
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

                    // The dropdown menu itself.
                    ExposedDropdownMenu(
                        expanded = alertMenuExpanded,
                        onDismissRequest = { alertMenuExpanded = false },
                        modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = true)
                    ) {
                        // Create a menu item for each alert option.
                        alertOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    // Update the selected parameter and close the menu.
                                    editableAlertParameter = option
                                    alertMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Add vertical spacing.
                Spacer(Modifier.height(8.dp))

                // Text field for entering the alert price.
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

                // Display an error message if the input is invalid.
                if (alertValueError != null) {
                    Text(
                        text = alertValueError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Add vertical spacing.
                Spacer(Modifier.height(8.dp))

                // Row for action buttons (Cancel, Save).
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel button.
                    TextButton(onClick = {
                        showAlertEditor = false
                        alertValueError = null
                    }) {
                        Text("Cancel")
                    }
                    // Add space between buttons.
                    Spacer(Modifier.width(8.dp))
                    // Save button.
                    Button(
                        onClick = {
                            // Parse the input text to a Double.
                            val parsed = editableAlertValueText.toDoubleOrNull()
                            // If parsing fails, show an error.
                            if (parsed == null) {
                                alertValueError = "Enter a valid number"
                            } else {
                                // Otherwise, hide the editor and save the alert.
                                showAlertEditor = false
                                alertValueError = null
                                onUpsertAlert(editableAlertParameter, parsed, alertActiveParameter)
                            }
                        },
                        // Custom button colors.
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF171A21),
                            contentColor = Color(0xFFB3C5FF)
                        )
                    ) {
                        Text("Save")
                    }
                }
            }

            // If there is no alert and the editor is hidden, show an "Add Alert" button.
            if (!hasAlert && !showAlertEditor) {
                // "No alert yet" placeholder text.
                Text(
                    text = "No alert yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDEE4EA),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                // "Add Alert" button.
                TextButton(onClick = { showAlertEditor = true }, modifier = Modifier.align(Alignment.End)) {
                    Text("Add Alert", color = Color(0xFFB3C5FF))

                }
            // If an alert exists and the editor is hidden, display the alert info.
            } else if (hasAlert && !showAlertEditor) {
                // "Alert" title.
                Text(
                    text = "Alert",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDEE4EA)
                )
                // Add vertical spacing.
                Spacer(Modifier.height(8.dp))
                // A styled box to display the alert details.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1F2430), shape = MaterialTheme.shapes.medium)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Price $alertParameter $$alertPrice",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB3C5FF)
                    )
                }
                // Add vertical spacing.
                Spacer(Modifier.height(4.dp))

                // Row for the alert's active status and toggle switch.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display whether the alert is active or paused.
                    Text(
                        text = if (alertActiveParameter) "Alert is ACTIVE" else "Alert is PAUSED",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (alertActiveParameter) Color(0xFF4ADE80) else Color(0xFF9AA4B2)
                    )

                    // Switch to toggle the alert's active state.
                    Switch(
                        checked = alertActiveParameter,
                        onCheckedChange = { newValue ->
                            alertActiveParameter = newValue
                            onToggleAlertActive(newValue)
                        }
                    )
                }

                // "Edit Alert" button.
                TextButton(onClick = { showAlertEditor = true }) {
                    Text("Edit Alert", color = Color(0xFFB3C5FF))
                }
            }


        }
    }
}

/**
 * Persists the picked media to internal storage.
 *
 * @param context The context.
 * @param source The source URI of the media.
 * @param extension The file extension of the media.
 * @return The URI of the persisted media.
 */
fun persistPickedMediaToInternal(
    context: Context,
    source: Uri,
    extension: String
): Uri {
    // Open an input stream for the source URI.
    val input = context.contentResolver.openInputStream(source)
        ?: throw IllegalStateException("Cannot open InputStream for $source")

    // Create a new file in the app's internal storage.
    val file = File(
        context.filesDir,
        "note_media_${System.currentTimeMillis()}$extension"
    )

    // Copy the content from the input stream to the new file.
    input.use { inStream ->
        file.outputStream().use { outStream ->
            inStream.copyTo(outStream)
        }
    }

    // Return the URI of the new file.
    return file.toUri()
}




