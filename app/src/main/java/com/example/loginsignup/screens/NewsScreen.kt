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
@OptIn(ExperimentalMaterial3Api::class) // Opt-in for using experimental Material 3 APIs
@Composable // Marks a function as a composable UI element
fun NewsScreen(
    nvm: NewsViewModel = viewModel(), // Injects the NewsViewModel
    timezone: String = "America/Chicago", // Sets a default timezone
) {
    val ctx = LocalContext.current // Gets the current Android context

    val newsItems by nvm.news.collectAsState() // Collects the list of news items as a state
    val isLoading by nvm.isLoading.collectAsState() // Collects the loading state
    Log.d("NewsScreen", "newsItems: $newsItems") // Logs the news items for debugging


    Scaffold( // A basic Material Design layout structure
        topBar = { // Defines the top app bar
            CenterAlignedTopAppBar( // A top app bar that centers the title
                title = { // The title of the app bar
                    Row(verticalAlignment = Alignment.CenterVertically) { // A row with vertically centered content
                        Icon( // Displays an icon
                            imageVector = Icons.Default.Public, // The icon to display
                            contentDescription = null, // No content description for decorative icon
                            modifier = Modifier // Modifier for styling
                                .size(22.dp) // Sets the size of the icon
                                .padding(end = 6.dp) // Adds padding to the end
                        )
                        Text("Market News", fontWeight = FontWeight.SemiBold) // The title text
                    }
                }
            )
        }
    ) { padding -> // Content of the screen
        Box( // A composable that positions its children relative to its edges
            modifier = Modifier // Modifier for styling
                .fillMaxSize() // Fills the maximum available size
                .padding(padding) // Applies padding from the Scaffold
        ) {
            if (isLoading) { // If the content is loading
                LoadingState() // Show the loading state UI
            }
            else { // If the content has finished loading
                if (newsItems.isEmpty()) // If there are no news items
                {
                    EmptyState() // Show the empty state UI
                }
                else { // If there are news items
                    NewsList( // Display the list of news items
                        items = newsItems, // The list of news items
                        timezone = timezone, // The timezone for date formatting
                        onOpen = { openInCustomTab(ctx, it.url) } // Lambda to open a news item in a custom tab
                    )
                }



        }
        }
    }
}

// --------- UI Pieces ---------

@Composable // Marks a function as a composable UI element
private fun NewsList(
    items: List<NewsItem>, // The list of news items to display
    timezone: String, // The timezone for date formatting
    onOpen: (NewsItem) -> Unit // Lambda to handle opening a news item
) {
    LazyColumn( // A vertically scrolling list that only composes and lays out the currently visible items
        modifier = Modifier.fillMaxSize(), // Fills the maximum available size
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp), // Adds padding to the content
        verticalArrangement = Arrangement.spacedBy(14.dp) // Adds spacing between items
    ) {
        items(items, key = { it.id }) { item -> // Defines the items in the list
            NewsCard(item = item, timezone = timezone, onOpen = onOpen) // A card for each news item
        }
        item { Spacer(Modifier.height(8.dp)) } // Adds extra space at the end of the list
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for using experimental Material 3 APIs
@Composable // Marks a function as a composable UI element
private fun NewsCard(
    item: NewsItem, // The news item to display
    timezone: String, // The timezone for date formatting
    onOpen: (NewsItem) -> Unit // Lambda to handle opening a news item
) {
    val shape = RoundedCornerShape(20.dp) // Defines a rounded corner shape for the card
    Card( // A Material Design card
        onClick = { onOpen(item) }, // Handles clicks on the card
        modifier = Modifier // Modifier for styling
            .fillMaxWidth(), // Fills the maximum available width
        shape = shape, // Sets the shape of the card
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Sets the elevation of the card
    ) {
        Column { // A vertically arranged layout
            // Image banner
            if (item.image.isNotBlank()) { // If the news item has an image
                AsyncImage( // A composable that executes an image request asynchronously and renders the result
                    model = ImageRequest.Builder(LocalContext.current) // Builds an image request
                        .data(item.image) // The URL of the image
                        .crossfade(true) // Enables crossfade animation
                        .build(), // Builds the request
                    contentDescription = item.headline, // Content description for accessibility
                    modifier = Modifier // Modifier for styling
                        .fillMaxWidth() // Fills the maximum available width
                        .height(180.dp) // Sets the height of the image
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)), // Clips the top corners of the image
                    contentScale = ContentScale.Crop // Scales the image to fill the container, cropping if necessary
                )
            }

            Column(modifier = Modifier.padding(16.dp)) { // A column with padding
                // Category and related tickers row
                Row( // A horizontally arranged layout
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Adds spacing between children
                    verticalAlignment = Alignment.CenterVertically // Vertically centers the children
                ) {
                    if (item.category.isNotBlank()) { // If the news item has a category
                        Chip(text = item.category.uppercase()) // Displays the category as a chip
                    }
                    if (item.related.isNotBlank()) { // If the news item has related tickers
                        val tickers = item.related.split(",").filter { it.isNotBlank() } // Splits the related tickers string into a list
                        AnimatedVisibility(visible = tickers.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) { // Animates the visibility of the tickers
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { // A row with spacing for the tickers
                                tickers.take(3).forEach { t -> // Displays up to 3 tickers
                                    Chip(text = t.trim()) // Displays each ticker as a chip
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp)) // Adds vertical space

                Text( // Displays the headline of the news item
                    text = item.headline, // The headline text
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), // Sets the text style
                    maxLines = 3, // Sets the maximum number of lines
                    overflow = TextOverflow.Ellipsis // Adds an ellipsis if the text overflows
                )

                Spacer(Modifier.height(6.dp)) // Adds vertical space

                SourceDateRow( // Displays the source and date of the news item
                    source = item.source, // The source of the news item
                    epochSeconds = item.datetime, // The date of the news item in epoch seconds
                    timezone = timezone // The timezone for date formatting
                )

                if (item.summary.isNotBlank()) { // If the news item has a summary
                    Spacer(Modifier.height(10.dp)) // Adds vertical space
                    Text( // Displays the summary of the news item
                        text = item.summary, // The summary text
                        style = MaterialTheme.typography.bodyMedium, // Sets the text style
                        maxLines = 4, // Sets the maximum number of lines
                        overflow = TextOverflow.Ellipsis // Adds an ellipsis if the text overflows
                    )
                }
            }
        }
    }
}

@Composable // Marks a function as a composable UI element
private fun Chip(text: String) { // A composable for displaying a chip
    Box( // A composable that positions its children relative to its edges
        modifier = Modifier // Modifier for styling
            .clip(RoundedCornerShape(999.dp)) // Clips the chip to a rounded shape
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)) // Sets the background color of the chip
            .padding(horizontal = 10.dp, vertical = 6.dp), // Adds padding to the chip
        contentAlignment = Alignment.Center // Centers the content of the chip
    ) {
        Text( // Displays the text of the chip
            text = text, // The text to display
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium), // Sets the text style
            color = MaterialTheme.colorScheme.primary // Sets the text color
        )
    }
}

@Composable // Marks a function as a composable UI element
private fun SourceDateRow(
    source: String, // The source of the news item
    epochSeconds: Long, // The date of the news item in epoch seconds
    timezone: String // The timezone for date formatting
) {
    val zone = remember(timezone) { ZoneId.of(timezone) } // Remembers the ZoneId for the given timezone
    val dateText = remember(epochSeconds, timezone) { // Remembers the formatted date text
        Instant.ofEpochSecond(epochSeconds) // Creates an Instant from the epoch seconds
            .atZone(zone) // Converts the Instant to a ZonedDateTime in the given timezone
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)) // Formats the date and time
    }

    Row( // A horizontally arranged layout
        verticalAlignment = Alignment.CenterVertically, // Vertically centers the children
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Adds spacing between the children
    ) {
        Box( // A composable that positions its children relative to its edges
            modifier = Modifier // Modifier for styling
                .size(8.dp) // Sets the size of the box
                .clip(CircleShape) // Clips the box to a circle shape
                .background(MaterialTheme.colorScheme.primary) // Sets the background color of the box
        )
        Text( // Displays the source of the news item
            text = source, // The source text
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold) // Sets the text style
        )
        Text( // Displays a separator dot
            text = "•", // The dot character
            style = MaterialTheme.typography.labelLarge // Sets the text style
        )
        Text( // Displays the formatted date
            text = dateText, // The date text
            style = MaterialTheme.typography.labelLarge, // Sets the text style
            color = MaterialTheme.colorScheme.onSurfaceVariant // Sets the text color
        )
    }
}

@Composable // Marks a function as a composable UI element
private fun LoadingState() { // A composable for displaying a loading indicator
    Column( // A vertically arranged layout
        modifier = Modifier // Modifier for styling
            .fillMaxSize() // Fills the maximum available size
            .padding(24.dp), // Adds padding
        verticalArrangement = Arrangement.Center, // Centers the content vertically
        horizontalAlignment = Alignment.CenterHorizontally // Centers the content horizontally
    ) {
        CircularProgressIndicator() // A circular progress indicator
        Spacer(Modifier.height(16.dp)) // Adds vertical space
        Text("Loading latest news…", style = MaterialTheme.typography.bodyMedium) // A text message indicating that news is loading
    }
}

@Composable // Marks a function as a composable UI element
private fun EmptyState() { // A composable for displaying an empty state
    Column( // A vertically arranged layout
        modifier = Modifier // Modifier for styling
            .fillMaxSize() // Fills the maximum available size
            .padding(24.dp), // Adds padding
        verticalArrangement = Arrangement.Center, // Centers the content vertically
        horizontalAlignment = Alignment.CenterHorizontally // Centers the content horizontally
    ) {
        Icon( // An icon to indicate an error
            imageVector = Icons.Outlined.ErrorOutline, // The error icon
            contentDescription = null, // No content description for a decorative icon
            modifier = Modifier.size(40.dp) // Sets the size of the icon
        )
        Spacer(Modifier.height(10.dp)) // Adds vertical space
        Text("No news found for this month.", style = MaterialTheme.typography.bodyMedium) // A message indicating that no news was found
        Text("Try refreshing or adjusting your filters.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) // A suggestion for the user
    }
}

// --------- Utilities ---------

private fun openInCustomTab(context: Context, url: String) { // A utility function to open a URL in a custom tab
    try { // Tries to open the URL in a custom tab
        val intent = CustomTabsIntent.Builder() // Builds a CustomTabsIntent
            .setShowTitle(true) // Shows the title of the page in the custom tab
            .build() // Builds the intent
        intent.launchUrl(context, Uri.parse(url)) // Launches the custom tab
    } catch (_: Throwable) { // Catches any exceptions
        // Fallback if no browser
    }
}

