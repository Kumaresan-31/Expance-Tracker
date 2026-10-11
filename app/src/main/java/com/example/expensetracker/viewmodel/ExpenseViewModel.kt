package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ExpenseViewModel(val repository: SimpleExpenseRepository) : ViewModel() {
    
    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    
    val currentMonth = _currentMonth.asStateFlow()
    val currentYear = _currentYear.asStateFlow()
    
    val expenses = _currentMonth.combine(_currentYear) { month, year ->
        repository.getExpensesByMonth(month, year)
    }.flatMapLatest { flow -> flow }
    
    val budgets = repository.budgets
    
    val categories = repository.getAllCategories()
    
    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses = _totalExpenses.asStateFlow()
    
    private val _totalBudget = MutableStateFlow(0.0)
    val totalBudget = _totalBudget.asStateFlow()
    
    init {
        updateTotals()
    }
    
    fun setMonth(month: Int) {
        _currentMonth.value = month
        updateTotals()
    }
    
    fun setYear(year: Int) {
        _currentYear.value = year
        updateTotals()
    }
    
    private fun updateTotals() {
        viewModelScope.launch {
            val month = _currentMonth.value
            val year = _currentYear.value
            
            _totalExpenses.value = repository.getTotalExpensesForMonth(month, year)
            _totalBudget.value = repository.getTotalBudgetForMonth(month, year)?.amount ?: 0.0
        }
    }
    
    fun getExpenseById(id: Long) = repository.getExpenseById(id)

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insertExpense(expense)
            updateTotals()
        }
    }
    
    fun saveExpense(expense: Expense) {
        viewModelScope.launch {
            if (expense.id == 0L) {
                repository.insertExpense(expense)
            } else {
                repository.updateExpense(expense)
            }
            updateTotals()
        }
    }
    
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            updateTotals()
        }
    }
    
    fun addBudget(budget: Budget) {
        viewModelScope.launch {
            repository.insertBudget(budget)
            updateTotals()
        }
    }
    
}
