package com.financialos.data.api

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.util.Properties
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import com.google.api.client.util.Base64 as GoogleBase64

class GmailService(private val context: Context) {

    private var gmailService: Gmail? = null

    fun initialize(accountName: String) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_SEND)
        )
        credential.selectedAccountName = accountName

        gmailService = Gmail.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Financial OS").build()
    }

    suspend fun sendEmail(to: String, subject: String, bodyText: String) = withContext(Dispatchers.IO) {
        val service = gmailService ?: return@withContext
        try {
            val email = createEmail(to, "me", subject, bodyText)
            var message = createMessageWithEmail(email)
            message = service.users().messages().send("me", message).execute()
            println("Email sent: ${message.id}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createEmail(to: String, from: String, subject: String, bodyText: String): MimeMessage {
        val props = Properties()
        val session = Session.getDefaultInstance(props, null)
        val email = MimeMessage(session)
        email.setFrom(InternetAddress(from))
        email.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
        email.subject = subject
        email.setText(bodyText)
        return email
    }

    private fun createMessageWithEmail(emailContent: MimeMessage): Message {
        val buffer = ByteArrayOutputStream()
        emailContent.writeTo(buffer)
        val bytes = buffer.toByteArray()
        val encodedEmail = Base64.encodeToString(bytes, Base64.URL_SAFE)
        val message = Message()
        message.raw = encodedEmail
        return message
    }
}
