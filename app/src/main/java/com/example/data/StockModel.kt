package com.example.data

import kotlin.random.Random

data class Candle(
    val dateLabel: String,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float,
    // Indicators built for this candle coordinate
    val ema10: Float = 0f,
    val ema20: Float = 0f,
    val sma50: Float = 0f,
    val rsi: Float = 50f,
    val macdLine: Float = 0f,
    val signalLine: Float = 0f,
    val bbUpper: Float = 0f,
    val bbLower: Float = 0f,
    val supportLevel: Float = 0f,
    val resistanceLevel: Float = 0f
)

data class StockTicker(
    val ticker: String,
    val name: String,
    val price: Double,
    val changePercent: Double,
    val volume: Long,
    val sector: String,
    val industry: String,
    val peRatio: Double,
    val pegRatio: Double,
    val priceToSales: Double,
    val debtToEquity: Double,
    val institutionalOwnership: Double, // percentage
    val shortInterest: Double, // percentage
    val daysToCover: Double, // in days
    val nextEarningsDate: String,
    val historicalEarningsSurprise: List<EarningsSurprise>,
    val relativeStrengthRank: Int, // 1 to 100 relative strength score
    val rsi: Double,
    val above50Sma: Boolean,
    val supportZone: Double,
    val resistanceZone: Double,
    val volumeProfileVisibleRange: List<Float> // Series of volume profile horizontally stacked
)

data class EarningsSurprise(
    val quarter: String,
    val epsActual: Double,
    val epsEstimate: Double,
    val surprisePercent: Double
)

object StockDataGenerator {
    fun generateStockTickers(): List<StockTicker> {
        return listOf(
            StockTicker(
                ticker = "RELIANCE",
                name = "Reliance Industries Ltd.",
                price = 2450.50,
                changePercent = 1.45,
                volume = 8500000,
                sector = "Energy & Materials",
                industry = "Oil & Gas / Retail",
                peRatio = 26.5,
                pegRatio = 1.35,
                priceToSales = 2.12,
                debtToEquity = 0.40,
                institutionalOwnership = 21.4,
                shortInterest = 0.65,
                daysToCover = 0.9,
                nextEarningsDate = "Jul 24, 2026",
                historicalEarningsSurprise = listOf(
                    EarningsSurprise("Q1 '26", 19.5, 18.2, 7.1),
                    EarningsSurprise("Q4 '25", 18.0, 17.5, 2.8),
                    EarningsSurprise("Q3 '25", 17.2, 16.9, 1.8),
                    EarningsSurprise("Q2 '25", 16.5, 16.0, 3.1)
                ),
                relativeStrengthRank = 82,
                rsi = 58.4,
                above50Sma = true,
                supportZone = 2390.00,
                resistanceZone = 2520.00,
                volumeProfileVisibleRange = listOf(1.2f, 1.8f, 3.5f, 4.2f, 5.1f, 3.8f, 2.0f, 1.5f, 0.9f)
            ),
            StockTicker(
                ticker = "TCS",
                name = "Tata Consultancy Services Ltd.",
                price = 3840.00,
                changePercent = -0.75,
                volume = 2200000,
                sector = "IT & Software",
                industry = "IT Services",
                peRatio = 31.2,
                pegRatio = 1.85,
                priceToSales = 6.20,
                debtToEquity = 0.05,
                institutionalOwnership = 15.6,
                shortInterest = 0.45,
                daysToCover = 1.4,
                nextEarningsDate = "Jul 12, 2026",
                historicalEarningsSurprise = listOf(
                    EarningsSurprise("Q1 '26", 32.4, 31.0, 4.5),
                    EarningsSurprise("Q4 '25", 30.5, 30.1, 1.3),
                    EarningsSurprise("Q3 '25", 29.8, 29.2, 2.1),
                    EarningsSurprise("Q2 '25", 28.5, 28.0, 1.8)
                ),
                relativeStrengthRank = 72,
                rsi = 49.2,
                above50Sma = false,
                supportZone = 3750.00,
                resistanceZone = 3980.00,
                volumeProfileVisibleRange = listOf(2.1f, 3.4f, 6.7f, 9.2f, 12.1f, 8.4f, 4.2f, 2.1f, 1.0f)
            ),
            StockTicker(
                ticker = "HDFCBANK",
                name = "HDFC Bank Ltd.",
                price = 1560.80,
                changePercent = -1.25,
                volume = 14200000,
                sector = "Banking & Finance",
                industry = "Private Banking",
                peRatio = 18.4,
                pegRatio = 1.15,
                priceToSales = 3.80,
                debtToEquity = 0.95,
                institutionalOwnership = 52.1,
                shortInterest = 1.35,
                daysToCover = 0.8,
                nextEarningsDate = "Jul 18, 2026",
                historicalEarningsSurprise = listOf(
                    EarningsSurprise("Q1 '26", 11.2, 10.8, 3.7),
                    EarningsSurprise("Q4 '25", 10.4, 10.1, 3.0),
                    EarningsSurprise("Q3 '25", 9.8, 9.5, 3.1),
                    EarningsSurprise("Q2 '25", 9.2, 9.0, 2.2)
                ),
                relativeStrengthRank = 45,
                rsi = 38.6,
                above50Sma = false,
                supportZone = 1510.00,
                resistanceZone = 1620.00,
                volumeProfileVisibleRange = listOf(5.6f, 4.3f, 2.1f, 1.8f, 3.2f, 6.7f, 8.1f, 4.5f, 2.3f)
            ),
            StockTicker(
                ticker = "INFY",
                name = "Infosys Ltd.",
                price = 1420.25,
                changePercent = 2.10,
                volume = 6400000,
                sector = "IT & Software",
                industry = "IT Services",
                peRatio = 24.8,
                pegRatio = 1.45,
                priceToSales = 4.15,
                debtToEquity = 0.08,
                institutionalOwnership = 33.4,
                shortInterest = 0.85,
                daysToCover = 1.1,
                nextEarningsDate = "Jul 15, 2026",
                historicalEarningsSurprise = listOf(
                    EarningsSurprise("Q1 '26", 12.8, 12.5, 2.4),
                    EarningsSurprise("Q4 '25", 12.1, 11.8, 2.5),
                    EarningsSurprise("Q3 '25", 11.5, 11.0, 4.5),
                    EarningsSurprise("Q2 '25", 10.8, 10.5, 2.9)
                ),
                relativeStrengthRank = 68,
                rsi = 56.4,
                above50Sma = true,
                supportZone = 1380.00,
                resistanceZone = 1480.00,
                volumeProfileVisibleRange = listOf(1.5f, 2.2f, 3.8f, 6.1f, 8.4f, 5.3f, 3.1f, 1.9f, 1.1f)
            ),
            StockTicker(
                ticker = "ICICIBANK",
                name = "ICICI Bank Ltd.",
                price = 1120.40,
                changePercent = 1.80,
                volume = 9800000,
                sector = "Banking & Finance",
                industry = "Private Banking",
                peRatio = 17.5,
                pegRatio = 1.05,
                priceToSales = 3.90,
                debtToEquity = 0.82,
                institutionalOwnership = 44.5,
                shortInterest = 0.55,
                daysToCover = 0.6,
                nextEarningsDate = "Jul 25, 2026",
                historicalEarningsSurprise = listOf(
                    EarningsSurprise("Q1 '26", 9.5, 9.1, 4.4),
                    EarningsSurprise("Q4 '25", 8.8, 8.5, 3.5),
                    EarningsSurprise("Q3 '25", 8.2, 7.9, 3.8),
                    EarningsSurprise("Q2 '25", 7.5, 7.2, 4.2)
                ),
                relativeStrengthRank = 85,
                rsi = 64.8,
                above50Sma = true,
                supportZone = 1080.00,
                resistanceZone = 1160.00,
                volumeProfileVisibleRange = listOf(0.5f, 0.9f, 1.2f, 2.1f, 4.6f, 5.8f, 7.2f, 3.1f, 1.0f)
            ),
            StockTicker(
                ticker = "SBIN",
                name = "State Bank of India",
                price = 745.30,
                changePercent = -0.85,
                volume = 11500000,
                sector = "Banking & Finance",
                industry = "Public Banking",
                peRatio = 12.8,
                pegRatio = 0.85,
                priceToSales = 1.25,
                debtToEquity = 1.10,
                institutionalOwnership = 11.2,
                shortInterest = 0.95,
                daysToCover = 0.7,
                nextEarningsDate = "Jul 31, 2026",
                historicalEarningsSurprise = listOf(
                    EarningsSurprise("Q1 '26", 14.2, 13.5, 5.2),
                    EarningsSurprise("Q4 '25", 13.0, 12.8, 1.6),
                    EarningsSurprise("Q3 '25", 12.2, 12.0, 1.7),
                    EarningsSurprise("Q2 '25", 11.5, 11.2, 2.7)
                ),
                relativeStrengthRank = 78,
                rsi = 51.5,
                above50Sma = true,
                supportZone = 720.00,
                resistanceZone = 780.00,
                volumeProfileVisibleRange = listOf(8.1f, 6.2f, 4.5f, 3.1f, 1.8f, 3.4f, 7.6f, 11.2f, 5.4f)
            ),
            StockTicker(
                ticker = "LT",
                name = "Larsen & Toubro Ltd.",
                price = 3480.00,
                changePercent = 0.60,
                volume = 1800000,
                sector = "Infrastructure",
                industry = "Engineering & Construction",
                peRatio = 38.4,
                pegRatio = 1.62,
                priceToSales = 2.45,
                debtToEquity = 1.25,
                institutionalOwnership = 19.8,
                shortInterest = 0.35,
                daysToCover = 1.2,
                nextEarningsDate = "Jul 28, 2026",
                historicalEarningsSurprise = listOf(
                    EarningsSurprise("Q1 '26", 24.5, 23.8, 2.9),
                    EarningsSurprise("Q4 '25", 23.0, 22.5, 2.2),
                    EarningsSurprise("Q3 '25", 21.8, 21.0, 3.8),
                    EarningsSurprise("Q2 '25", 20.2, 19.8, 2.0)
                ),
                relativeStrengthRank = 80,
                rsi = 59.8,
                above50Sma = true,
                supportZone = 3380.00,
                resistanceZone = 3580.00,
                volumeProfileVisibleRange = listOf(1.2f, 2.5f, 4.6f, 7.2f, 9.1f, 5.4f, 3.1f, 1.8f, 1.0f)
            ),
            StockTicker(
                ticker = "BHARTIRTAL",
                name = "Bharti Airtel Ltd.",
                price = 1210.15,
                changePercent = 3.20,
                volume = 5800000,
                sector = "Telecommunications",
                industry = "Telecom Services",
                peRatio = 48.5,
                pegRatio = 2.10,
                priceToSales = 4.25,
                debtToEquity = 1.95,
                institutionalOwnership = 23.2,
                shortInterest = 0.75,
                daysToCover = 0.8,
                nextEarningsDate = "Aug 02, 2026",
                historicalEarningsSurprise = listOf(
                    EarningsSurprise("Q1 '26", 6.8, 6.2, 9.7),
                    EarningsSurprise("Q4 '25", 5.9, 5.5, 7.3),
                    EarningsSurprise("Q3 '25", 5.2, 4.8, 8.3),
                    EarningsSurprise("Q2 '25", 4.5, 4.2, 7.1)
                ),
                relativeStrengthRank = 92,
                rsi = 72.1,
                above50Sma = true,
                supportZone = 1160.00,
                resistanceZone = 1260.00,
                volumeProfileVisibleRange = listOf(0.3f, 0.7f, 1.1f, 1.8f, 3.2f, 4.5f, 5.6f, 2.4f, 0.8f)
            ),
            StockTicker(
                ticker = "TATAMOTORS",
                name = "Tata Motors Ltd.",
                price = 940.60,
                changePercent = 4.80,
                volume = 7200000,
                sector = "Automotive",
                industry = "Auto Manufacturers",
                peRatio = 16.8,
                pegRatio = 0.95,
                priceToSales = 0.85,
                debtToEquity = 1.55,
                institutionalOwnership = 14.1,
                shortInterest = 0.85,
                daysToCover = 0.5,
                nextEarningsDate = "Aug 05, 2026",
                historicalEarningsSurprise = listOf(
                    EarningsSurprise("Q1 '26", 11.2, 9.8, 14.3),
                    EarningsSurprise("Q4 '25", 9.5, 8.8, 8.0),
                    EarningsSurprise("Q3 '25", 8.4, 8.0, 5.0),
                    EarningsSurprise("Q2 '25", 7.2, 7.0, 2.9)
                ),
                relativeStrengthRank = 95,
                rsi = 75.4,
                above50Sma = true,
                supportZone = 890.00,
                resistanceZone = 980.00,
                volumeProfileVisibleRange = listOf(0.8f, 1.4f, 2.5f, 4.2f, 6.1f, 4.8f, 3.0f, 1.5f, 0.7f)
            )
        )
    }

    // Generate simulated candle data matching a timeline
    fun generateCandles(ticker: String, timeframe: String, count: Int = 30): List<Candle> {
        val basePrice = when(ticker) {
            "RELIANCE" -> 2450f
            "TCS" -> 3840f
            "INFY" -> 1420f
            "HDFCBANK" -> 1560f
            "ICICIBANK" -> 1120f
            "SBIN" -> 745f
            "LT" -> 3480f
            "BHARTIRTAL" -> 1210f
            "TATAMOTORS" -> 940f
            else -> 1000f
        }

        val scale = when(timeframe) {
            "Weekly" -> 4.5f * (basePrice * 0.01f)
            "Daily" -> 2.0f * (basePrice * 0.01f)
            "4H" -> 0.8f * (basePrice * 0.01f)
            "1H" -> 0.4f * (basePrice * 0.01f)
            else -> 1.0f * (basePrice * 0.01f)
        }

        // Simulating sequence
        val candles = mutableListOf<Candle>()
        var lastClose = basePrice - (count * 0.3f * scale)
        for (i in 0 until count) {
            val ratio = i.toFloat() / count.toFloat()
            val trend = when (ticker) {
                "HDFCBANK" -> -0.3f // downward pullback
                "BHARTIRTAL" -> 1.0f // strong momentum rise
                "TATAMOTORS" -> 1.2f // explosive bullish trend
                "TCS" -> 0.1f // steady consolidation
                else -> 0.2f // generic steady rise
            }
            
            // Add wave cycles
            val wave = Math.sin(ratio * Math.PI * 3.5).toFloat() * 6f * scale
            val open = lastClose
            val close = open + (wave + trend * scale) + (Random.nextDouble(-4.0, 4.0).toFloat() * 0.5f * scale)
            val high = Math.max(open, close) + Random.nextDouble(0.1, 3.0).toFloat() * 0.3f * scale
            val low = Math.min(open, close) - Random.nextDouble(0.1, 3.0).toFloat() * 0.3f * scale
            
            val volBase = when(ticker) {
                "RELIANCE" -> 800000f
                "HDFCBANK" -> 1200000f
                "SBIN" -> 1000000f
                else -> 400000f
            }
            val volume = volBase + Random.nextDouble(-100000.0, 400000.0).toFloat()
            
            val label = when(timeframe) {
                "Weekly" -> "Wk ${i + 1}"
                "Daily" -> "May ${i + 1}"
                "4H" -> "H${(i * 4) % 24} ${20 + (i * 4)/24}/05"
                else -> "${i % 24}:00"
            }

            candles.add(
                Candle(
                    dateLabel = label,
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = volume
                )
            )
            lastClose = close
        }

        // Compute technical indicators over the generated candles
        val results = mutableListOf<Candle>()
        for (idx in candles.indices) {
            val c = candles[idx]
            
            // 10 EMA Calculation (smooth approximation)
            val ema10 = if (idx == 0) c.close else {
                val alpha = 2f / (10f + 1f)
                (c.close * alpha) + (candles[idx - 1].close * (1f - alpha))
            }

            // 20 EMA Calculation
            val ema20 = if (idx == 0) c.close else {
                val alpha = 2f / (20f + 1f)
                (c.close * alpha) + (candles[idx - 1].close * (1f - alpha))
            }

            // 50 SMA Calculation (simple rolling mean)
            val rollingCount = Math.min(idx + 1, 15) // shorter window for our short fake series to keep it beautiful
            var sum = 0f
            for (r in 0 until rollingCount) {
                sum += candles[idx - r].close
            }
            val sma50 = sum / rollingCount

            // Bollinger Bands
            val stdev = ((c.high - c.low) * 0.45f)
            val bbUpper = sma50 + (1.8f * stdev)
            val bbLower = sma50 - (1.8f * stdev)

            // RSI (fluctuating based on close vs open)
            val relativeDiff = (c.close - c.open) / (c.high - c.low + 0.1f)
            val rsi = 50f + (relativeDiff * 25f) + (Math.sin(idx.toDouble() * 0.8).toFloat() * 10f)

            // MACD (using EMA differences)
            val macdLine = (ema10 - ema20) * 1.5f
            val signalLine = if (idx == 0) macdLine else (macdLine * 0.2f + (results[idx - 1].macdLine * 0.8f))

            results.add(
                c.copy(
                    ema10 = ema10,
                    ema20 = ema20,
                    sma50 = sma50,
                    bbUpper = bbUpper,
                    bbLower = bbLower,
                    rsi = rsi.coerceIn(10f, 95f),
                    macdLine = macdLine,
                    signalLine = signalLine,
                    supportLevel = basePrice * 0.94f,
                    resistanceLevel = basePrice * 1.05f
                )
            )
        }
        return results
    }
}
