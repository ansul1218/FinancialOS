package com.financialos

import android.app.Application
import com.financialos.data.api.GmailService
import com.financialos.data.api.SheetsService
import com.financialos.data.auth.GoogleAuthClient
import com.financialos.data.repository.FinancialRepository

class FinancialApplication : Application() {

    lateinit var googleAuthClient: GoogleAuthClient
    lateinit var repository: FinancialRepository

    override fun onCreate() {
        super.onCreate()
        googleAuthClient = GoogleAuthClient(this)
        
        val sheetsService = SheetsService(this)
        val gmailService = GmailService(this)
        
        repository = FinancialRepository(sheetsService, gmailService)
    }
}
