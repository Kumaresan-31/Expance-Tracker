package com.example.expensetracker.data

data class Budget(
    val id: Long = 0,
    val month: Int,
    val year: Int,
    val amount: Double,
    val category: String? = null // null means total budget
)

