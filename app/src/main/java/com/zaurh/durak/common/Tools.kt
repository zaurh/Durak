package com.zaurh.durak.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.durak.R
import com.zaurh.durak.presentation.viewmodel.AuthViewModel
import kotlin.math.pow


fun Context.sendMail(to: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "vnd.android.cursor.item/email" // or "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        println("No email app.")
    } catch (t: Throwable) {
        println("Error occurred.")
    }
}

data class NavParam(
    val name: String,
    val value: Parcelable
)

fun navigateTo(navController: NavController, dest: String, vararg params: NavParam) {
    for (param in params) {
        navController.currentBackStackEntry?.arguments?.putParcelable(param.name, param.value)
    }
    navController.navigate(dest) {
        popUpTo(dest)
        launchSingleTop = true
    }
}


@Composable
fun MyCheckSignedIn(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = authViewModel.isSignedIn.value
    if (signedIn && !alreadyLoggedIn.value) {
        alreadyLoggedIn.value = true
        navController.navigate("durak_tables") {
            popUpTo(0)
        }
    }
}

@Composable
fun MyProgressBar() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) {}
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(color = Color.Blue)
    }
}


fun roundToNearestPowerOfTen(value: Int): Int {
    val magnitude = 10.0.pow(value.toString().length - 1)
    return (value / magnitude).toInt() * magnitude.toInt()
}

fun formatNumberWithK(value: Int): String {
    return when {
        value in 10000..999999 -> "${value / 1000}K"
        value >= 1000000 -> "${value / 1000000}M"
        else -> "$value"
    }
}

fun entryPriceCalculate(entryPrice: Long): Int {
    val sum = entryPrice - entryPrice * 0.2
    return sum.toInt()
}

fun willBeSoonToast(context: Context){
    Toast.makeText(context, context.getString(R.string.willBeSoon), Toast.LENGTH_SHORT).show()
}


@Composable
fun CustomSwitcher(
    switch: Boolean,
    size: Dp = 40.dp,
    firstIcon: String,
    secondIcon: String,
    padding: Dp = 5.dp,
    borderWidth: Dp = 1.dp,
    parentShape: RoundedCornerShape = CircleShape,
    toggleShape: RoundedCornerShape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300),
) {
    val offset by animateDpAsState(
        targetValue = if (!switch) 0.dp else size,
        animationSpec = animationSpec, label = ""
    )

    Box(
        modifier = Modifier
            .width(size * 2)
            .height(size)
            .clip(shape = parentShape)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = offset)
                .padding(all = padding)
                .clip(shape = toggleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shape = parentShape
                )
        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Text(text = firstIcon)
            }
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Text(text = secondIcon)
            }
        }
    }
}