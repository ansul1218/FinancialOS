package com.financialos.data.api

import android.content.Context
import com.financialos.model.Account
import com.financialos.model.AccountType
import com.financialos.model.Transaction
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SheetsService(private val context: Context) {

    private var sheetsService: Sheets? = null
    // You should allow the user to configure this ID, or store it in preferences. 
    // For now we'll assume a fixed ID or require setup.
    var spreadsheetId: String? = null 

    fun initialize(accountName: String) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(SheetsScopes.SPREADSHEETS)
        )
        credential.selectedAccountName = accountName

        sheetsService = Sheets.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Financial OS").build()
    }

    suspend fun getAccounts(): List<Account> = withContext(Dispatchers.IO) {
        val service = sheetsService ?: return@withContext emptyList()
        val sheetId = spreadsheetId ?: return@withContext emptyList()

        try {
            val response: ValueRange = service.spreadsheets().values()
                .get(sheetId, "Accounts!A2:E") // Assuming Headers are A1:E1
                .execute()

            val values = response.getValues() ?: return@withContext emptyList()
            
            values.mapNotNull { row ->
                if (row.size < 5) return@mapNotNull null
                try {
                    Account(
                        id = row[0].toString(),
                        name = row[1].toString(),
                        type = AccountType.valueOf(row[2].toString().uppercase()),
                        balance = row[3].toString().replace(",", "").toDoubleOrNull() ?: 0.0,
                        lastUpdated = row[4].toString().toLongOrNull() ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        val service = sheetsService ?: return@withContext emptyList()
        val sheetId = spreadsheetId ?: return@withContext emptyList()

        try {
            val response: ValueRange = service.spreadsheets().values()
                .get(sheetId, "Transactions!A2:F")
                .execute()

            val values = response.getValues() ?: return@withContext emptyList()

            values.mapNotNull { row ->
                 if (row.size < 6) return@mapNotNull null
                 try {
                     Transaction(
                         id = row[0].toString(),
                         fromAccountId = row[1].toString(),
                         toAccountId = row[2].toString(),
                         amount = row[3].toString().replace(",", "").toDoubleOrNull() ?: 0.0,
                         date = row[4].toString().toLongOrNull() ?: System.currentTimeMillis(),
                         description = row[5].toString()
                     )
                 } catch (e: Exception) {
                     null
                 }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
