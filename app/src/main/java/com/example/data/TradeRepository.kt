package com.example.data

import kotlinx.coroutines.flow.Flow

class TradeRepository(private val tradeDao: TradeDao) {

    val allTrades: Flow<List<TradeJournalEntry>> = tradeDao.getAllTrades()
    val allWatchlist: Flow<List<WatchlistItem>> = tradeDao.getWatchlist()
    val allPresets: Flow<List<ScreenerPreset>> = tradeDao.getScreenerPresets()

    suspend fun insertTrade(trade: TradeJournalEntry) {
        tradeDao.insertTrade(trade)
    }

    suspend fun updateTrade(trade: TradeJournalEntry) {
        tradeDao.updateTrade(trade)
    }

    suspend fun deleteTradeById(id: Int) {
        tradeDao.deleteTradeById(id)
    }

    suspend fun insertWatchlist(item: WatchlistItem) {
        tradeDao.insertWatchlist(item)
    }

    suspend fun removeWatchlist(ticker: String) {
        tradeDao.removeWatchlist(ticker)
    }

    suspend fun insertPreset(preset: ScreenerPreset) {
        tradeDao.insertScreenerPreset(preset)
    }

    suspend fun deletePreset(id: Int) {
        tradeDao.deleteScreenerPreset(id)
    }
}
