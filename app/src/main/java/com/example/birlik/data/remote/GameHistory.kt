package com.example.birlik.data.remote

import com.example.birlik.R

data class GameHistory(
    val title: String = "",
    val winner: String = "",
    val loser: String = "",
    val game: String? = null,
    val fine: String? = null,
    val moneyIcon: Int? = null,
    val amount: String = "",
    val rating: String = "",
    val background: Int = R.drawable.background_blue
)
