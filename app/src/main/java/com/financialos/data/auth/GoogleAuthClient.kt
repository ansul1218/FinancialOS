package com.financialos.data.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.sheets.v4.SheetsScopes

class GoogleAuthClient(private val context: Context) {

    fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .requestScopes(Scope(GmailScopes.GMAIL_SEND))
            .requestScopes(Scope(GmailScopes.GMAIL_READONLY))
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun getSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun getSignInIntent(): Intent {
        return getSignInClient().signInIntent
    }

    fun signOut(onComplete: () -> Unit) {
        getSignInClient().signOut().addOnCompleteListener { onComplete() }
    }
}
