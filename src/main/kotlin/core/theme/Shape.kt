package com.oussama_chatri.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val WellLogShapes = Shapes(
    // Chips, badges, small tags
    extraSmall = RoundedCornerShape(4.dp),
    // Inputs, buttons, small cards
    small      = RoundedCornerShape(8.dp),
    // Standard cards and panels
    medium     = RoundedCornerShape(12.dp),
    // Large dialog panels
    large      = RoundedCornerShape(16.dp),
    // Bottom sheets, full panels
    extraLarge = RoundedCornerShape(20.dp)
)