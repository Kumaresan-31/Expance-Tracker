package com.example.expensetracker.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class SimpleExpenseRepository {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: Flow<List<Expense>> = _expenses.asStateFlow()
    
    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: Flow<List<Budget>> = _budgets.asStateFlow()
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: Flow<List<Category>> = _categories.asStateFlow()
    
    init {
        // Initialize with some default categories
        _categories.value = listOf(
            Category(1, "Food", "#FF9800", "restaurant"),
            Category(2, "Transport", "#2196F3", "directions_car"),
            Category(3, "Entertainment", "#9C27B0", "movie"),
            Category(4, "Shopping", "#E91E63", "shopping_bag"),
            Category(5, "Health", "#4CAF50", "local_hospital"),
            Category(6, "Education", "#3F51B5", "school"),
            Category(7, "Other", "#607D8B", "more_horiz")
        )
    }
    
    fun getAllExpenses(): Flow<List<Expense>> = expenses
    
    fun getExpensesByMonth(month: Int, year: Int): Flow<List<Expense>> {
        return MutableStateFlow(
            _expenses.value.filter { it.month == month && it.year == year }
        )
    }
    
    fun getExpenseById(id: Long): Expense? {
        return _expenses.value.firstOrNull { it.id == id }
    }
    
    suspend fun insertExpense(expense: Expense) {
        val currentList = _expenses.value.toMutableList()
        currentList.add(expense.copy(id = currentList.size.toLong() + 1))
        _expenses.value = currentList
    }
    
    suspend fun updateExpense(expense: Expense) {
        val currentList = _expenses.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == expense.id }
        if (index >= 0) {
            currentList[index] = expense
            _expenses.value = currentList
        }
    }
    
    suspend fun deleteExpense(expense: Expense) {
        val currentList = _expenses.value.toMutableList()
        currentList.removeAll { it.id == expense.id }
        _expenses.value = currentList
    }
    
    suspend fun getTotalExpensesForMonth(month: Int, year: Int): Double {
        return _expenses.value
            .filter { it.month == month && it.year == year }
            .sumOf { it.amount }
    }
    
    suspend fun getTotalBudgetForMonth(month: Int, year: Int): Budget? {
        return _budgets.value.find { it.month == month && it.year == year }
    }
    
    suspend fun insertBudget(budget: Budget) {
        val currentList = _budgets.value.toMutableList()
        // Remove existing budget for the same month/year
        currentList.removeAll { it.month == budget.month && it.year == budget.year }
        currentList.add(budget.copy(id = currentList.size.toLong() + 1))
        _budgets.value = currentList
    }
    
    fun getAllCategories(): Flow<List<Category>> = categories
}
