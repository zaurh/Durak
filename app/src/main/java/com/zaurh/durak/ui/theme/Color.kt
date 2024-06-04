package com.zaurh.durak.ui.theme

import androidx.compose.ui.graphics.Color

sealed class ThemeColors(
    val background: Color,
    val messageBackground: Color,
    val surface: Color,
    val title: Color,
    val text: Color,
    val secondBackground: Color,
    val authColor: Color,
    val red: Color,
    val yellow: Color,
) {
    object Night : ThemeColors(
        background = Color(0xFF272727),
        messageBackground = Color(0xFF212121),
        surface = Color(0xFF212121),
        title = Color(0xFFFFFFFF),
        text = Color.Gray,
        secondBackground = Color(0xFF313131),
        red = Color(0xFFFF6D6D),
        yellow = Color(0xFF5E5342),
        authColor = Color(0xFF4DBE86)
    )

    object Day : ThemeColors(
        background = Color(0xFF4DBE86),
        messageBackground = Color(0xFFEBFFF9),
        surface = Color(0xFFEBFFF9),
        title = Color(0xFF000000),
        text = Color.Gray,
        secondBackground = Color(0xFFE3F2FD),
        red = Color(0xFFFF3D3D),
        yellow = Color(0xFFFFECCC),
        authColor = Color(0xFF4DBE86)
    )
}