package com.zaurh.durak.presentation.screen.components.durak

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zaurh.durak.R
import com.zaurh.durak.data.remote.durak.CardPair
import com.zaurh.durak.presentation.screen.components.DropTarget

@Composable
fun CardStyle(
    modifier: Modifier = Modifier,
    card: CardPair,
    onDropState: Boolean = false,
    onDrop: (CardPair) -> Unit = {}
) {
    DropTarget<CardPair>(modifier) { isInBound, cardPair ->
        val bgColor = if (isInBound && onDropState) {
            Color.Green
        } else {
            Color.Transparent
        }
        if (cardPair != null && onDropState) {
            onDrop(cardPair)
        }

        Column(
            modifier = modifier
                .width(80.dp)
                .height(120.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(10.dp)
                )
                .clip(RoundedCornerShape(10.dp))
                .border(2.dp, color = bgColor, shape = RoundedCornerShape(10.dp))
                .background(colorResource(id = R.color.grey))
        ) {
            Image(
                painter = painterResource(id = displayedNumber(card)),
                contentDescription = ""
            )
        }
    }
}


@Composable
fun EmptyCard(
    modifier: Modifier = Modifier,
    onDrop: (CardPair) -> Unit = {}
) {
    DropTarget<CardPair>(modifier) { isInBound, cardPair ->
        val bgColor = if (isInBound) {
            Color.Green
        } else {
            Color.Gray
        }
        if (cardPair != null) {
            onDrop(cardPair)
        }

        Box(
            modifier = modifier
                .width(80.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(2.dp, color = bgColor, shape = RoundedCornerShape(10.dp))
        )
    }
}


@Composable
fun PlayerCardStyle(
    modifier: Modifier = Modifier,
    card: CardPair
) {
    val d = LocalDensity.current
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .offset(
                    (offsetX / d.density).dp,
                    (offsetY / d.density).dp
                )
                .width(130.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(10.dp)
                )
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(id = R.color.grey))
        ) {
            Image(
                painter = painterResource(id = displayedNumber(card)),
                contentDescription = ""
            )
        }
    }
}



fun displayedNumber(card: CardPair): Int {
    val displayed = when {
        (card.number == 6 || card.number == 21) && card.suit == R.drawable.club -> R.drawable.six_club
        (card.number == 6 || card.number == 21) && card.suit == R.drawable.spade -> R.drawable.six_spade
        (card.number == 6 || card.number == 21) && card.suit == R.drawable.heart -> R.drawable.six_heart
        (card.number == 6 || card.number == 21) && card.suit == R.drawable.diamond -> R.drawable.six_diamond
        (card.number == 7 || card.number == 22) && card.suit == R.drawable.club -> R.drawable.seven_club
        (card.number == 7 || card.number == 22) && card.suit == R.drawable.spade -> R.drawable.seven_spade
        (card.number == 7 || card.number == 22) && card.suit == R.drawable.heart -> R.drawable.seven_heart
        (card.number == 7 || card.number == 22) && card.suit == R.drawable.diamond -> R.drawable.seven_diamond
        (card.number == 8 || card.number == 23) && card.suit == R.drawable.club -> R.drawable.eight_club
        (card.number == 8 || card.number == 23) && card.suit == R.drawable.spade -> R.drawable.eight_spade
        (card.number == 8 || card.number == 23) && card.suit == R.drawable.heart -> R.drawable.eight_heart
        (card.number == 8 || card.number == 23) && card.suit == R.drawable.diamond -> R.drawable.eight_diamond
        (card.number == 9 || card.number == 24) && card.suit == R.drawable.club -> R.drawable.nine_club
        (card.number == 9 || card.number == 24) && card.suit == R.drawable.spade -> R.drawable.nine_spade
        (card.number == 9 || card.number == 24) && card.suit == R.drawable.heart -> R.drawable.nine_heart
        (card.number == 9 || card.number == 24) && card.suit == R.drawable.diamond -> R.drawable.nine_diamond
        (card.number == 10 || card.number == 25) && card.suit == R.drawable.club -> R.drawable.ten_club
        (card.number == 10 || card.number == 25) && card.suit == R.drawable.spade -> R.drawable.ten_spade
        (card.number == 10 || card.number == 25) && card.suit == R.drawable.heart -> R.drawable.ten_heart
        (card.number == 10 || card.number == 25) && card.suit == R.drawable.diamond -> R.drawable.ten_diamond
        (card.number == 11 || card.number == 26) && card.suit == R.drawable.club -> R.drawable.j_club
        (card.number == 11 || card.number == 26) && card.suit == R.drawable.spade -> R.drawable.j_spade
        (card.number == 11 || card.number == 26) && card.suit == R.drawable.heart -> R.drawable.j_heart
        (card.number == 11 || card.number == 26) && card.suit == R.drawable.diamond -> R.drawable.j_diamond
        (card.number == 12 || card.number == 27) && card.suit == R.drawable.club -> R.drawable.q_club
        (card.number == 12 || card.number == 27) && card.suit == R.drawable.spade -> R.drawable.q_spade
        (card.number == 12 || card.number == 27) && card.suit == R.drawable.heart -> R.drawable.q_heart
        (card.number == 12 || card.number == 27) && card.suit == R.drawable.diamond -> R.drawable.q_diamond
        (card.number == 13 || card.number == 28) && card.suit == R.drawable.club -> R.drawable.k_clubs
        (card.number == 13 || card.number == 28) && card.suit == R.drawable.spade -> R.drawable.k_spades
        (card.number == 13 || card.number == 28) && card.suit == R.drawable.heart -> R.drawable.k_heart
        (card.number == 13 || card.number == 28) && card.suit == R.drawable.diamond -> R.drawable.k_diamond
        (card.number == 14 || card.number == 29) && card.suit == R.drawable.club -> R.drawable.a_club
        (card.number == 14 || card.number == 29) && card.suit == R.drawable.spade -> R.drawable.a_spade
        (card.number == 14 || card.number == 29) && card.suit == R.drawable.heart -> R.drawable.a_heart
        (card.number == 14 || card.number == 29) && card.suit == R.drawable.diamond -> R.drawable.a_diamond
        else -> card.suit
    }
    return displayed ?: R.drawable.job
}


