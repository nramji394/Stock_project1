package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.TradeDatabase
import com.example.data.TradeRepository
import com.example.ui.DashboardScreen
import com.example.ui.StockViewModel
import com.example.ui.StockViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Room Database
    val database = TradeDatabase.getDatabase(applicationContext)
    val dao = database.tradeDao()
    val repository = TradeRepository(dao)

    // Instantiate State ViewModel via Factory
    val viewModel = ViewModelProvider(
        this,
        StockViewModelFactory(repository)
    )[StockViewModel::class.java]

    setContent {
      MyApplicationTheme {
        DashboardScreen(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}

