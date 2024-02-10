package com.example.birlik.presentation.screen.components.durak

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.birlik.R
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CardStyle(
    modifier: Modifier = Modifier,
    card: CardPair,
    userViewModel: UserViewModel = hiltViewModel(),
    onClick: () -> Unit = {},
) {
    val manyCardLeft = userViewModel.manyCardLeft.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current

    val offsetX = remember { Animatable(0f) }

    if(manyCardLeft.value){
        animateText(offsetX, coroutineScope, view)
    }

    Column(
        modifier = modifier
            .offset(offsetX.value.dp, 0.dp)
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

        Image(painter = painterResource(id = displayedNumber(card) ?: R.drawable.job), contentDescription = "")
//        MirrorStyle(card = card)
//        MirrorStyle(card = card, modifier = Modifier.rotate(180F))
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

            Image(
                modifier = Modifier.size(14.dp),
                painter = painterResource(id = displayedNumber(card)),
                contentDescription = ""
            )

//
//            val displayedNumber = when (card.number) {
//                21 -> "6"
//                22 -> "7"
//                23 -> "8"
//                24 -> "9"
//                25 -> "10"
//                11, 26 -> "J"
//                12, 27 -> "Q"
//                13, 28 -> "K"
//                14, 29 -> "A"
//                else -> card.number.toString()
//            }
//            Text(
//                text = displayedNumber,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//            Image(
//                modifier = Modifier.size(14.dp),
//                painter = painterResource(id = card.suit ?: R.drawable.job),
//                contentDescription = ""
//            )
        }
    }
}


@Composable
fun PlayerCardStyle(
    modifier: Modifier = Modifier,
    card: CardPair,
    onClick: () -> Unit = {},
    selectedCard: CardPair?,
    onCardSelected: (CardPair) -> Unit,
    onInactiveClick: () -> Unit = {}
) {
    val clicked = selectedCard == card

    Column(
        modifier = modifier
            .offset(y = if (clicked) ((-10).dp) else 60.dp)
            .width(130.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(id = R.color.grey))
            .clickable {
                val newSelectedCard =
                    if (clicked) null
                    else card
                onCardSelected(newSelectedCard ?: CardPair())
                if (clicked) {
                    onInactiveClick()
                } else {
                    onClick()
                }
            }
    )
    {
        Image(painter = painterResource(id = displayedNumber(card) ?: R.drawable.job), contentDescription = "")

//        PlayerMirrorStyle(card = card)
//        PlayerMirrorStyle(card = card, modifier = Modifier.rotate(180F))
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
            Text(
                text = displayedNumber,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(id = card.suit ?: R.drawable.job),
                contentDescription = ""
            )
        }
    }
}



private val shakeKeyframes: AnimationSpec<Float> = keyframes {
    durationMillis = 800
    val easing = FastOutLinearInEasing

    // generate 8 keyframes
    for (i in 1..8) {
        val x = when (i % 3) {
            0 -> 4f
            1 -> -4f
            else -> 0f
        }
        x at durationMillis / 10 * i with easing
    }
}

private fun animateText(
    offset: Animatable<Float, AnimationVector1D>,
    coroutineScope: CoroutineScope,
    view: View? = null,
) {
    coroutineScope.launch {
        offset.animateTo(
            targetValue = 0f,
            animationSpec = shakeKeyframes,
        )
    }
    view?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }
}

private fun displayedNumber(card: CardPair): Int {
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


