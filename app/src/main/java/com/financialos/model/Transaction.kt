package com.financialos.model

data class Transaction(
    val id: String,
    val fromAccountId: String,
    val toAccountId: String,
    val amount: Double,
    val date: Long,
    val description: String
)
