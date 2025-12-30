package com.financialos.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financialos.model.Task

@Composable
fun TaskBoardScreen() {
    val tasks = mockTasks()
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Financial Tasks",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp,
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(tasks) { task ->
                TaskCard(task)
            }
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    val cardColor = if (task.isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val textColor = if (task.isCompleted) Color(0xFF2E7D32) else Color(0xFFC62828)

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹ ${task.amount}",
                style = MaterialTheme.typography.headlineSmall,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Due: 2 Days", // Mock relative time
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (!task.isCompleted) {
                TextButton(
                    onClick = { /* Pay Action */ },
                    colors = ButtonDefaults.textButtonColors(contentColor = textColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("PAY NOW")
                }
            }
        }
    }
}

fun mockTasks() = listOf(
    Task("1", "Electricity Bill", 1240.0, 0, false),
    Task("2", "SIP Investment", 5000.0, 0, false),
    Task("3", "Credit Card Bill", 12500.0, 0, false),
    Task("4", "House Rent", 18000.0, 0, true),
    Task("5", "Netflix", 649.0, 0, false)
)
