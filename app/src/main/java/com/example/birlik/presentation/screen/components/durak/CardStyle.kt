package com.example.birlik.presentation.screen.components.durak

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.birlik.R
import com.example.birlik.data.remote.durak.CardPair

@Composable
fun CardStyle(
    modifier: Modifier = Modifier,
    card: CardPair,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .width(50.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(id = R.color.grey))
            .clickable {
                onClick()
            }
    )
    {
        MirrorStyle(card = card)
        MirrorStyle(card = card, modifier = Modifier.rotate(180F))
    }
}

@Composable
private fun MirrorStyle(
    card: CardPair,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .padding(5.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            val displayedNumber = when (card.number) {
                21 -> "6"
                22 -> "7"
                23 -> "8"
                24 -> "9"
                25 -> "10"
                11, 26 -> "J"
                12, 27 -> "Q"
                13, 28 -> "K"
                14, 29 -> "A"
                else -> card.number.toString()
            }
            Text(text = displayedNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Image(
                modifier = Modifier.size(12.dp),
                painter = painterResource(id = card.suit ?: R.drawable.job),
                contentDescription = ""
            )
        }
    }
}


@Composable
fun PlayerCardStyle(
    modifier: Modifier = Modifier,
    card: CardPair,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))

            .background(colorResource(id = R.color.grey))
            .clickable {
                onClick()
            }
    )
    {
        PlayerMirrorStyle(card = card)
        PlayerMirrorStyle(card = card, modifier = Modifier.rotate(180F))
    }
}

@Composable
fun PlayerMirrorStyle(
    card: CardPair,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            val displayedNumber = when (card.number) {
                21 -> "6"
                22 -> "7"
                23 -> "8"
                24 -> "9"
                25 -> "10"
                11, 26 -> "J"
                12, 27 -> "Q"
                13, 28 -> "K"
                14, 29 -> "A"
                else -> card.number.toString()
            }
            Text(text = displayedNumber, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(id = card.suit ?: R.drawable.job),
                contentDescription = ""
            )
        }
    }
}