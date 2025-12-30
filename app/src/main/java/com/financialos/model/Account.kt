package com.financialos.model

enum class AccountType {
    SOURCE, HUB, SINK
}

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val lastUpdated: Long
)
