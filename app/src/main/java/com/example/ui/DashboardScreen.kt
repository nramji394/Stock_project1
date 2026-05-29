@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Candle
import com.example.data.ScreenerPreset
import com.example.data.StockTicker
import com.example.data.TradeJournalEntry
import com.example.data.StockDataGenerator
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: StockViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val tickers by viewModel.tickers.collectAsStateWithLifecycle()
    val selectedTicker by viewModel.selectedTicker.collectAsStateWithLifecycle()
    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ObsidianDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(TechBlue)
                        ) {
                            Icon(
                                imageOfSveIcon(),
                                contentDescription = "Logo",
                                tint = Color(0xFF381E72),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "SwingScope",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = "ARCHITECT EDITION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextSecondary,
                                    letterSpacing = 1.2.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    selectedTicker?.let { ticker ->
                        val isStarred = watchlist.any { it.ticker == ticker.ticker }
                        IconButton(onClick = { viewModel.toggleWatchlist(ticker) }) {
                            Icon(
                                imageVector = if (isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Add to Watchlist",
                                tint = if (isStarred) SoftGold else TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceSlate,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                ),
                modifier = Modifier.drawBehind {
                    // Underline border
                    drawLine(
                        color = ElementCard,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceSlate,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.drawBehind {
                    // Topline border
                    drawLine(
                        color = ElementCard,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 2f
                    )
                }
            ) {
                NavigationBarItem(
                    selected = activeTab == "screener",
                    onClick = { viewModel.activeTab.value = "screener" },
                    icon = { Icon(Icons.Outlined.FilterList, contentDescription = "Screener") },
                    label = { Text("Screener", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TechBlue,
                        selectedTextColor = TechBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = ElementCard
                    ),
                    modifier = Modifier.testTag("nav_screener")
                )
                NavigationBarItem(
                    selected = activeTab == "chart_view",
                    onClick = { viewModel.activeTab.value = "chart_view" },
                    icon = { Icon(Icons.Outlined.ShowChart, contentDescription = "Chart") },
                    label = { Text("Chart", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TechBlue,
                        selectedTextColor = TechBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = ElementCard
                    ),
                    modifier = Modifier.testTag("nav_chart")
                )
                NavigationBarItem(
                    selected = activeTab == "catalysts",
                    onClick = { viewModel.activeTab.value = "catalysts" },
                    icon = { Icon(Icons.Outlined.Business, contentDescription = "Catalysts") },
                    label = { Text("Catalysts", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TechBlue,
                        selectedTextColor = TechBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = ElementCard
                    ),
                    modifier = Modifier.testTag("nav_catalysts")
                )
                NavigationBarItem(
                    selected = activeTab == "journal",
                    onClick = { viewModel.activeTab.value = "journal" },
                    icon = { Icon(Icons.Outlined.Book, contentDescription = "Journal") },
                    label = { Text("Journal", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TechBlue,
                        selectedTextColor = TechBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = ElementCard
                    ),
                    modifier = Modifier.testTag("nav_journal")
                )
                NavigationBarItem(
                    selected = activeTab == "roadmap",
                    onClick = { viewModel.activeTab.value = "roadmap" },
                    icon = { Icon(Icons.Outlined.Architecture, contentDescription = "Roadmap") },
                    label = { Text("Roadmap", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TechBlue,
                        selectedTextColor = TechBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = ElementCard
                    ),
                    modifier = Modifier.testTag("nav_roadmap")
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = activeTab,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "tab_fade",
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) { tab ->
            when (tab) {
                "screener" -> ScreenerTab(viewModel)
                "chart_view" -> ChartViewTab(viewModel)
                "catalysts" -> CatalystsTab(viewModel)
                "journal" -> JournalTab(viewModel)
                "roadmap" -> RoadmapTab()
            }
        }
    }
}

@Composable
fun imageOfSveIcon() = Icons.Outlined.TrendingUp

// --- TAB 1: SCREENER & SCANNER ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScreenerTab(viewModel: StockViewModel) {
    val context = LocalContext.current
    val filteredList by viewModel.filteredTickers.collectAsStateWithLifecycle(emptyList())
    val selectedTicker by viewModel.selectedTicker.collectAsStateWithLifecycle()
    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()
    val presets by viewModel.customScreenerPresets.collectAsStateWithLifecycle()

    val sectorFilter by viewModel.selectedSectorFilter.collectAsStateWithLifecycle()
    val minRsi by viewModel.minRsiFilter.collectAsStateWithLifecycle()
    val maxRsi by viewModel.maxRsiFilter.collectAsStateWithLifecycle()
    val above50Sma by viewModel.above50SmaFilter.collectAsStateWithLifecycle()
    val overSoldOnly by viewModel.rsiOversoldFilter.collectAsStateWithLifecycle()
    val overBoughtOnly by viewModel.rsiOverboughtFilter.collectAsStateWithLifecycle()
    val squeezeOnly by viewModel.squeezePotentialFilter.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Quick Watchlist Bar if items are added
        if (watchlist.isNotEmpty()) {
            Text(
                text = "⭐️ MY WATCHLIST",
                style = MaterialTheme.typography.labelMedium,
                color = SoftGold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                items(watchlist) { watchStock ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
                        onClick = { 
                            // Select ticker
                            val fullTicker = StockDataGenerator.generateStockTickers().find { it.ticker == watchStock.ticker }
                            if (fullTicker != null) {
                                viewModel.selectTicker(fullTicker)
                                viewModel.activeTab.value = "chart_view"
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ElementCard),
                        modifier = Modifier
                            .width(130.dp)
                            .testTag("watchlist_item_${watchStock.ticker}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(watchStock.ticker, fontWeight = FontWeight.Bold, color = TextPrimary)
                                val isUp = watchStock.changePercent >= 0
                                Icon(
                                    imageVector = if (isUp) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = if (isUp) BullishGreen else BearishRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(watchStock.name, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("₹${watchStock.price}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                val changeColor = if (watchStock.changePercent >= 0) BullishGreen else BearishRed
                                Text(
                                    text = "${if (watchStock.changePercent >= 0) "+" else ""}${String.format("%.2f", watchStock.changePercent)}%",
                                    fontSize = 11.sp,
                                    color = changeColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Screener Title / Header
        Text(
            text = "Tactical Screener & Scanners",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Sift technical patterns and catalyst indicators over active lists.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Prebuilt Strategy Presets Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🎯 PREBUILT SWING SCREENS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TechBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presets.forEach { preset ->
                        FilterChip(
                            selected = false,
                            onClick = { viewModel.applyPreset(preset) },
                            label = { Text(preset.name, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = ElementCard,
                                labelColor = TextPrimary
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.FlashOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = SoftGold
                                )
                            }
                        )
                    }
                }
            }
        }

        // Dynamic Filtering Console Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎛️ FILTERS CONSOLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(
                        onClick = { viewModel.clearScreenerFilters() },
                        colors = ButtonDefaults.textButtonColors(contentColor = BearishRed)
                    ) {
                        Text("Reset All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Sector Selector Row
                Text(
                    text = "Sector Focus",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                ScrollableTabRow(
                    selectedTabIndex = when(sectorFilter) {
                        "All" -> 0
                        "Technology" -> 1
                        "Consumer Cyclical" -> 2
                        "Financials" -> 3
                        else -> 0
                    },
                    containerColor = Color.Transparent,
                    edgePadding = 0.dp,
                    indicator = {},
                    divider = {},
                    modifier = Modifier.height(38.dp)
                ) {
                    listOf("All", "Technology", "Consumer Cyclical", "Financials").forEach { sec ->
                        val isSelected = sectorFilter == sec
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) BullishGreen else ElementCard
                            ),
                            shape = RoundedCornerShape(8.dp),
                            onClick = { viewModel.selectedSectorFilter.value = sec },
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .height(32.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 14.dp)
                            ) {
                                Text(
                                    text = sec,
                                    color = if (isSelected) ObsidianDark else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // RSI Range Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RSI Limits (Cooling to Overbought)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "${minRsi.toInt()} - ${maxRsi.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BullishGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                RangeSlider(
                    value = minRsi..maxRsi,
                    onValueChange = { range ->
                        viewModel.minRsiFilter.value = range.start
                        viewModel.maxRsiFilter.value = range.endInclusive
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = BullishGreen,
                        inactiveTrackColor = ElementCard,
                        thumbColor = BullishGreen
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Boolean filter toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = above50Sma,
                        onClick = { viewModel.above50SmaFilter.value = !above50Sma },
                        label = { Text("Above 50 SMA", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BullishGreen,
                            selectedLabelColor = ObsidianDark,
                            containerColor = ElementCard,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = squeezeOnly,
                        onClick = { viewModel.squeezePotentialFilter.value = !squeezeOnly },
                        label = { Text("Short Squeeze Setup", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SoftGold,
                            selectedLabelColor = ObsidianDark,
                            containerColor = ElementCard,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = overSoldOnly,
                        onClick = { 
                            viewModel.rsiOversoldFilter.value = !overSoldOnly
                            if(overSoldOnly) {
                                viewModel.minRsiFilter.value = 0f
                                viewModel.maxRsiFilter.value = 40f
                            }
                        },
                        label = { Text("Oversold (RSI < 40)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElementCard,
                            selectedLabelColor = BullishGreen,
                            containerColor = ElementCard,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = overBoughtOnly,
                        onClick = { 
                            viewModel.rsiOverboughtFilter.value = !overBoughtOnly
                            if(overBoughtOnly) {
                                viewModel.minRsiFilter.value = 70f
                                viewModel.maxRsiFilter.value = 100f
                            }
                        },
                        label = { Text("Momentum (RSI > 70)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElementCard,
                            selectedLabelColor = BearishRed,
                            containerColor = ElementCard,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Screener Results count banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📊 SCREENER MATCHES (${filteredList.size})",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap a stock to chart of trade",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // List of stock items
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No setups match current filters.",
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Try easing RSI constraints or choosing 'All' sectors.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            filteredList.forEach { stock ->
                val isSelected = selectedTicker?.ticker == stock.ticker
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ElementCard else SurfaceSlate
                    ),
                    shape = RoundedCornerShape(14.dp),
                    onClick = { viewModel.selectTicker(stock) },
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) TechBlue else ElementCard
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("screener_item_${stock.ticker}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(ElementCard, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = stock.ticker,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stock.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${stock.sector} • ${stock.industry}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Price and Pill
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${stock.price}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                )
                                val isUp = stock.changePercent >= 0
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (isUp) BullishGreen.copy(alpha = 0.15f) else BearishRed.copy(
                                                alpha = 0.15f
                                            ),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${if (isUp) "▲" else "▼"} ${String.format("%.2f", stock.changePercent)}%",
                                        fontSize = 11.sp,
                                        color = if (isUp) BullishGreen else BearishRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = ElementCard.copy(alpha = 0.4f))

                        // Technical Signals row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Speed, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("RSI: ", fontSize = 12.sp, color = TextSecondary)
                                val rsiColor = if (stock.rsi <= 40) BullishGreen else if (stock.rsi >= 70) BearishRed else TextPrimary
                                Text(
                                    text = String.format("%.1f", stock.rsi),
                                    fontSize = 12.sp,
                                    color = rsiColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.CompassCalibration, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("50 SMA: ", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    text = if (stock.above50Sma) "Above" else "Below",
                                    fontSize = 12.sp,
                                    color = if (stock.above50Sma) BullishGreen else BearishRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Compress, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Short Int: ", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    text = "${stock.shortInterest}%",
                                    fontSize = 12.sp,
                                    color = if (stock.shortInterest >= 5.0) SoftGold else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- TAB 2: TECHNICAL CHARTS CODES ---
@Composable
fun ChartViewTab(viewModel: StockViewModel) {
    val selectedTicker by viewModel.selectedTicker.collectAsStateWithLifecycle()
    val timeframe by viewModel.selectedTimeframe.collectAsStateWithLifecycle()
    val candles by viewModel.selectedCandles.collectAsStateWithLifecycle()

    val showEma10Val by viewModel.showEma10.collectAsStateWithLifecycle()
    val showEma20Val by viewModel.showEma20.collectAsStateWithLifecycle()
    val showSma50Val by viewModel.showSma50.collectAsStateWithLifecycle()
    val showBbVal by viewModel.showBollingerBands.collectAsStateWithLifecycle()
    val showVpVal by viewModel.showVolumeProfile.collectAsStateWithLifecycle()

    // Calculator values
    val acctSize by viewModel.calcAccountSize.collectAsStateWithLifecycle()
    val riskPct by viewModel.calcRiskPercent.collectAsStateWithLifecycle()
    val entryPrice by viewModel.calcEntryPrice.collectAsStateWithLifecycle()
    val stopLoss by viewModel.calcStopLoss.collectAsStateWithLifecycle()
    val targetPrice by viewModel.calcTargetPrice.collectAsStateWithLifecycle()

    val sharesResult by viewModel.calculatedShares.collectAsStateWithLifecycle()
    val posSizeResult by viewModel.calculatedPositionSize.collectAsStateWithLifecycle()
    val riskCashResult by viewModel.calculatedRiskAmount.collectAsStateWithLifecycle()
    val r2rResult by viewModel.calculatedRewardToRisk.collectAsStateWithLifecycle()

    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()

    if (selectedTicker == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a stock on the Screener tab first.", color = TextSecondary, fontWeight = FontWeight.Bold)
        }
        return
    }

    val activeStock = selectedTicker!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Active Stock header card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = activeStock.ticker,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val isStarred = watchlist.any { it.ticker == activeStock.ticker }
                    Icon(
                        imageVector = if (isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Watchlist status",
                        tint = if (isStarred) SoftGold else TextSecondary,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { viewModel.toggleWatchlist(activeStock) }
                    )
                }
                Text(
                    text = activeStock.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${activeStock.price}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black
                )
                val isUp = activeStock.changePercent >= 0
                Text(
                    text = "${if (isUp) "+" else ""}${String.format("%.2f", activeStock.changePercent)}%",
                    color = if (isUp) BullishGreen else BearishRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Timeframe selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("1H", "4H", "Daily", "Weekly").forEach { tf ->
                val isSelected = timeframe == tf
                InputChip(
                    selected = isSelected,
                    onClick = { viewModel.setTimeframe(tf) },
                    label = { Text(tf, fontSize = 12.sp) },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = BullishGreen,
                        selectedLabelColor = ObsidianDark,
                        containerColor = SurfaceSlate,
                        labelColor = TextPrimary
                    )
                )
            }
        }

        // Custom Stock Chart Drawing Area (Card container)
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                if (candles.isNotEmpty()) {
                    StockChart(
                        candles = candles,
                        supportZone = activeStock.supportZone.toFloat(),
                        resistanceZone = activeStock.resistanceZone.toFloat(),
                        showEma10 = showEma10Val,
                        showEma20 = showEma20Val,
                        showSma50 = showSma50Val,
                        showBb = showBbVal,
                        showVp = showVpVal,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BullishGreen)
                    }
                }
            }
        }

        // Chart Indicator Toggles Panel
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Chart Indicators & Overlays",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    maxItemsInEachRow = 3,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IndicatorCheckbox("EMA 10 (Orange)", showEma10Val) { viewModel.showEma10.value = it }
                    IndicatorCheckbox("EMA 20 (Cyan)", showEma20Val) { viewModel.showEma20.value = it }
                    IndicatorCheckbox("SMA 50 (Blue)", showSma50Val) { viewModel.showSma50.value = it }
                    IndicatorCheckbox("B-Bands", showBbVal) { viewModel.showBollingerBands.value = it }
                    IndicatorCheckbox("Volume Profile", showVpVal) { viewModel.showVolumeProfile.value = it }
                }
            }
        }

        // POSITION SIZER AND RISK CALCULATOR CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📐 Tactical Risk & Position Size Calculator",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "A critical rule of swing trading: never risk more than 1-2% of capital on a single run.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // Input rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = acctSize,
                        onValueChange = { viewModel.calcAccountSize.value = it },
                        label = { Text("Account Size (₹)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBlue,
                            focusedLabelColor = TechBlue,
                            unfocusedBorderColor = ElementCard,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = ElementCard,
                            unfocusedContainerColor = ElementCard
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = riskPct,
                        onValueChange = { viewModel.calcRiskPercent.value = it },
                        label = { Text("Risk % (e.g. 1.5)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBlue,
                            focusedLabelColor = TechBlue,
                            unfocusedBorderColor = ElementCard,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = ElementCard,
                            unfocusedContainerColor = ElementCard
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = entryPrice,
                        onValueChange = { viewModel.calcEntryPrice.value = it },
                        label = { Text("Entry Price (₹)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBlue,
                            focusedLabelColor = TechBlue,
                            unfocusedBorderColor = ElementCard,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = ElementCard,
                            unfocusedContainerColor = ElementCard
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stopLoss,
                        onValueChange = { viewModel.calcStopLoss.value = it },
                        label = { Text("Stop Loss (₹)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BearishRed,
                            focusedLabelColor = BearishRed,
                            unfocusedBorderColor = ElementCard,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = ElementCard,
                            unfocusedContainerColor = ElementCard
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetPrice,
                        onValueChange = { viewModel.calcTargetPrice.value = it },
                        label = { Text("Target Price (₹)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBlue,
                            focusedLabelColor = TechBlue,
                            unfocusedBorderColor = ElementCard,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = ElementCard,
                            unfocusedContainerColor = ElementCard
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calculations Visual Results
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = TechBlue,
                        contentColor = Color(0xFF381E72)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.Calculate,
                                    contentDescription = null,
                                    tint = Color(0xFF381E72),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "POSITION SIZER RESULT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF381E72)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF381E72))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE RULE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("SHARES TO BUY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF381E72).copy(alpha = 0.7f))
                                Text(
                                    text = "$sharesResult Units",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF381E72)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TOTAL VALUE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF381E72).copy(alpha = 0.7f))
                                Text(
                                    text = "₹${String.format("%.2f", posSizeResult)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF381E72)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF381E72).copy(alpha = 0.15f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("CAPITAL AT RISK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF381E72).copy(alpha = 0.7f))
                                Text(
                                    text = "₹${String.format("%.2f", riskCashResult)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF381E72)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("R:R RATIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF381E72).copy(alpha = 0.7f))
                                Text(
                                    text = "1 : ${String.format("%.1f", r2rResult)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF381E72)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Log Trade action button
                var showLogDialog by remember { mutableStateOf(false) }
                Button(
                    onClick = { showLogDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BullishGreen),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = ObsidianDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Register Swing Setup to Trade Journal", color = ObsidianDark, fontWeight = FontWeight.Black)
                }

                // Add to trade log Dialog
                if (showLogDialog) {
                    var strategyInput by remember { mutableStateOf("EMA Pullback Breakout") }
                    var notesInput by remember { mutableStateOf("") }

                    AlertDialog(
                        onDismissRequest = { showLogDialog = false },
                        containerColor = SurfaceSlate,
                        title = { Text("Log Setup for ${activeStock.ticker}", color = TextPrimary) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Choose the strategy confluence and record trading remarks:", color = TextSecondary, fontSize = 12.sp)
                                OutlinedTextField(
                                    value = strategyInput,
                                    onValueChange = { strategyInput = it },
                                    label = { Text("Trading Strategy", color = TextSecondary) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BullishGreen,
                                        unfocusedBorderColor = ElementCard,
                                        focusedLabelColor = BullishGreen
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = notesInput,
                                    onValueChange = { notesInput = it },
                                    label = { Text("Confluence & Notes", color = TextSecondary) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BullishGreen,
                                        unfocusedBorderColor = ElementCard,
                                        focusedLabelColor = BullishGreen
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.logSwingTrade(strategyInput, notesInput)
                                    showLogDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BullishGreen)
                            ) {
                                Text("Save Trade", color = ObsidianDark, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLogDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun IndicatorCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = BullishGreen,
                checkmarkColor = ObsidianDark,
                uncheckedColor = TextSecondary
            )
        )
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
    }
}

// Custom Stock Chart Canvas
@Composable
fun StockChart(
    candles: List<Candle>,
    supportZone: Float,
    resistanceZone: Float,
    showEma10: Boolean,
    showEma20: Boolean,
    showSma50: Boolean,
    showBb: Boolean,
    showVp: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val candleCount = candles.size
        
        if (candleCount == 0) return@Canvas

        val minPrice = candles.minOf { it.low } * 0.99f
        val maxPrice = candles.maxOf { it.high } * 1.01f
        val priceDiff = maxPrice - minPrice

        val candleWidth = width / candleCount
        val innerHeight = height * 0.82f // save room at bottom for charts volume profile or timestamps

        // Helper price scaler
        fun getYForPrice(price: Float): Float {
            val ratio = (price - minPrice) / priceDiff
            return height * 0.85f - (ratio * innerHeight)
        }

        // 1. Bollinger Bands Shading (Lower Layer)
        if (showBb) {
            val bbPath = androidx.compose.ui.graphics.Path()
            var firstPt = true
            
            // Forward Upper Band
            for (i in candles.indices) {
                val x = (i * candleWidth) + (candleWidth / 2f)
                val y = getYForPrice(candles[i].bbUpper)
                if (firstPt) {
                    bbPath.moveTo(x, y)
                    firstPt = false
                } else {
                    bbPath.lineTo(x, y)
                }
            }
            // Backward Lower Band
            for (i in candles.indices.reversed()) {
                val x = (i * candleWidth) + (candleWidth / 2f)
                val y = getYForPrice(candles[i].bbLower)
                bbPath.lineTo(x, y)
            }
            bbPath.close()
            drawPath(
                path = bbPath,
                color = TechBlue.copy(alpha = 0.08f)
            )
        }

        // 2. Structural Support & Resistance Dashed Lines
        val supY = getYForPrice(supportZone)
        val resY = getYForPrice(resistanceZone)

        drawLine(
            color = BearishRed.copy(alpha = 0.5f),
            start = Offset(0f, supY),
            end = Offset(width, supY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )
        drawLine(
            color = BullishGreen.copy(alpha = 0.5f),
            start = Offset(0f, resY),
            end = Offset(width, resY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )

        // 3. Draw Volume Profile on Horizontal Left (If toggled)
        if (showVp) {
            val maxProfileVol = 12f // Normalized units
            val activeTickerCandles = candles.take(12) // sample range
            val blockHeight = height * 0.65f / 12f

            for (idx in activeTickerCandles.indices) {
                val volRatio = (idx % 4 + 2f) / 6f // simulate realistic visual peaks
                val profileWidth = volRatio * (width * 0.22f)
                val y = height * 0.1f + (idx * blockHeight)
                
                drawRect(
                    color = TechBlue.copy(alpha = 0.12f),
                    topLeft = Offset(0f, y),
                    size = Size(profileWidth, blockHeight - 2f)
                )
            }
        }

        // 4. Draw Candlesticks & Indicators (Wicks + Bodies)
        for (i in candles.indices) {
            val candle = candles[i]
            val x = i * candleWidth
            val centerX = x + (candleWidth / 2f)

            val openY = getYForPrice(candle.open)
            val closeY = getYForPrice(candle.close)
            val highY = getYForPrice(candle.high)
            val lowY = getYForPrice(candle.low)

            val isBullish = candle.close >= candle.open
            val color = if (isBullish) BullishGreen else BearishRed

            // Draw Wick Line
            drawLine(
                color = color,
                start = Offset(centerX, highY),
                end = Offset(centerX, lowY),
                strokeWidth = 2f
            )

            // Draw Body Rect
            val rectTop = Math.min(openY, closeY)
            val rectBottom = Math.max(openY, closeY)
            val rectHeight = (rectBottom - rectTop).coerceAtLeast(2f)
            val bodyWidth = (candleWidth * 0.72f).coerceAtLeast(3f)

            drawRect(
                color = color,
                topLeft = Offset(centerX - (bodyWidth / 2f), rectTop),
                size = Size(bodyWidth, rectHeight)
            )
        }

        // 5. Technical Moving Averages Plots (Overlaid)
        if (showEma10) {
            for (i in 0 until candleCount - 1) {
                val x1 = (i * candleWidth) + (candleWidth / 2f)
                val y1 = getYForPrice(candles[i].ema10)
                val x2 = ((i + 1) * candleWidth) + (candleWidth / 2f)
                val y2 = getYForPrice(candles[i + 1].ema10)

                drawLine(color = SoftGold, start = Offset(x1, y1), end = Offset(x2, y2), strokeWidth = 3.5f)
            }
        }

        if (showEma20) {
            for (i in 0 until candleCount - 1) {
                val x1 = (i * candleWidth) + (candleWidth / 2f)
                val y1 = getYForPrice(candles[i].ema20)
                val x2 = ((i + 1) * candleWidth) + (candleWidth / 2f)
                val y2 = getYForPrice(candles[i + 1].ema20)

                drawLine(color = Color(0xFF00E5FF), start = Offset(x1, y1), end = Offset(x2, y2), strokeWidth = 3.5f)
            }
        }

        if (showSma50) {
            for (i in 0 until candleCount - 1) {
                val x1 = (i * candleWidth) + (candleWidth / 2f)
                val y1 = getYForPrice(candles[i].sma50)
                val x2 = ((i + 1) * candleWidth) + (candleWidth / 2f)
                val y2 = getYForPrice(candles[i + 1].sma50)

                drawLine(color = TechBlue, start = Offset(x1, y1), end = Offset(x2, y2), strokeWidth = 3.5f)
            }
        }
    }
}


// --- TAB 3: CATS & AI ASSISTANT ---
@Composable
fun CatalystsTab(viewModel: StockViewModel) {
    val selectedTicker by viewModel.selectedTicker.collectAsStateWithLifecycle()
    val timeframe by viewModel.selectedTimeframe.collectAsStateWithLifecycle()
    val copilotResponse by viewModel.copilotResponse.collectAsStateWithLifecycle()
    val isCopilotLoading by viewModel.isCopilotLoading.collectAsStateWithLifecycle()
    val isUsingFallback by viewModel.isUsingFallback.collectAsStateWithLifecycle()

    if (selectedTicker == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a stock on the Screener tab first.", color = TextSecondary, fontWeight = FontWeight.Bold)
        }
        return
    }

    val activeStock = selectedTicker!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Catalysts & Fundamental Metrics",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Earnings timelines, volume spikes, and short interest metrics.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Catalyst Metrics dashboard (Columns layout)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CatalystMetricItem("P/E Ratio", activeStock.peRatio.toString(), "PE Valuation", modifier = Modifier.weight(1f))
            CatalystMetricItem("PEG Ratio", activeStock.pegRatio.toString(), "Value Adjusted Growth", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CatalystMetricItem("Price-to-Sales", activeStock.priceToSales.toString(), "Sales Multiplier", modifier = Modifier.weight(1f))
            CatalystMetricItem("Debt-to-Equity", activeStock.debtToEquity.toString(), "Sovereign Risk Level", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Institutional & Squeeze Sizer Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💼 Shareholder Structure & Squeeze Torque",
                    style = MaterialTheme.typography.labelMedium,
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Institutional Ownership", fontSize = 11.sp, color = TextSecondary)
                        Text("${activeStock.institutionalOwnership}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Short Interest Ratio", fontSize = 11.sp, color = TextSecondary)
                        val shortColor = if (activeStock.shortInterest >= 5.0) SoftGold else TextPrimary
                        Text("${activeStock.shortInterest}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = shortColor)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Days to Cover Shorts", fontSize = 11.sp, color = TextSecondary)
                        Text("${activeStock.daysToCover} Days", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Relative Sector Rank", fontSize = 11.sp, color = TextSecondary)
                        Text("Rank ${activeStock.relativeStrengthRank}/100", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BullishGreen)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upcoming Earnings Surprise History Timeline
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📅 Upcoming Catalyst: ${activeStock.nextEarningsDate}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Historical Earnings EPS Surprises",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Render earnings lists
                activeStock.historicalEarningsSurprise.forEach { surprise ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = surprise.quarter,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Est: ${String.format("%.2f", surprise.epsEstimate)}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "Act: ${String.format("%.2f", surprise.epsActual)}",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val isPositive = surprise.surprisePercent >= 0
                        Text(
                            text = "${if (isPositive) "+" else ""}${String.format("%.1f", surprise.surprisePercent)}%",
                            fontSize = 12.sp,
                            color = if (isPositive) BullishGreen else BearishRed,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // AI SWING COPRILOT REPORT AREA
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, TechBlue.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = TechBlue, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gemini AI Tactical Copilot",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Generate an institutional quality Swing Setup Trade Plan instantly based on indicators.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = { viewModel.askCopilotSetup(activeStock, timeframe) },
                    enabled = !isCopilotLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = TechBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_generate_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isCopilotLoading) {
                        CircularProgressIndicator(color = ObsidianDark, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Draft Tactical Swing Trade Setup Report", color = Color(0xFF381E72), fontWeight = FontWeight.Black)
                    }
                }

                if (copilotResponse.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (isUsingFallback) {
                        Box(
                            modifier = Modifier
                                .background(SoftGold.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .border(1.dp, SoftGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "💡 Connected to local technical evaluation formulas. Connect your GEMINI_API_KEY inside the Secrets panel to activate neural catalyst sentiment scans.",
                                fontSize = 11.sp,
                                color = SoftGold,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Copilot output block
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ElementCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(14.dp)
                                .verticalScroll(rememberScrollState(), enabled = false)
                        ) {
                            Text(
                                text = copilotResponse,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp,
                                modifier = Modifier.testTag("copilot_response_text")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CatalystMetricItem(title: String, score: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ElementCard),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = TextSecondary)
            Text(score, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(subtitle, fontSize = 10.sp, color = TextSecondary)
        }
    }
}


// --- TAB 4: TRADE JOURNAL LISTS ---
@Composable
fun JournalTab(viewModel: StockViewModel) {
    val trades by viewModel.tradeLogs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Swing Trade Journal Ledger",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Monitor open tactical structures, log closed trades, and review historic win ratios.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Stat overview boxes
        val totalTradesCount = trades.size
        val activeTrades = trades.filter { !it.isClosed }
        val closedTrades = trades.filter { it.isClosed }
        val wonTrades = closedTrades.filter { it.exitPrice > it.entryPrice }
        val winRate = if (closedTrades.isNotEmpty()) (wonTrades.size.toFloat() / closedTrades.size.toFloat() * 100).toInt() else 0
        val totalPnL = closedTrades.sumOf { (it.exitPrice - it.entryPrice) * it.quantity }

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💼 TACTICAL HEALTH METRICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    JournalOverviewSquare("Total Logs", totalTradesCount.toString(), "Saves in DB")
                    JournalOverviewSquare("Active Trades", activeTrades.size.toString(), "Holding")
                    JournalOverviewSquare("Win Rate", "$winRate%", "${wonTrades.size}/${closedTrades.size}")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cash Profit Indicator banner
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (totalPnL >= 0) BullishGreen.copy(alpha = 0.08f) else BearishRed.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Historic Locked Profit/Loss: ", fontSize = 12.sp, color = TextPrimary)
                        Text(
                            text = "${if (totalPnL >= 0) "+" else ""}₹${String.format("%.2f", totalPnL)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (totalPnL >= 0) BullishGreen else BearishRed
                        )
                    }
                }
            }
        }

        // Active Trade Setups List
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.HourglassEmpty, contentDescription = null, tint = SoftGold, modifier = Modifier.size(16.dp))
            Text(
                text = "Tactical Active Positions (${activeTrades.size})",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black
            )
        }

        if (activeTrades.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No active tactical swing positions. Save trades inside standard charts.", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            activeTrades.forEach { trade ->
                var closeTradeDialogId by remember { mutableStateOf<Int?>(null) }
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, ElementCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("active_trade_${trade.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(trade.ticker, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(ElementCard, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(trade.strategy, fontSize = 10.sp, color = SoftGold, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("Quantity: ${trade.quantity} units", fontSize = 11.sp, color = TextSecondary)
                            }

                            // Close Trade action
                            Button(
                                onClick = { closeTradeDialogId = trade.id },
                                colors = ButtonDefaults.buttonColors(containerColor = BullishGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Close Trade", fontSize = 11.sp, color = ObsidianDark, fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = ElementCard.copy(alpha = 0.4f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Entry Trigger", fontSize = 11.sp, color = TextSecondary)
                                Text("₹${trade.entryPrice}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column {
                                Text("Stop loss Limit", fontSize = 11.sp, color = TextSecondary)
                                Text("₹${trade.stopLoss}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BearishRed)
                            }
                            Column {
                                Text("Profit Target", fontSize = 11.sp, color = TextSecondary)
                                Text("₹${trade.targetPrice}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BullishGreen)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Reward : Risk", fontSize = 11.sp, color = TextSecondary)
                                Text("${String.format("%.1f", trade.rewardToRiskRatio)}:1", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                        }

                        if (trade.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .background(ElementCard, RoundedCornerShape(8.dp))
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text(trade.notes, fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }

                // Close Trade Dialog code
                if (closeTradeDialogId == trade.id) {
                    var exitPriceInput by remember { mutableStateOf(trade.targetPrice.toString()) }
                    AlertDialog(
                        onDismissRequest = { closeTradeDialogId = null },
                        containerColor = SurfaceSlate,
                        title = { Text("Close Position for ${trade.ticker}", color = TextPrimary) },
                        text = {
                            Column {
                                Text("Register final exit selling price to compute trade metrics:", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = exitPriceInput,
                                    onValueChange = { exitPriceInput = it },
                                    label = { Text("Exit Price (₹)") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BullishGreen,
                                        unfocusedBorderColor = ElementCard,
                                        focusedLabelColor = BullishGreen
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val price = exitPriceInput.toDoubleOrNull() ?: trade.targetPrice
                                    viewModel.closeSwingTrade(trade.id, price)
                                    closeTradeDialogId = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BullishGreen)
                            ) {
                                Text("Confirm Exit", color = ObsidianDark, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { closeTradeDialogId = null }, colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Historic Closed trades List
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = BullishGreen, modifier = Modifier.size(16.dp))
            Text(
                text = "Closed Swing Journals (${closedTrades.size})",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black
            )
        }

        if (closedTrades.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No fully realized closed swing trades listed.", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            closedTrades.forEach { trade ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, ElementCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("closed_trade_${trade.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(trade.ticker, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(ElementCard, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(trade.strategy, fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                                Text("In: ₹${trade.entryPrice} • Out: ₹${trade.exitPrice}", fontSize = 11.sp, color = TextSecondary)
                            }

                            // Profit / Loss calculation indicator
                            val pnl = (trade.exitPrice - trade.entryPrice) * trade.quantity
                            val isGain = pnl >= 0
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (isGain) "+" else ""}₹${String.format("%.2f", pnl)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = if (isGain) BullishGreen else BearishRed
                                )
                                IconButton(
                                    onClick = { viewModel.removeTrade(trade.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete trade logs", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JournalOverviewSquare(title: String, score: String, subtitle: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ElementCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(score, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(subtitle, fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}


// --- TAB 5: ARCHITECT SYSTEM ROADMAPS ---
@Composable
fun RoadmapTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "SwingTrade Architecture Blueprint",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "System blueprint, developer database schemas, and integration plan.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Segment 1: Tech Stack Recommendation
        RoadmapSectionHeader("🛠️ 1. MODERN FINTECH TECH STACK RECOMMENDATIONS")
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                BulletPointRow("Web Frontend", "Next.js (React 19) paired with Tailwind CSS for full server-side SEO pre-rendering and fluid custom widgets.")
                BulletPointRow("Mobile Frontend", "Kotlin Multiplatform (KMP) or Jetpack Compose with modern declarative Canvas graphs for real-time tick chart visualizers.")
                BulletPointRow("Backend Engine", "Python with FastAPI (for asynchronous high-frequency operations) and Pandas/NumPy/TA-Lib for technical strategy vector calculations.")
                BulletPointRow("Database Sizer", "PostgreSQL (with TimescaleDB extension for time-series stock tracking metric histories) and Redis for high-speed quote caching.")
                BulletPointRow("Charting Library", "TradingView Lightweight Charts (free, ultra-light, open-source JS canvas engine) or Highcharts Stock.")
            }
        }

        // Segment 2: Financial Data APIs
        RoadmapSectionHeader("📡 2. RECOMMENDED MARKET DATA API CONDUITS")
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                BulletPointRow("Financial Modeling Prep (FMP)", "Top tier fundamental provider offering rich historic balance sheets, analyst target calendars, macro indices, and detailed SEC surprises.")
                BulletPointRow("Polygon.io", "Exceptional, standard-grade market data conduit suited for high-speed live socket streaming and detailed timeframe candles (1m to Daily).")
                BulletPointRow("Alpaca Markets LLC", "The ultimate brokerage asset provider offering commission-free mock/live trading API keys with quote syncing capabilities.")
            }
        }

        // Segment 3: Database schema codes
        RoadmapSectionHeader("🗄️ 3. CORE DATABASE SCHEMA ENTITIES (SQL)")
        Card(
            colors = CardDefaults.cardColors(containerColor = ElementCard),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = """
                        -- Core User Registry
                        CREATE TABLE users (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            email VARCHAR(255) UNIQUE NOT NULL,
                            password_hash VARCHAR(255) NOT NULL,
                            account_balance NUMERIC(15, 2) DEFAULT 10000.00
                        );

                        -- Watchlists Registry
                        CREATE TABLE watchlists (
                            id SERIAL PRIMARY KEY,
                            user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                            ticker VARCHAR(12) NOT NULL,
                            added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );

                        -- Trade Journal Entries
                        CREATE TABLE trade_logs (
                            id SERIAL PRIMARY KEY,
                            user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                            ticker VARCHAR(12) NOT NULL,
                            entry_price NUMERIC(10, 4) NOT NULL,
                            stop_loss NUMERIC(10, 4) NOT NULL,
                            target_price NUMERIC(10, 4) NOT NULL,
                            quantity INTEGER NOT NULL,
                            strategy VARCHAR(60) NOT NULL,
                            is_closed BOOLEAN DEFAULT FALSE,
                            exit_price NUMERIC(10, 4),
                            exit_date TIMESTAMP,
                            remarks TEXT
                        );
                    """.trimIndent(),
                    fontSize = 11.sp,
                    color = BullishGreen,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 14.sp
                )
            }
        }

        // Segment 4: Action Roadmap
        RoadmapSectionHeader("🗺️ 4. TACTICAL FOUR-PHASE DEVELOPMENT ROADMAP")
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, ElementCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                RoadmapStepCard("Phase 1: MVP UI Core Focus", "Build core lightweight chart integration (using lightweight Canvas) and custom screener. Populate static peer stock arrays.")
                RoadmapStepCard("Phase 2: Live APIs & Ratios", "Integrate actual Polygon REST calls inside viewModel. Add live Earnings surprises, relative indexes, and sector strength rankings.")
                RoadmapStepCard("Phase 3: Risk Managers & Database", "Build native trade logging with secondary Room tables. Bind calculators with chart drag-and-drop lines.")
                RoadmapStepCard("Phase 4: AI Pattern & Sentiment Copilot", "Connect Gemini API models to run neural summaries on social posts and chat setups. Include auto bull flag indicators.")
            }
        }
    }
}

@Composable
fun RoadmapSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = SoftGold,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun BulletPointRow(boldTitle: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("• ", color = BullishGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Column {
            Text(text = boldTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = desc, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable
fun RoadmapStepCard(step: String, body: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(step, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = TechBlue)
        Text(body, fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
    }
}
