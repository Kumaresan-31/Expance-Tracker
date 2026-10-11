package com.example.expensetracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.theme.*
import kotlin.math.*

@Composable
fun ExpenseChart(
    categoryTotals: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val total = categoryTotals.values.sum()
    if (total == 0.0) return
    
    val colors = listOf(
        FoodColor, TransportColor, EntertainmentColor, 
        ShoppingColor, HealthColor, EducationColor, OtherColor
    )
    
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Expenses by Category",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            // Pie Chart
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = minOf(size.width, size.height) / 2f - 20f
                    
                    var startAngle = -90f
                    
                    categoryTotals.entries.forEachIndexed { index, (_, amount) ->
                        val sweepAngle = (amount / total) * 360f
                        val color = colors[index % colors.size]
                        
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle.toFloat(),
                            useCenter = true,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                        
                        startAngle += sweepAngle.toFloat()
                    }
                }
            }
            
            // Legend
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categoryTotals.entries.toList()) { (category, amount) ->
                    val percentage = (amount / total * 100).toInt()
                    val colorIndex = categoryTotals.keys.indexOf(category)
                    val color = colors[colorIndex % colors.size]
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = color,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        Text(
                            text = "$category ($percentage%)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
