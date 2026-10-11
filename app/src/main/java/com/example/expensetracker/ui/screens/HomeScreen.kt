package com.example.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.expensetracker.data.Expense
import com.example.expensetracker.ui.components.ExpenseCard
import com.example.expensetracker.ui.components.MonthYearSelector
import com.example.expensetracker.ui.components.SummaryCard
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ExpenseViewModel,
    onAddExpense: () -> Unit,
    onViewCharts: () -> Unit,
    onSettings: () -> Unit,
    onEditExpense: (Long) -> Unit = {}
) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val allExpenses by viewModel.repository.getAllExpenses().collectAsState(initial = emptyList())
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val totalBudget by viewModel.totalBudget.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val currentYear by viewModel.currentYear.collectAsState()

    val remainingBudget = totalBudget - totalExpenses
    val isOverBudget = remainingBudget < 0

    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    var exportAll by remember { mutableStateOf(false) }
    var exportFileName by remember { mutableStateOf("") }
    var exportMenuExpanded by remember { mutableStateOf(false) }

    fun String.csvEscaped(): String {
        val needsQuotes = contains(',') || contains('"') || contains('\n') || contains('\r')
        val escaped = replace("\"", "\"\"")
        return if (needsQuotes) "\"$escaped\"" else escaped
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter().use { writer ->
                    if (writer != null) {
                        writer.appendLine("Id,Title,Amount,Category,Description,Date,Month,Year")
                        val data = if (exportAll) allExpenses else expenses
                        data.forEach { e: Expense ->
                            val line = listOf(
                                e.id.toString(),
                                e.title.csvEscaped(),
                                e.amount.toString(),
                                e.category.csvEscaped(),
                                (e.description ?: "").csvEscaped(),
                                dateFormatter.format(e.date).csvEscaped(),
                                e.month.toString(),
                                e.year.toString()
                            ).joinToString(",")
                            writer.appendLine(line)
                        }
                        writer.flush()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Tracker") },
                actions = {
                    Box {
                        IconButton(onClick = { exportMenuExpanded = true }) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export")
                        }
                        DropdownMenu(
                            expanded = exportMenuExpanded,
                            onDismissRequest = { exportMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Export current month") },
                                onClick = {
                                    exportAll = false
                                    exportFileName = "expenses_${currentYear}_${String.format("%02d", currentMonth)}.csv"
                                    exportMenuExpanded = false
                                    exportLauncher.launch(exportFileName)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export all") },
                                onClick = {
                                    exportAll = true
                                    exportFileName = "expenses_all.csv"
                                    exportMenuExpanded = false
                                    exportLauncher.launch(exportFileName)
                                }
                            )
                        }
                    }
                    IconButton(onClick = onViewCharts) {
                        Icon(Icons.Default.Analytics, contentDescription = "Charts")
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpense,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MonthYearSelector(
                    currentMonth = currentMonth,
                    currentYear = currentYear,
                    onMonthChange = viewModel::setMonth,
                    onYearChange = viewModel::setYear
                )
            }

            item {
                SummaryCard(
                    totalExpenses = totalExpenses,
                    totalBudget = totalBudget,
                    remainingBudget = remainingBudget,
                    isOverBudget = isOverBudget
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Expenses",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (expenses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "No expenses yet",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Tap + to add your first expense",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                items(expenses) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onEdit = { onEditExpense(expense.id) },
                        onDelete = { viewModel.deleteExpense(expense) }
                    )
                }
            }
        }
    }
}
