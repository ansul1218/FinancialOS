package com.financialos.data.repository

import com.financialos.data.api.GmailService
import com.financialos.data.api.SheetsService
import com.financialos.model.Account
import com.financialos.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FinancialRepository(
    private val sheetsService: SheetsService,
    private val gmailService: GmailService
) {

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    suspend fun syncData() {
        _isLoading.value = true
        val remoteAccounts = sheetsService.getAccounts()
        val remoteTransactions = sheetsService.getTransactions()
        
        _accounts.value = remoteAccounts
        _transactions.value = remoteTransactions
        _isLoading.value = false
    }

    fun initializeServices(accountName: String) {
        sheetsService.initialize(accountName)
        gmailService.initialize(accountName)
    }
    
    fun setSpreadsheetId(id: String) {
        sheetsService.spreadsheetId = id
    }
}
