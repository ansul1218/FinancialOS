package com.financialos.ui.moneymap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.financialos.model.Account
import com.financialos.model.AccountType

// Simple Edge model for visualization
data class FlowEdge(
    val start: Offset,
    val end: Offset,
    val amount: Double,
    val strokeWidth: Float
)

data class Node(
    val id: String,
    val name: String,
    val position: Offset,
    val type: AccountType,
    val color: Color
)

@Composable
fun MoneyMapGraph(
    accounts: List<Account>,
    modifier: Modifier = Modifier
) {
    if (accounts.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary

    // Layout Logic (Simplified Force Layout or Fixed Columns)
    val nodes = remember(accounts) {
        val grouped = accounts.groupBy { it.type }
        val sources = grouped[AccountType.SOURCE] ?: emptyList()
        val hubs = grouped[AccountType.HUB] ?: emptyList()
        val sinks = grouped[AccountType.SINK] ?: emptyList()

        val nodeList = mutableListOf<Node>()

        // Fixed Columns Layout:
        // Sources: X = 0.2, Y = Distributed
        // Hubs: X = 0.5, Y = Distributed
        // Sinks: X = 0.8, Y = Distributed
        
        fun layoutColumn(items: List<Account>, xPercent: Float) {
            val count = items.size
            items.forEachIndexed { index, account ->
                val yPercent = (index + 1) / (count + 1).toFloat()
                val color = when(account.type) {
                    AccountType.SOURCE -> Color(0xFF00C853) // Green
                    AccountType.HUB -> Color(0xFF2962FF) // Blue
                    AccountType.SINK -> Color(0xFFFF6D00) // Orange
                }
                nodeList.add(Node(
                    id = account.id,
                    name = account.name,
                    position = Offset(xPercent, yPercent), // Normalized coordinates
                    type = account.type,
                    color = color
                ))
            }
        }

        layoutColumn(sources, 0.2f)
        layoutColumn(hubs, 0.5f)
        layoutColumn(sinks, 0.8f)

        nodeList
    }

    // Mock Edges since we don't have transaction aggregation logic yet
    // In real app, aggregate [Transactions] to build edges
    val edges = remember(nodes) {
        val edgeList = mutableListOf<FlowEdge>()
        
        // Example flows
        // All Sources -> First Hub
        val firstHub = nodes.find { it.type == AccountType.HUB }
        nodes.filter { it.type == AccountType.SOURCE }.forEach { source ->
             firstHub?.let { hub ->
                 edgeList.add(FlowEdge(source.position, hub.position, 100000.0, 10f))
             }
        }
        
        // First Hub -> All Sinks
        nodes.filter { it.type == AccountType.SINK }.forEach { sink ->
            firstHub?.let { hub ->
                 edgeList.add(FlowEdge(hub.position, sink.position, 20000.0, 5f))
            }
        }
        
        edgeList
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Draw Edges
        edges.forEach { edge ->
            val start = Offset(edge.start.x * width, edge.start.y * height)
            val end = Offset(edge.end.x * width, edge.end.y * height)
            
            // Curved Path
            val path = Path().apply {
                moveTo(start.x, start.y)
                // Control points for cubic bezier
                val c1 = Offset(start.x + (end.x - start.x) / 2, start.y)
                val c2 = Offset(start.x + (end.x - start.x) / 2, end.y)
                cubicTo(c1.x, c1.y, c2.x, c2.y, end.x, end.y)
            }
            
            drawPath(
                path = path,
                color = onSurface.copy(alpha = 0.2f),
                style = Stroke(width = edge.strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Draw Nodes
        nodes.forEach { node ->
            val center = Offset(node.position.x * width, node.position.y * height)
            
            // Node Circle
            drawCircle(
                color = node.color,
                radius = 20f,
                center = center
            )
            
            // Node Label
            val textLayoutResult = textMeasurer.measure(
                text = node.name,
                style = TextStyle(color = onSurface, fontSize = 12.sp)
            )
            
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(center.x - textLayoutResult.size.width / 2, center.y + 25f)
            )
        }
    }
}
