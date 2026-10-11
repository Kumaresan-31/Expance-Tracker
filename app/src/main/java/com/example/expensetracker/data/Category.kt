package com.example.expensetracker.data

data class Category(
    val id: Long = 0,
    val name: String,
    val color: String, // Hex color code
    val icon: String // Icon name
)

