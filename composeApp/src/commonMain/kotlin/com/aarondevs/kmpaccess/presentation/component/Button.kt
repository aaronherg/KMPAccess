package com.aarondevs.kmpaccess.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderRadius: Int = 14,
    borderSize: Int = 1,
    verticalPadding: Int = 12,
    borderColor: Brush = Brush.linearGradient(listOf(Color.Cyan, Color.Magenta)),
    textSize: Int = 14,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                BorderStroke(borderSize.dp, borderColor),
                shape = RoundedCornerShape(borderRadius.dp)
            )
            .clickable(enabled = enabled) { if (enabled) onClick() }
            .padding(vertical = verticalPadding.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            fontSize = textSize.sp,
            color = if (enabled) Color.White else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
