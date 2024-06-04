@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.zaurh.durak.presentation.screen.components

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.zaurh.durak.R
import com.zaurh.durak.common.roundToNearestPowerOfTen
import com.zaurh.durak.common.willBeSoonToast
import com.zaurh.durak.data.remote.UserData
import com.zaurh.durak.data.remote.durak.Rules
import com.zaurh.durak.presentation.screen.auth.components.AuthTextField
import com.zaurh.durak.presentation.screen.components.durak.DurakTextField
import com.zaurh.durak.presentation.viewmodel.DurakViewModel
import com.zaurh.durak.presentation.viewmodel.StorageViewModel
import com.zaurh.durak.presentation.viewmodel.UserViewModel

@Composable
fun CreateTableAlert(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    userViewModel: UserViewModel,
    durakViewModel: DurakViewModel,
    userData: UserData
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var perevod by remember { mutableStateOf(true) }
    var cheat by remember { mutableStateOf(false) }

    var table1 by remember { mutableStateOf(true) }
    var table2 by remember { mutableStateOf(true) }
    var table3 by remember { mutableStateOf(false) }
    var table4 by remember { mutableStateOf(false) }

    val tableSize = listOf(table1, table2, table3, table4).count { it }

    var tablePrice by remember { mutableStateOf("") }


    Dialog(
        onDismissRequest = { onDismiss() }, content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .clip(shape = RoundedCornerShape(10))
            ) {
                val focus = LocalFocusManager.current
                var pickedSkin by remember { mutableStateOf(userData.skinSettings?.backgroundPicked?.image) }
                var moneyMethod by remember { mutableStateOf(false) }

                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(
                        id = pickedSkin ?: R.drawable.background_green
                    ),
                    contentDescription = ""
                )

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(id = R.string.nameOfTable), color = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    DurakTextField(
                        value = title,
                        onValueChange = { title = it },
                        onDone = {
                            focus.clearFocus()
                        },
                        placeHolder = stringResource(id = R.string.nameforgame)
                    )

                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(id = R.string.rules), color = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    Row() {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = "",
                                tint = Color.Black
                            )
                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        perevod = !perevod
                                    },
                                painter = painterResource(id = R.drawable.rule_perevod),
                                contentDescription = "",
                                tint = if (perevod) Color.Green else Color.Gray
                            )
                        }
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = "",
                                tint = Color.Black
                            )

                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        willBeSoonToast(context)
                                    },
                                painter = painterResource(id = R.drawable.rule_cheat),
                                contentDescription = "",
                                tint = if (cheat) Color.Green else Color.Gray

                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(id = R.string.numberOfTables), color = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    Row() {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = "",
                                tint = Color.Black
                            )

                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        willBeSoonToast(context)
                                    },
                                painter = painterResource(id = R.drawable.tableskin_black_simple),
                                contentDescription = "",
                                tint = if (table1) Color.Green else Color.Gray
                            )
                        }
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = "",
                                tint = Color.Black
                            )

                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        willBeSoonToast(context)
                                    },
                                painter = painterResource(id = R.drawable.tableskin_black_simple),
                                contentDescription = "",
                                tint = if (table2) Color.Green else Color.Gray
                            )
                        }
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = "",
                                tint = Color.Black
                            )

                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        willBeSoonToast(context)
                                    },
                                painter = painterResource(id = R.drawable.tableskin_black_simple),
                                contentDescription = "",
                                tint = if (table3) Color.Green else Color.Gray
                            )
                        }
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = "",
                                tint = Color.Black
                            )

                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        willBeSoonToast(context)
                                    },
                                painter = painterResource(id = R.drawable.tableskin_black_simple),
                                contentDescription = "",
                                tint = if (table4) Color.Green else Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(id = R.string.entryPrice), color = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    DurakTextField(
                        value = tablePrice,
                        onValueChange = {
                            if (it.length <= 18) {
                                tablePrice = it.filter { it.isDigit() }
                            }
                        },
                        onDone = {
                            focus.clearFocus()
                        },
                        placeHolder = if (tablePrice == "") stringResource(
                            id = R.string.free
                        ) else "",
                        leadingIcon = painterResource(id = if (moneyMethod) R.drawable.birlik_coin else R.drawable.birlik_cash),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        changeMoney = {
                            moneyMethod = !moneyMethod
                            tablePrice = ""
                        }
                    )
                    val cash = roundToNearestPowerOfTen(userData.cash ?: 0).toFloat()
                    val coin = roundToNearestPowerOfTen(userData.coin ?: 0).toFloat()

                    Slider(
                        colors = SliderDefaults.colors(
                            thumbColor = Color.LightGray
                        ),
                        steps = 4, // Set the number of desired steps
                        valueRange = 0f..if (moneyMethod) coin else cash,
                        value = if (tablePrice == "") 0.toFloat() else tablePrice.toFloat(),
                        onValueChange = {
                            tablePrice = it.toInt().toString()
                        }
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(id = R.string.background), color = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        val backgroundSkins = userData.skinSettings?.ownBackgroundSkins

                        for (skin in backgroundSkins ?: mutableListOf()) {
                            Image(modifier = Modifier
                                .clickable {
                                    pickedSkin = skin.image
                                    userData.let {
                                        userViewModel.changeSkin(
                                            userData = it.copy(
                                                skinSettings = it.skinSettings?.copy(
                                                    backgroundPicked = skin
                                                )
                                            ),
                                        )
                                    }
                                }
                                .border(
                                    width = 1.dp,
                                    color = if (pickedSkin == skin.image) Color.White else Color.Transparent
                                )
                                .padding(8.dp),
                                painter = painterResource(
                                    id = skin.image ?: R.drawable.background_green
                                ),
                                contentDescription = "")
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        onClick = {
                            val sufficientFunds = if (moneyMethod) {
                                userData.coin >= if (tablePrice.isEmpty()) 0 else tablePrice.toLong()
                            } else {
                                userData.cash >= if (tablePrice.isEmpty()) 0 else tablePrice.toLong()
                            }
                            if (sufficientFunds) {
                                durakViewModel.startGame(
                                    title = title.ifEmpty { context.getString(R.string.unnamedTable) },
                                    rules = Rules(cheat, tableSize, perevod),
                                    entryPriceCash = if (!moneyMethod) if (tablePrice == "") 0 else tablePrice.toLong() else 0,
                                    entryPriceCoin = if (moneyMethod) if (tablePrice == "") 0 else tablePrice.toLong() else 0,
                                    context = context
                                )
                                onSuccess()
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.notEnoughMoney),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }) {
                        Text(text = stringResource(id = R.string.start), color = Color.White)
                    }

                }
            }


        })
}

@Composable
fun FlagAlert(
    enabled: Boolean = true, onDismiss: () -> Unit, yes: () -> Unit, no: () -> Unit
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.tertiary,
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(id = R.string.doYouWantToGiveUp),
                color = MaterialTheme.colorScheme.primary
            )
        },
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.background
            ), onClick = {
                no()
            }) {
                Text(text = stringResource(id = R.string.no), color = Color.White)
            }
        },
        dismissButton = {
            Button(colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.background
            ), onClick = {
                if (enabled) {
                    yes()
                }
            }) {
                Text(text = stringResource(id = R.string.yes), color = Color.White)
            }
        })
}

@Composable
fun ResultAlert(
    win: Boolean,
    rating: String,
    amount: String,
    moneyIcon: Int,
    onDismiss: () -> Unit,
    close: () -> Unit
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.tertiary,
        onDismissRequest = { onDismiss() },
        title = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = if (win) "\uD83C\uDF8A ${stringResource(id = R.string.win)} \uD83C\uDF8A" else stringResource(
                        id = R.string.lose
                    ).uppercase(), color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column() {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            modifier = Modifier.size(22.dp),
                            painter = painterResource(id = moneyIcon),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "${if (win) "+" else "-"}$amount",
                            color = if (win) colorResource(id = R.color.green) else MaterialTheme.colorScheme.onTertiary,
                            fontSize = 22.sp
                        )
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.size(22.dp),
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "",
                            tint = colorResource(id = R.color.yellow)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "${if (win) "+" else "-"}$rating",
                            color = if (win) colorResource(id = R.color.green) else MaterialTheme.colorScheme.onTertiary,
                            fontSize = 22.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                ), onClick = {
                    close()
                }) {
                Text(text = stringResource(id = R.string.close), color = Color.White)
            }
        })
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EditProfileAlert(
    userData: UserData,
    storageViewModel: StorageViewModel,
    userViewModel: UserViewModel,
    onDone: () -> Unit,
    onDismiss: () -> Unit
) {
    val userImage = remember { mutableStateOf(userData.image) }
    val email = remember { mutableStateOf(userData.email ?: "") }
    val name = remember { mutableStateOf(userData.name ?: "") }
    val isMediaLoading = storageViewModel.isMediaLoading.value

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val context = LocalContext.current as Activity

    val imageCropLauncher =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let {
                    //getBitmap method is deprecated in Android SDK 29 or above so we need to do this check here
                    bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder
                            .createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                    it.let {
                        storageViewModel.uploadMedia(it, "images") { image ->
                            userImage.value = image.toString()
                        }
                    }
                }
            } else {
                //If something went wrong you can handle the error here
                println("ImageCropping error: ${result.error}")
            }
        }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        text = {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .clickable {
                                val cropOptions = CropImageContractOptions(
                                    null,
                                    CropImageOptions(imageSourceIncludeCamera = false)
                                )
                                imageCropLauncher.launch(cropOptions)
                            },
                        contentScale = ContentScale.Crop,
                        painter = rememberImagePainter(data = userImage.value.takeUnless { it.isNullOrEmpty() }
                            ?: R.drawable.empty_profile),
                        contentDescription = "",
                        alpha = if (isMediaLoading) 0.2f else 1f
                    )
                    if (isMediaLoading) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.size(8.dp))
                Text(text = email.value, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.size(16.dp))
                AuthTextField(
                    value = name.value,
                    onValueChange = { if (it.length <= 25) name.value = it },
                    onDone = {
                        onDone()
                    },
                    placeHolder = stringResource(id = R.string.username)
                )
            }
        }, onDismissRequest = { onDismiss() }, confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.background
                ),
                onClick = {
                    userViewModel.updateUserData(
                        username = name.value,
                        imageUrl = userImage.value ?: ""
                    )
                    onDismiss()
                }) {
                Text(text = stringResource(id = R.string.save), color = Color.White)
            }
        })

}


@Composable
fun WhatsNewAlert(
    version: String,
    text: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(id = R.string.whatsnew) + " $version",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(text = text, color = colorResource(id = R.color.green))
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                onClick = {
                    onDismiss()
                }) {
                Text(text = stringResource(id = R.string.close), color = Color.White)
            }
        }
    )
}


@Composable
fun PromoAlert(
    onDismiss: () -> Unit,
    userViewModel: UserViewModel
) {
    var promoTf by remember { mutableStateOf("") }
    val focus = LocalFocusManager.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(id = R.string.enterPromo),
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            AuthTextField(
                value = promoTf,
                onValueChange = { promoTf = it },
                onDone = {
                    focus.clearFocus()
                },
                placeHolder = stringResource(id = R.string.enterPromo)
            )
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                onClick = {
                    userViewModel.getPromo(
                        code = promoTf.uppercase(),
                        context = context
                    )
                }) {
                Text(text = stringResource(id = R.string.send), color = Color.White)
            }
        },
    )
}



