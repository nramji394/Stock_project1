package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_logs")
data class TradeJournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticker: String,
    val tickerName: String,
    val entryDate: Long = System.currentTimeMillis(),
    val entryPrice: Double,
    val stopLoss: Double,
    val targetPrice: Double,
    val quantity: Int,
    val strategy: String, // e.g., "Bull Flag Breakout", "Mean Reversion Drop", "Golden Cross"
    val isClosed: Boolean = false,
    val exitPrice: Double = 0.0,
    val exitDate: Long = 0L,
    val notes: String = ""
) {
    val riskAmount: Double
        get() = (entryPrice - stopLoss) * quantity

    val rewardAmount: Double
        get() = (targetPrice - entryPrice) * quantity

    val rewardToRiskRatio: Double
        get() = if (entryPrice != stopLoss) {
            (targetPrice - entryPrice) / (entryPrice - stopLoss)
        } else {
            0.0
        }

    val profitOrLoss: Double
        get() = if (isClosed) {
            (exitPrice - entryPrice) * quantity
        } else {
            0.0
        }
}

@Entity(tableName = "watchlist")
data class WatchlistItem(
    @PrimaryKey val ticker: String,
    val name: String,
    val price: Double,
    val changePercent: Double,
    val sector: String,
    val relativeStrengthRank: Int = 50 // 1 to 100 relative strength score
)

@Entity(tableName = "screener_presets")
data class ScreenerPreset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val filterSector: String = "All",
    val filterMinRsi: Double = 0.0,
    val filterMaxRsi: Double = 100.0,
    val filterAboveSma50: Boolean = false,
    val filterEarningsNextWeek: Boolean = false,
    val isPrebuilt: Boolean = false
)
