package com.tracker.scotmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tracker.scotmobile.data.local.repository.ResultCodeLocalRepository
import com.tracker.scotmobile.data.local.repository.OrderServiceLocalRepository

class SyncViewModelFactory(
    private val resultCodeLocalRepository: ResultCodeLocalRepository,
    private val orderServiceLocalRepository: OrderServiceLocalRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SyncViewModel::class.java)) {
            return SyncViewModel(resultCodeLocalRepository, orderServiceLocalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
