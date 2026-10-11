package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun MonthYearSelector(
    currentMonth: Int,
    currentYear: Int,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit
) {
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    
    var showMonthMenu by remember { mutableStateOf(false) }
    var showYearMenu by remember { mutableStateOf(false) }
    
    val currentYearRange = (currentYear - 5)..(currentYear + 5)
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Month Selector
            Box {
                OutlinedButton(
                    onClick = { showMonthMenu = true }
                ) {
                    Text(months[currentMonth - 1])
                }
                
                DropdownMenu(
                    expanded = showMonthMenu,
                    onDismissRequest = { showMonthMenu = false }
                ) {
                    months.forEachIndexed { index, month ->
                        DropdownMenuItem(
                            text = { Text(month) },
                            onClick = {
                                onMonthChange(index + 1)
                                showMonthMenu = false
                            }
                        )
                    }
                }
            }
            
            // Year Selector
            Box {
                OutlinedButton(
                    onClick = { showYearMenu = true }
                ) {
                    Text(currentYear.toString())
                }
                
                DropdownMenu(
                    expanded = showYearMenu,
                    onDismissRequest = { showYearMenu = false }
                ) {
                    currentYearRange.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.toString()) },
                            onClick = {
                                onYearChange(year)
                                showYearMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

