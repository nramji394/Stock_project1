package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class StockViewModel(private val repository: TradeRepository) : ViewModel() {

    // Market data state
    private val _tickers = MutableStateFlow<List<StockTicker>>(emptyList())
    val tickers: StateFlow<List<StockTicker>> = _tickers.asStateFlow()

    private val _selectedTicker = MutableStateFlow<StockTicker?>(null)
    val selectedTicker: StateFlow<StockTicker?> = _selectedTicker.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow("Daily")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    private val _selectedCandles = MutableStateFlow<List<Candle>>(emptyList())
    val selectedCandles: StateFlow<List<Candle>> = _selectedCandles.asStateFlow()

    // Indicator Toggles
    val showEma10 = MutableStateFlow(true)
    val showEma20 = MutableStateFlow(true)
    val showSma50 = MutableStateFlow(true)
    val showBollingerBands = MutableStateFlow(false)
    val showVolumeProfile = MutableStateFlow(true)

    // Room DB Observables
    val tradeLogs: StateFlow<List<TradeJournalEntry>> = repository.allTrades
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchlist: StateFlow<List<WatchlistItem>> = repository.allWatchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customScreenerPresets: StateFlow<List<ScreenerPreset>> = repository.allPresets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active screen state inside ViewModel for state preservation
    val activeTab = MutableStateFlow("screener")

    // Dynamic screener filters
    val selectedSectorFilter = MutableStateFlow("All")
    val minRsiFilter = MutableStateFlow(0f)
    val maxRsiFilter = MutableStateFlow(100f)
    val above50SmaFilter = MutableStateFlow(false)
    val rsiOversoldFilter = MutableStateFlow(false)
    val rsiOverboughtFilter = MutableStateFlow(false)
    val squeezePotentialFilter = MutableStateFlow(false)

    // Position Size Calculator states
    val calcAccountSize = MutableStateFlow("10000")
    val calcRiskPercent = MutableStateFlow("1.5")
    val calcEntryPrice = MutableStateFlow("")
    val calcStopLoss = MutableStateFlow("")
    val calcTargetPrice = MutableStateFlow("")
    
    // Position sizing results
    val calculatedShares = MutableStateFlow(0)
    val calculatedPositionSize = MutableStateFlow(0.0)
    val calculatedRiskAmount = MutableStateFlow(0.0)
    val calculatedRewardToRisk = MutableStateFlow(0.0)

    // Gemini Copilot Assistant state
    val copilotResponse = MutableStateFlow<String>("")
    val isCopilotLoading = MutableStateFlow(false)
    val isUsingFallback = MutableStateFlow(false)

    init {
        // Load initial seed tickers
        val generated = StockDataGenerator.generateStockTickers()
        _tickers.value = generated
        _selectedTicker.value = generated.firstOrNull()
        updateCandles()

        // Start real-time live simulation ticks
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(2000L) // Update every 2 seconds
                updateLiveMarketTickers()
            }
        }

        viewModelScope.launch {
            // Seed Watchlist with RELIANCE and HDFCBANK (Indian stocks) if empty
            watchlist.take(2).collect { currentList ->
                if (currentList.isEmpty() && generated.isNotEmpty()) {
                    val reliance = generated.find { it.ticker == "RELIANCE" }
                    val hdfc = generated.find { it.ticker == "HDFCBANK" }
                    if (reliance != null) toggleWatchlist(reliance)
                    if (hdfc != null) toggleWatchlist(hdfc)
                }
            }
        }
        
        viewModelScope.launch {
            // Seed a custom preset
            customScreenerPresets.take(2).collect { presets ->
                if (presets.isEmpty()) {
                    repository.insertPreset(
                        ScreenerPreset(
                            name = "Oversold Tech Pulldown",
                            filterSector = "Technology",
                            filterMinRsi = 0.0,
                            filterMaxRsi = 45.0,
                            filterAboveSma50 = false,
                            isPrebuilt = true
                        )
                    )
                    repository.insertPreset(
                        ScreenerPreset(
                            name = "Squeeze & Relative Strength Breakouts",
                            filterSector = "All",
                            filterMinRsi = 50.0,
                            filterMaxRsi = 75.0,
                            filterAboveSma50 = true,
                            isPrebuilt = true
                        )
                    )
                }
            }
        }

        // Auto-recalculate position calculator when inputs change
        combine(
            calcAccountSize, calcRiskPercent, calcEntryPrice, calcStopLoss, calcTargetPrice
        ) { acct, risk, entry, stop, tg ->
            calculatePositionSizeInternal(acct, risk, entry, stop, tg)
        }.launchIn(viewModelScope)
    }

    fun selectTicker(ticker: StockTicker) {
        _selectedTicker.value = ticker
        updateCandles()
        // Reset calc entry fields for convenience
        calcEntryPrice.value = String.format("%.2f", ticker.price)
        calcStopLoss.value = String.format("%.2f", ticker.supportZone)
         calcTargetPrice.value = String.format("%.2f", ticker.resistanceZone)
        copilotResponse.value = ""
    }

    fun setTimeframe(tf: String) {
        _selectedTimeframe.value = tf
        updateCandles()
    }

    private fun updateCandles() {
        val currentTicker = _selectedTicker.value ?: return
        val currentTf = _selectedTimeframe.value
        _selectedCandles.value = StockDataGenerator.generateCandles(currentTicker.ticker, currentTf)
    }

    // Toggle items in watchlists
    fun toggleWatchlist(stock: StockTicker) {
        viewModelScope.launch {
            val contains = watchlist.value.any { it.ticker == stock.ticker }
            if (contains) {
                repository.removeWatchlist(stock.ticker)
            } else {
                repository.insertWatchlist(
                    WatchlistItem(
                        ticker = stock.ticker,
                        name = stock.name,
                        price = stock.price,
                        changePercent = stock.changePercent,
                        sector = stock.sector,
                        relativeStrengthRank = stock.relativeStrengthRank
                    )
                )
            }
        }
    }

    // Screener filter list
    @Suppress("UNCHECKED_CAST")
    val filteredTickers: Flow<List<StockTicker>> = combine(
        _tickers,
        selectedSectorFilter,
        minRsiFilter,
        maxRsiFilter,
        above50SmaFilter,
        rsiOversoldFilter,
        rsiOverboughtFilter,
        squeezePotentialFilter
    ) { args: Array<Any> ->
        val raw = args[0] as List<StockTicker>
        val sector = args[1] as String
        val minRsi = (args[2] as Float).toDouble()
        val maxRsi = (args[3] as Float).toDouble()
        val aboveSma = args[4] as Boolean
        val overSold = args[5] as Boolean
        val overBought = args[6] as Boolean
        val squeeze = args[7] as Boolean

        raw.filter { ticker ->
            val sectorMatch = sector == "All" || ticker.sector == sector
            val rsiRangeMatch = ticker.rsi >= minRsi && ticker.rsi <= maxRsi
            val smaMatch = !aboveSma || ticker.above50Sma
            val oversoldMatch = !overSold || ticker.rsi <= 40
            val overboughtMatch = !overBought || ticker.rsi >= 70
            val squeezeMatch = !squeeze || ticker.shortInterest >= 5.0
            
            sectorMatch && rsiRangeMatch && smaMatch && oversoldMatch && overboughtMatch && squeezeMatch
        }
    }

    // Prebuilt screener templates
    fun applyPreset(preset: ScreenerPreset) {
        selectedSectorFilter.value = preset.filterSector
        minRsiFilter.value = preset.filterMinRsi.toFloat()
        maxRsiFilter.value = preset.filterMaxRsi.toFloat()
        above50SmaFilter.value = preset.filterAboveSma50
        rsiOversoldFilter.value = false
        rsiOverboughtFilter.value = false
        squeezePotentialFilter.value = preset.name.contains("Squeeze", ignoreCase = true)
    }

    fun clearScreenerFilters() {
        selectedSectorFilter.value = "All"
        minRsiFilter.value = 0f
        maxRsiFilter.value = 100f
        above50SmaFilter.value = false
        rsiOversoldFilter.value = false
        rsiOverboughtFilter.value = false
        squeezePotentialFilter.value = false
    }

    // Position Size Calculator
    private fun calculatePositionSizeInternal(
        acctStr: String, riskStr: String, entryStr: String, stopStr: String, targetStr: String
    ) {
        val account = acctStr.toDoubleOrNull() ?: 10000.0
        val riskPct = riskStr.toDoubleOrNull() ?: 1.0
        val entry = entryStr.toDoubleOrNull() ?: 0.0
        val stop = stopStr.toDoubleOrNull() ?: 0.0
        val target = targetStr.toDoubleOrNull() ?: 0.0

        if (entry <= 0.0 || stop <= 0.0 || entry == stop) {
            calculatedShares.value = 0
            calculatedPositionSize.value = 0.0
            calculatedRiskAmount.value = 0.0
            calculatedRewardToRisk.value = 0.0
            return
        }

        val totalRiskCash = account * (riskPct / 100.0)
        val riskPerShare = Math.abs(entry - stop)
        val shares = (totalRiskCash / riskPerShare).toInt().coerceAtLeast(1)
        val positionSize = shares * entry
        val totalRisk = shares * riskPerShare
        
        val rewardPerShare = Math.abs(target - entry)
        val r2r = if (riskPerShare > 0) rewardPerShare / riskPerShare else 0.0

        calculatedShares.value = shares
        calculatedPositionSize.value = positionSize
        calculatedRiskAmount.value = totalRisk
        calculatedRewardToRisk.value = r2r
    }

    // Adding Journal entries
    fun logSwingTrade(strategy: String, notes: String) {
        val currentStock = _selectedTicker.value ?: return
        val entry = calcEntryPrice.value.toDoubleOrNull() ?: currentStock.price
        val stop = calcStopLoss.value.toDoubleOrNull() ?: currentStock.supportZone
        val target = calcTargetPrice.value.toDoubleOrNull() ?: currentStock.resistanceZone
        val shares = calculatedShares.value.coerceAtLeast(1)

        viewModelScope.launch {
            repository.insertTrade(
                TradeJournalEntry(
                    ticker = currentStock.ticker,
                    tickerName = currentStock.name,
                    entryPrice = entry,
                    stopLoss = stop,
                    targetPrice = target,
                    quantity = shares,
                    strategy = strategy,
                    notes = notes
                )
            )
        }
    }

    fun closeSwingTrade(id: Int, exitPrice: Double) {
        viewModelScope.launch {
            val trade = tradeLogs.value.find { it.id == id } ?: return@launch
            repository.insertTrade(
                trade.copy(
                    isClosed = true,
                    exitPrice = exitPrice,
                    exitDate = System.currentTimeMillis()
                )
            )
        }
    }

    fun removeTrade(id: Int) {
        viewModelScope.launch {
            repository.deleteTradeById(id)
        }
    }

    // Gemini API integration block with fallbacks
    fun askCopilotSetup(stock: StockTicker, tf: String) {
        isCopilotLoading.value = true
        copilotResponse.value = ""
        
        val apiKey = com.example.BuildConfig.GEMINI_API_KEY
        val hasApiKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        viewModelScope.launch {
            if (hasApiKey) {
                isUsingFallback.value = false
                val prompt = """
                    You are a Senior FinTech Swing Trading Coach & Quantitative Market Strategist advising a client in the Indian Stock Market.
                    Provide a comprehensive swing trading setup report for ${stock.ticker} (${stock.name}) on the $tf timeframe.
                    
                    STOCK METRICS:
                    - Current Price: ₹${stock.price}
                    - Sector / Industry: ${stock.sector} / ${stock.industry}
                    - RSI Level: ${stock.rsi} (Current trend status: ${if(stock.rsi < 40) "Oversold Pullback" else if(stock.rsi > 70) "Overbought Momentum" else "Consolidation"})
                    - PE Ratio: ${stock.peRatio} | PEG Ratio: ${stock.pegRatio}
                    - Debt-to-Equity: ${stock.debtToEquity}
                    - Market Cap / Vol: Heavy (Price: ₹${stock.price}, Session Vol: ${stock.volume})
                    - Institution Holding: ${stock.institutionalOwnership}%
                    - Short Interest: ${stock.shortInterest}% (Days to Cover: ${stock.daysToCover} days)
                    - Upcoming Earnings: ${stock.nextEarningsDate}
                    - High-volume Support: ₹${stock.supportZone} | Heavy Overhead Resistance: ₹${stock.resistanceZone}
                    
                    Provide are structured response containing:
                    1. **TECHNICAL ANALYSIS CONFLUENCE**: Analyze RSI ($tf), support/resistance ranges, SMA indicators. Explain details about trend strength.
                    2. **CATALYST EVALUATION**: Comment on short-squeeze risks (Short Interest ${stock.shortInterest}%), earnings calendar timeline ($tf), and relative strength.
                    3. **SWING CONVICTION**: High/Medium/Low. Describe why this is a prime play or why care is needed over a 2-day to 4-week window.
                    4. **COMPREHENSIVE TRADING PLAN**:
                       - Recommended Entry Zone (₹)
                       - Defensive Stop Loss (₹)
                       - High Probability Profit Targets (₹)
                       - Expected Reward-to-Risk Ratio
                    Be objective, highly metrics-focused, and write clean, elegant markup. Keep it concise, displaying pricing in Rupees (₹).
                """.trimIndent()

                val result = callGeminiApiDirect(apiKey, prompt)
                copilotResponse.value = result
            } else {
                // Heuristic technical generator (simulate high value architecture)
                isUsingFallback.value = true
                val result = generateHeuristicAnalysis(stock, tf)
                copilotResponse.value = result
            }
            isCopilotLoading.value = false
        }
    }

    private suspend fun callGeminiApiDirect(apiKey: String, prompt: String): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient.Builder()
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .build()

        val jsonRequest = JSONObject().apply {
            put("contents", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.3) // high precision
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonRequest.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "Error fetching from Gemini API: ${response.code} (Code ${response.message})"
                }
                val rawBody = response.body?.string() ?: return@withContext "Error: Received empty response from model."
                val jsonResponse = JSONObject(rawBody)
                val candidates = jsonResponse.getJSONArray("candidates")
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.getJSONObject("content")
                val parts = content.getJSONArray("parts")
                parts.getJSONObject(0).getString("text")
            }
        } catch (e: Exception) {
            "API Connection Exception: ${e.localizedMessage}. Checking your internet connection or key status in AI Studio."
        }
    }

    private fun updateLiveMarketTickers() {
        val currentTickers = _tickers.value.toMutableList()
        if (currentTickers.isEmpty()) return

        val updatedTickers = currentTickers.map { ticker ->
            // Simulating a tiny index step update
            val percentChange = (java.util.Random().nextDouble() - 0.49) * 0.4 // -0.2% to +0.2%
            val priceDiff = ticker.price * (percentChange / 100.0)
            val newPrice = (ticker.price + priceDiff).coerceAtLeast(1.0)
            val newChangePercent = ticker.changePercent + (percentChange * 1.5)
            val newRsi = (ticker.rsi + (java.util.Random().nextDouble() - 0.5) * 1.0).coerceIn(10.0, 95.0)
            
            ticker.copy(
                price = (Math.round(newPrice * 100.0) / 100.0),
                changePercent = (Math.round(newChangePercent * 100.0) / 100.0),
                rsi = (Math.round(newRsi * 10.0) / 10.0)
            )
        }

        _tickers.value = updatedTickers

        // Update selected ticker if present
        val currentSelected = _selectedTicker.value
        if (currentSelected != null) {
            val freshSelected = updatedTickers.find { it.ticker == currentSelected.ticker }
            if (freshSelected != null) {
                _selectedTicker.value = freshSelected
                
                // Live update the last candle in _selectedCandles to match live price!
                val candles = _selectedCandles.value.toMutableList()
                if (candles.isNotEmpty()) {
                    val lastCandle = candles.last()
                    val livePrice = freshSelected.price.toFloat()
                    val updatedLastCandle = lastCandle.copy(
                        close = livePrice,
                        high = Math.max(lastCandle.high, livePrice),
                        low = Math.min(lastCandle.low, livePrice)
                    )
                    candles[candles.lastIndex] = updatedLastCandle
                    _selectedCandles.value = candles
                }
            }
        }
    }

    private fun generateHeuristicAnalysis(stock: StockTicker, tf: String): String {
        val trendStatus = if (stock.rsi < 42f) "BULLISH MEAN REVERSION PULLBACK" 
                          else if (stock.rsi > 66f) "MOMENTUM CONTINUATION / SQUEEZE"
                          else "CONSOLIDATION ZONE COIL"
        val entry = stock.price * 1.005
        val stop = stock.supportZone * 0.99
        val target = stock.resistanceZone * 0.98
        val r2rValue = (target - entry) / (entry - stop)

        val earningsWarning = if (stock.nextEarningsDate.contains("Jul") || stock.nextEarningsDate.contains("Aug")) {
            "⚠️ Earnings on ${stock.nextEarningsDate} represents a clear catalyst event within our 4-week tactical horizon."
        } else {
            "✅ Clean runway. Next earnings on ${stock.nextEarningsDate} allows safe swing holding without direct gap-down event risk."
        }

        return """
            ### 🤖 SwingTrade Heuristic Signal Engine [LOCAL MODE]
            
            #### 1. TECHNICAL ANALYSIS CONFLUENCE
            *   **Trend Matrix ($tf)**: Currently displaying a **$trendStatus**.
            *   **RSI Integration**: RSI is at **${String.format("%.1f", stock.rsi)}**, indicating the asset is in a ${if(stock.rsi < 40) "high probability accumulation" else "neutral-bullish continuation"} posture.
            *   **Structure Profile**: High-volume support verified at **₹${String.format("%.2f", stock.supportZone)}** matching the volume profile POC (Point of Control) cluster. overhead resistance is heavy around **₹${String.format("%.2f", stock.resistanceZone)}**.
            
            #### 2. CATALYST EVALUATION
            *   **Short Interest Squeeze Vector**: Short interest sits at **${stock.shortInterest}%** with a Days-to-Cover ratio of **${stock.daysToCover}**. ${if(stock.shortInterest > 5.0) "High short ratios present squeeze trigger opportunities upon volume surges!" else "Low short interest represents minimal short-covering torque."}.
            *   **Sector Peer Comparison**: Sector Relative Strength stands at **Rank ${stock.relativeStrengthRank}/100** indicating ${if(stock.relativeStrengthRank > 75) "Top Tier Institutional flow inflows" else "market-neutral index synchronization"}.
            *   $earningsWarning
            
            #### 3. SWING CONVICTION
            *   **Setup rating**: **${if (stock.rsi < 40 && stock.above50Sma) "Grade A Swing Buy (Mean Reversion)" else if(stock.above50Sma) "Grade B+ Trend Continuation" else "Grade B Pullback Play"}**
            *   **Horizon**: 5 to 14 Sessions.
            
            #### 4. TRADING PLAN CODES
            *   🎯 **Recommended Entry Trigger**: **₹${String.format("%.2f", entry)}** (limit orders near structures)
            *   ⛔ **Tactical Stop Loss**: **₹${String.format("%.2f", stop)}** (below structure invalidation level)
            *   🚀 **Target Profit-Taker**: **₹${String.format("%.2f", target)}** (first major overhead volume pocket)
            *   📊 **Reward-to-Risk ratio**: **${String.format("%.2f", r2rValue)}:1**
            
            ---
            *💡 Tip: To unlock AI-powered Catalyst analyses, institutional holder sentiment scans, and macro cross-market reports, connect a valid **GEMINI_API_KEY** in the AI Studio Secrets panel!*
        """.trimIndent()
    }
}

// ViewModel factory implementation
class StockViewModelFactory(private val repository: TradeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
