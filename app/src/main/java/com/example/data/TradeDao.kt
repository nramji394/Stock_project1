package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    // Trade Logs / Journaling
    @Query("SELECT * FROM trade_logs ORDER BY entryDate DESC")
    fun getAllTrades(): Flow<List<TradeJournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeJournalEntry)

    @Update
    suspend fun updateTrade(trade: TradeJournalEntry)

    @Query("DELETE FROM trade_logs WHERE id = :id")
    suspend fun deleteTradeById(id: Int)

    // Watchlist
    @Query("SELECT * FROM watchlist ORDER BY ticker ASC")
    fun getWatchlist(): Flow<List<WatchlistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(item: WatchlistItem)

    @Query("DELETE FROM watchlist WHERE ticker = :ticker")
    suspend fun removeWatchlist(ticker: String)

    // Screener Presets
    @Query("SELECT * FROM screener_presets")
    fun getScreenerPresets(): Flow<List<ScreenerPreset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenerPreset(preset: ScreenerPreset)

    @Query("DELETE FROM screener_presets WHERE id = :id")
    suspend fun deleteScreenerPreset(id: Int)
}
