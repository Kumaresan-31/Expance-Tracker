package com.example.expensetracker.data

import java.util.Date

data class Expense(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val description: String? = null,
    val date: Date,
    val month: Int,
    val year: Int
)

