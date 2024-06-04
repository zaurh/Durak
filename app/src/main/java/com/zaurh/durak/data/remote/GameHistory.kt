package com.zaurh.durak.data.remote

import com.zaurh.durak.R

data class GameHistory(
    val title: String = "",
    val winner: String = "",
    val winnerId: String = "",
    val loser: String = "",
    val loserId: String = "",
    val game: String? = null,
    val fine: String? = null,
    val moneyIcon: Int? = null,
    val amount: String = "",
    val rating: String = "",
    val background: Int = R.drawable.background_blue
)
