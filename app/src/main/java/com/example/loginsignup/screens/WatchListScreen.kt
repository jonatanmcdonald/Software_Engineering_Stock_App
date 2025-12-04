package com.example.loginsignup.screens


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

data class WatchRow(
    val ticker: String
)

data class UiMedia(
    val uri: String,
    val type: String
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
    val isUp: Boolean? = false,         // null if change unknown
    val alertActive: Boolean = true,
    val media: List<String> = emptyList(),
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

@Composable
fun NoteMediaPicker(
    onMediaSelected: (UiMedia) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val type = when {
                uri.toString().contains("image") -> "image"
                uri.toString().contains("video") -> "video"
                else -> "unknown"
            }

            scope.launch(Dispatchers.IO) {
                try {
                    val persistedUri = persistPickedMediaToInternal(
                        context = context,
                        source = uri,
                        extension = if (type == "video") ".mp4" else ".jpg"
                    )

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
    Log.d("WatchList", "Media: $media")
    val alertOptions = listOf("LESS_THAN", "GREATER_THAN", "EQUAL_TO")

    // ===== NOTE STATE =====
    var showNoteEditor by remember { mutableStateOf(false) }
    var editableNoteText by remember(noteContent) {
        mutableStateOf(noteContent.orEmpty())
    }


   var localMedia by remember(noteId, media) {

       mutableStateOf(media.map {uri -> UiMedia(uri, "image")})
   }
    Log.d("WatchList", "Media: $localMedia")

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


            // ===== Media Display
            Spacer(Modifier.height(8.dp))
            if (localMedia.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Attached Media",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDEE4EA)
                )
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    localMedia.forEach { media ->
                        Log.d("WatchList", media.toString())
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
                Spacer(Modifier.height(8.dp))
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
                        NoteMediaPicker { media ->
                            localMedia = localMedia + media
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showNoteEditor = false
                                onSaveNoteWithMedia(editableNoteText.trim(), localMedia, noteId)
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
                        text = "Price $alertParameter $$alertPrice",
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

fun persistPickedMediaToInternal(
    context: Context,
    source: Uri,
    extension: String
): Uri {
    val input = context.contentResolver.openInputStream(source)
        ?: throw IllegalStateException("Cannot open InputStream for $source")

    val file = File(
        context.filesDir,
        "note_media_${System.currentTimeMillis()}$extension"
    )

    input.use { inStream ->
        file.outputStream().use { outStream ->
            inStream.copyTo(outStream)
        }
    }

    return file.toUri()
}




