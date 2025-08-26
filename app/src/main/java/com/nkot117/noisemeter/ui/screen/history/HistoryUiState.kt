package com.nkot117.noisemeter.ui.screen.history

sealed class HistoryUiState {
    object Initial : HistoryUiState()
    object Success : HistoryUiState()
    object Error : HistoryUiState()
}
