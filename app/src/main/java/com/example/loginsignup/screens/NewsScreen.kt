package com.example.loginsignup.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.loginsignup.data.models.NewsItem
import com.example.loginsignup.viewModels.NewsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// --------- Top-level Screen -----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    nvm: NewsViewModel = viewModel(),
    timezone: String = "America/Chicago",
) {
    val ctx = LocalContext.current

    val newsItems by nvm.news.collectAsState()
    val isLoading by nvm.isLoading.collectAsState()
    Log.d("NewsScreen", "newsItems: $newsItems")


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            modifier = Modifier
                                .size(22.dp)
                                .padding(end = 6.dp)
                        )
                        Text("Market News", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                LoadingState()
            }
            else {
                if (newsItems.isEmpty())
                {
                    EmptyState()
                }
                else {
                    NewsList(
                        items = newsItems,
                        timezone = timezone,
                        onOpen = { openInCustomTab(ctx, it.url) }
                    )
                }



        }
        }
    }
}

// --------- UI Pieces ---------

@Composable
private fun NewsList(
    items: List<NewsItem>,
    timezone: String,
    onOpen: (NewsItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(items, key = { it.id }) { item ->
            NewsCard(item = item, timezone = timezone, onOpen = onOpen)
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsCard(
    item: NewsItem,
    timezone: String,
    onOpen: (NewsItem) -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Card(
        onClick = { onOpen(item) },
        modifier = Modifier
            .fillMaxWidth(),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image banner
            if (item.image.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.headline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Category and related tickers row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.category.isNotBlank()) {
                        Chip(text = item.category.uppercase())
                    }
                    if (item.related.isNotBlank()) {
                        val tickers = item.related.split(",").filter { it.isNotBlank() }
                        AnimatedVisibility(visible = tickers.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                tickers.take(3).forEach { t ->
                                    Chip(text = t.trim())
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = item.headline,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                SourceDateRow(
                    source = item.source,
                    epochSeconds = item.datetime,
                    timezone = timezone
                )

                if (item.summary.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SourceDateRow(
    source: String,
    epochSeconds: Long,
    timezone: String
) {
    val zone = remember(timezone) { ZoneId.of(timezone) }
    val dateText = remember(epochSeconds, timezone) {
        Instant.ofEpochSecond(epochSeconds)
            .atZone(zone)
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = source,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = "•",
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = dateText,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text("Loading latest news…", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text("No news found for this month.", style = MaterialTheme.typography.bodyMedium)
        Text("Try refreshing or adjusting your filters.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// --------- Utilities ---------

private fun openInCustomTab(context: Context, url: String) {
    try {
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        intent.launchUrl(context, Uri.parse(url))
    } catch (_: Throwable) {
        // Fallback if no browser
    }
}

