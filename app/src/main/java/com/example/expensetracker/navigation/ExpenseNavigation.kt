package com.example.expensetracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.expensetracker.ui.screens.AddExpenseScreen
import com.example.expensetracker.ui.screens.BudgetScreen
import com.example.expensetracker.ui.screens.ChartsScreen
import com.example.expensetracker.ui.screens.HomeScreen
import com.example.expensetracker.viewmodel.ExpenseViewModel

@Composable
fun ExpenseNavigation(
    navController: NavHostController,
    viewModel: ExpenseViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onAddExpense = {
                    navController.navigate("add_expense")
                },
                onViewCharts = {
                    navController.navigate("charts")
                },
                onSettings = {
                    navController.navigate("budget")
                },
                onEditExpense = { id ->
                    navController.navigate("edit_expense/$id")
                }
            )
        }
        
        composable("add_expense") {
            AddExpenseScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "edit_expense/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            AddExpenseScreen(
                viewModel = viewModel,
                expenseId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("charts") {
            ChartsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("budget") {
            BudgetScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
