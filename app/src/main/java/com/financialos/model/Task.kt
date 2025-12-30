package com.financialos.model

data class Task(
    val id: String,
    val title: String,
    val amount: Double,
    val dueDate: Long,
    val isCompleted: Boolean
)
