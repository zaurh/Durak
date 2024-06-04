@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package com.zaurh.durak.presentation.screen


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.zaurh.durak.R
import com.zaurh.durak.common.formatNumberWithK
import com.zaurh.durak.data.remote.GameHistory
import com.zaurh.durak.data.remote.durak.Skin
import com.zaurh.durak.presentation.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DurakSettings(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val userData = userViewModel.userDataState.collectAsState()
    val skinSettings = userData.value?.skinSettings

    val ownTableSkins = skinSettings?.ownTableSkins
    val ownCardBackSkins = skinSettings?.ownCardBackSkins
    val ownBackgroundSkins = skinSettings?.ownCardBackSkins
    val trophySkins = skinSettings?.trophyTableSkins

    val allOwnSkins: List<Skin> = listOfNotNull(
        ownTableSkins,
        ownCardBackSkins,
        ownBackgroundSkins
    ).flatten()

    var tableSkinAlert by remember { mutableStateOf(false) }
    var cardBackSkinAlert by remember { mutableStateOf(false) }
    var backgroundSkinAlert by remember { mutableStateOf(false) }
    var achievementInfoAlert by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var rewardedAd: RewardedAd? = null
    val btnText = remember { mutableStateOf("Loading Rewarded Ad") }
    val btnEnable = remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        userData.value?.let { userData ->
            val achievableRatingSkins = trophySkins?.filter {
                it.rating != 0 && it.rating <= userData.rating
            }

            val achievableDurakWinSkins = trophySkins?.filter {
                it.winCount != 0 && it.winCount <= userData.durakWinCount
            }

            val updateOwnSkins = userData.skinSettings?.ownTableSkins

            val ratingSkinsToAdd = achievableRatingSkins?.filterNot {
                updateOwnSkins?.contains(it) == true
            }
            val winSkinsToAdd = achievableDurakWinSkins?.filterNot {
                updateOwnSkins?.contains(it) == true
            }

            if (ratingSkinsToAdd.isNullOrEmpty().not()) {
                updateOwnSkins?.addAll(ratingSkinsToAdd ?: mutableListOf())
            }

            if (winSkinsToAdd.isNullOrEmpty().not()) {
                updateOwnSkins?.addAll(winSkinsToAdd ?: mutableListOf())
            }

            userData.let { user ->
                userViewModel.changeSkin(
                    userData = user.copy(
                        skinSettings = user.skinSettings?.copy(
                            ownTableSkins = updateOwnSkins
                        )
                    )
                )
            }
        }

    }

    fun loadRewardedAd(context: Context) {
        RewardedAd.load(
            context,
            "ca-app-pub-8423110392314429/4313001051",
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    btnText.value = "Show rewarded Ad"
                    btnEnable.value = true
                }
            })
    }

    fun showRewardedAd(context: Context, onAdDismissed: () -> Unit) {
        if (rewardedAd != null) {
            rewardedAd!!.show(context as Activity) {
                userViewModel.rewardUser()
                Toast.makeText(
                    context,
                    context.getString(R.string.rewardSuccessful),
                    Toast.LENGTH_SHORT
                ).show()
                loadRewardedAd(context)
                onAdDismissed()
                rewardedAd = null

                btnText.value = "Loading rewarded Ad"
                btnEnable.value = false
            }
        }
    }

    loadRewardedAd(context)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                }, title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = R.drawable.birlik_cash),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = userData.value?.cash.toString(),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Image(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = R.drawable.birlik_coin),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = userData.value?.coin.toString(),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(enabled = btnEnable.value, onClick = {
                            showRewardedAd(context) {

                            }
                        }) {
                            Icon(imageVector = Icons.Default.Movie, contentDescription = "")
                        }
                    }
                })
        },
        content = {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (tableSkinAlert) {
                    var pickedTableSkin by remember { mutableStateOf(userData.value?.skinSettings?.tablePicked) }
                    val ownTableSkins = skinSettings?.ownTableSkins
                    val saleTableSkins = skinSettings?.saleTableSkins

                    SkinAlert(
                        onDismiss = { tableSkinAlert = false },
                        onPick = { skin ->
                            userData.value?.let { userData ->
                                userViewModel.changeSkin(
                                    userData = userData.copy(
                                        skinSettings = userData.skinSettings?.copy(
                                            tablePicked = skin
                                        )
                                    ),
                                )
                            }
                            tableSkinAlert = false
                        },
                        chosenSkin = pickedTableSkin ?: Skin(),
                        onClick = { skin ->
                            pickedTableSkin = skin
                        },
                        onBuy = { skin ->
                            if ((userData.value?.cash ?: 0) >= skin.cash && (userData.value?.coin
                                    ?: 0) >= skin.coin
                            ) {
                                val updateOwnSkins = userData.value?.skinSettings?.ownTableSkins
                                updateOwnSkins?.add(skin)
                                val moneyIcon =
                                    if (skin.cash != 0) R.drawable.birlik_cash else R.drawable.birlik_coin
                                val amount = if (skin.cash != 0) skin.cash else skin.coin
                                userData.value?.let { userData ->
                                    userViewModel.changeSkin(
                                        userData = userData.copy(
                                            skinSettings = userData.skinSettings?.copy(
                                                ownTableSkins = updateOwnSkins,
                                                tablePicked = skin,
                                            ),
                                            cash = userData.cash.minus(skin.cash),
                                            coin = userData.coin.minus(skin.coin),
                                            gameHistory = userData.gameHistory?.apply {
                                                add(
                                                    GameHistory(
                                                        title = "Shop",
                                                        moneyIcon = moneyIcon,
                                                        amount = "-$amount",
                                                        background = R.color.light_red
                                                    )
                                                )
                                            }
                                        ),
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.notEnoughMoney),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        ownSkinList = ownTableSkins ?: mutableListOf(),
                        saleSkinList = saleTableSkins ?: mutableListOf()
                    )
                } else if (cardBackSkinAlert) {
                    var pickedCardBackSkin by remember { mutableStateOf(userData.value?.skinSettings?.cardBackPicked) }
                    val ownCardBackSkins = skinSettings?.ownCardBackSkins
                    val saleCardBackSkins = skinSettings?.saleCardBackSkins

                    SkinAlert(
                        onDismiss = { cardBackSkinAlert = false },
                        onPick = { skin ->
                            userData.value?.let {
                                userViewModel.changeSkin(
                                    userData = it.copy(
                                        skinSettings = it.skinSettings?.copy(
                                            cardBackPicked = skin
                                        )
                                    ),
                                )
                            }
                            cardBackSkinAlert = false
                        },
                        chosenSkin = pickedCardBackSkin ?: Skin(),
                        onClick = { skin ->
                            pickedCardBackSkin = skin
                        },
                        onBuy = { skin ->
                            if ((userData.value?.cash ?: 0) >= skin.cash && (userData.value?.coin
                                    ?: 0) >= skin.coin
                            ) {
                                val updateOwnSkins = userData.value?.skinSettings?.ownCardBackSkins
                                updateOwnSkins?.add(skin)
                                val moneyIcon =
                                    if (skin.cash != 0) R.drawable.birlik_cash else R.drawable.birlik_coin
                                val amount = if (skin.cash != 0) skin.cash else skin.coin

                                userData.value?.let { userData ->
                                    userViewModel.changeSkin(
                                        userData = userData.copy(
                                            skinSettings = userData.skinSettings?.copy(
                                                ownCardBackSkins = updateOwnSkins,
                                                cardBackPicked = skin,
                                            ),
                                            cash = userData.cash.minus(skin.cash),
                                            coin = userData.coin.minus(skin.coin),
                                            gameHistory = userData.gameHistory?.apply {
                                                add(
                                                    GameHistory(
                                                        title = "Shop",
                                                        moneyIcon = moneyIcon,
                                                        amount = "-$amount",
                                                        background = R.color.light_red
                                                    )
                                                )
                                            }
                                        ),
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.notEnoughMoney),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        ownSkinList = ownCardBackSkins ?: mutableListOf(),
                        saleSkinList = saleCardBackSkins ?: mutableListOf()
                    )
                } else if (backgroundSkinAlert) {
                    var pickedBackgroundSkin by remember { mutableStateOf(userData.value?.skinSettings?.backgroundPicked) }
                    val ownBackgroundSkins = skinSettings?.ownBackgroundSkins
                    val saleBackgroundSkins = skinSettings?.saleBackgroundSkins

                    SkinAlert(
                        onDismiss = { backgroundSkinAlert = false },
                        onPick = { skin ->
                            userData.value?.let {
                                userViewModel.changeSkin(
                                    userData = it.copy(
                                        skinSettings = it.skinSettings?.copy(
                                            backgroundPicked = skin
                                        )
                                    ),
                                )
                            }
                            backgroundSkinAlert = false
                        },
                        chosenSkin = pickedBackgroundSkin ?: Skin(),
                        onClick = { skin ->
                            pickedBackgroundSkin = skin
                        },
                        onBuy = { skin ->
                            if ((userData.value?.cash ?: 0) >= skin.cash && (userData.value?.coin
                                    ?: 0) >= skin.coin
                            ) {
                                val updateOwnSkins =
                                    userData.value?.skinSettings?.ownBackgroundSkins
                                updateOwnSkins?.add(skin)
                                val moneyIcon =
                                    if (skin.cash != 0) R.drawable.birlik_cash else R.drawable.birlik_coin
                                val amount = if (skin.cash != 0) skin.cash else skin.coin
                                userData.value?.let { userData ->
                                    userViewModel.changeSkin(
                                        userData = userData.copy(
                                            skinSettings = userData.skinSettings?.copy(
                                                ownBackgroundSkins = updateOwnSkins,
                                                backgroundPicked = skin,
                                            ),
                                            cash = userData.cash.minus(skin.cash),
                                            coin = userData.coin.minus(skin.coin),
                                            gameHistory = userData.gameHistory?.apply {
                                                add(
                                                    GameHistory(
                                                        title = "Shop",
                                                        moneyIcon = moneyIcon,
                                                        amount = "-$amount",
                                                        background = R.color.light_red
                                                    )
                                                )
                                            },
                                        ),
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.notEnoughMoney),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        ownSkinList = ownBackgroundSkins ?: mutableListOf(),
                        saleSkinList = saleBackgroundSkins ?: mutableListOf()
                    )
                }

                Box(modifier = Modifier.fillMaxWidth()) {

                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        val tableSkin = userData.value?.skinSettings?.tablePicked
                        val cardBackSkin = userData.value?.skinSettings?.cardBackPicked?.image

                        Box(
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(80.dp),
                                painter = painterResource(id = tableSkin?.image ?: R.drawable.job),
                                contentDescription = ""
                            )
                            Image(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape),
                                painter = rememberImagePainter(data = userData.value?.image.takeUnless { it.isNullOrEmpty() }
                                    ?: R.drawable.empty_profile),
                                contentScale = ContentScale.Crop,
                                contentDescription = ""
                            )
                        }
                        Image(
                            modifier = Modifier
                                .size(50.dp),
                            painter = painterResource(id = cardBackSkin ?: R.drawable.job),
                            contentDescription = ""
                        )

                    }
                }

                Divider(Modifier.padding(32.dp))
                ItemToChoose(
                    title = stringResource(id = R.string.tableSkin),
                    image = userData.value?.skinSettings?.tablePicked?.image
                        ?: R.drawable.tableskin_black_simple,
                    onClick = {
                        tableSkinAlert = true
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
                ItemToChoose(
                    title = stringResource(id = R.string.cardSkin),
                    image = userData.value?.skinSettings?.cardBackPicked?.image
                        ?: R.drawable.cardback_green,
                    onClick = {
                        cardBackSkinAlert = true
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
                ItemToChoose(
                    title = stringResource(id = R.string.background),
                    image = userData.value?.skinSettings?.backgroundPicked?.image
                        ?: R.drawable.background_green,
                    onClick = {
                        backgroundSkinAlert = true
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(id = R.string.achievements))
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                achievementInfoAlert = true
                            },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,

                        )
                }
                if (achievementInfoAlert) {
                    AlertDialog(
                        title = { Text(text = stringResource(id = R.string.achievements)) },
                        text = {
                            Text(text = stringResource(id = R.string.achievementText), color = MaterialTheme.colorScheme.primary)
                        },
                        onDismissRequest = { achievementInfoAlert = false },
                        confirmButton = {
                            Button(colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background
                            ), onClick = { achievementInfoAlert = false }) {
                                Text(
                                    text = stringResource(id = R.string.close),
                                    color = Color.White
                                )
                            }
                        })
                }
                Spacer(modifier = Modifier.size(16.dp))
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp), colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.rating),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = " | ", color = MaterialTheme.colorScheme.primary)
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "",
                                tint = colorResource(
                                    id = R.color.yellow
                                )
                            )
                            Text(text = "${userData.value?.rating}", color = MaterialTheme.colorScheme.primary)
                        }
                        
                        LazyRow(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            items(trophySkins ?: mutableListOf()) { skin ->
                                if (skin.rating != 0) {
                                    Box(
                                        modifier = Modifier.padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Image(
                                                modifier = Modifier.size(50.dp),
                                                painter = painterResource(
                                                    id = skin.image
                                                        ?: R.drawable.tableskin_black_simple
                                                ), contentDescription = ""
                                            )
                                            Row() {
                                                Icon(
                                                    modifier = Modifier.size(20.dp),
                                                    imageVector = Icons.Default.EmojiEvents,
                                                    contentDescription = "",
                                                    tint = colorResource(
                                                        id = R.color.yellow
                                                    )
                                                )
                                                Text(
                                                    text = "${skin.rating}",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }

                                        if (allOwnSkins.contains(skin)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    modifier = Modifier.size(24.dp),
                                                    imageVector = Icons.Default.Circle,
                                                    contentDescription = "",
                                                    tint = colorResource(
                                                        id = R.color.green
                                                    )
                                                )
                                                Icon(
                                                    modifier = Modifier.size(18.dp),
                                                    imageVector = Icons.Default.Done,
                                                    contentDescription = "",
                                                    tint = Color.White
                                                )
                                            }
                                        }

                                    }

                                }
                            }
                        }
                        Divider(Modifier.padding(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.win),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = " | ", color = MaterialTheme.colorScheme.primary)
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.drawable.victory),
                                contentDescription = ""
                            )
                            Text(text = "${userData.value?.durakWinCount}", color = MaterialTheme.colorScheme.primary)
                        }
                        LazyRow(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(trophySkins ?: mutableListOf()) { skin ->
                                if (skin.winCount != 0) {
                                    Box(
                                        modifier = Modifier.padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Image(
                                                modifier = Modifier.size(50.dp),
                                                painter = painterResource(
                                                    id = skin.image
                                                        ?: R.drawable.tableskin_black_simple
                                                ), contentDescription = ""
                                            )
                                            Row() {
                                                Image(
                                                    modifier = Modifier.size(20.dp),
                                                    painter = painterResource(id = R.drawable.victory),
                                                    contentDescription = ""
                                                )
                                                Text(
                                                    text = "${skin.winCount}",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                        if (allOwnSkins.contains(skin)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    modifier = Modifier.size(24.dp),
                                                    imageVector = Icons.Default.Circle,
                                                    contentDescription = "",
                                                    tint = colorResource(
                                                        id = R.color.green
                                                    )
                                                )
                                                Icon(
                                                    modifier = Modifier.size(18.dp),
                                                    imageVector = Icons.Default.Done,
                                                    contentDescription = "",
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    }

                                }
                            }
                        }

                    }




                }
            }
        }
    )
}

@Composable
private fun ItemToChoose(
    title: String,
    image: Int,
    onClick: () -> Unit,
) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, color = MaterialTheme.colorScheme.primary)
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = image),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun SkinAlert(
    onDismiss: () -> Unit,
    onClick: (Skin) -> Unit,
    onPick: (Skin) -> Unit,
    onBuy: (Skin) -> Unit,
    chosenSkin: Skin,
    ownSkinList: MutableList<Skin>,
    saleSkinList: MutableList<Skin>
) {
    AlertDialog(onDismissRequest = { onDismiss() }, icon = {
        Column {
            LazyVerticalGrid(
                modifier = Modifier.height(150.dp),
                columns = GridCells.Fixed(4),
                content = {
                    items(ownSkinList) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                modifier = Modifier
                                    .size(50.dp)
                                    .border(
                                        1.dp,
                                        if (it == chosenSkin) colorResource(id = R.color.dark_green) else Color.Transparent
                                    )
                                    .clickable {
                                        onClick(it)
                                    },
                                painter = painterResource(
                                    id = it.image ?: R.drawable.tableskin_black_simple
                                ),
                                contentDescription = ""
                            )
                            Text(text = "${it.name}", color = MaterialTheme.colorScheme.primary)
                        }

                    }
                })
            Divider(modifier = Modifier.padding(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(id = R.string.shop),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            LazyVerticalGrid(
                modifier = Modifier.height(150.dp),
                columns = GridCells.Fixed(4),
                content = {
                    items(saleSkinList.sortedBy { ownSkinList.contains(it) }) { saleSkin ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val blackAndWhite = ColorMatrix()
                            blackAndWhite.setToSaturation(0F)
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .border(
                                            1.dp,
                                            if (saleSkin == chosenSkin && !ownSkinList.contains(
                                                    saleSkin
                                                )
                                            ) colorResource(
                                                id = R.color.dark_green
                                            ) else Color.Transparent
                                        )
                                        .clickable {
                                            onClick(saleSkin)
                                        },
                                    painter = painterResource(
                                        id = saleSkin.image ?: R.drawable.tableskin_black_simple
                                    ),
                                    contentDescription = "",
                                    colorFilter = ColorFilter.colorMatrix(
                                        if (ownSkinList.contains(
                                                saleSkin
                                            )
                                        ) blackAndWhite else ColorMatrix()
                                    )
                                )
                                if (ownSkinList.contains(saleSkin)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            modifier = Modifier.size(24.dp),
                                            imageVector = Icons.Default.Circle,
                                            contentDescription = "",
                                            tint = colorResource(
                                                id = R.color.green
                                            )
                                        )
                                        Icon(
                                            modifier = Modifier.size(18.dp),
                                            imageVector = Icons.Default.Done,
                                            contentDescription = "",
                                            tint = Color.White
                                        )
                                    }
                                }

                            }

                            Text(
                                text = "${saleSkin.name}",
                                color = if (ownSkinList.contains(saleSkin)) Color.Gray else MaterialTheme.colorScheme.primary
                            )
                            if (saleSkin.cash != 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        modifier = Modifier.size(14.dp),
                                        painter = painterResource(
                                            id = R.drawable.birlik_cash
                                        ),
                                        contentDescription = "",
                                        colorFilter = ColorFilter.colorMatrix(
                                            if (ownSkinList.contains(
                                                    saleSkin
                                                )
                                            ) blackAndWhite else ColorMatrix()
                                        )
                                    )
                                    Text(
                                        text = formatNumberWithK(saleSkin.cash), color =
                                        if (ownSkinList.contains(saleSkin)) Color.Gray else MaterialTheme.colorScheme.primary
                                    )
                                }
                            } else if (saleSkin.coin != 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        modifier = Modifier.size(14.dp),
                                        painter = painterResource(
                                            id = R.drawable.birlik_coin
                                        ),
                                        contentDescription = "",
                                        colorFilter = ColorFilter.colorMatrix(
                                            if (ownSkinList.contains(
                                                    saleSkin
                                                )
                                            ) blackAndWhite else ColorMatrix()
                                        )
                                    )
                                    Text(
                                        text = formatNumberWithK(saleSkin.coin), color =
                                        if (ownSkinList.contains(saleSkin)) Color.Gray else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                        }

                    }
                })
        }

    }, confirmButton = {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            onClick = {
                if (ownSkinList.contains(chosenSkin)) {
                    onPick(chosenSkin)
                } else {
                    onBuy(chosenSkin)
                }
            }) {
            Text(
                text = stringResource(id = R.string.choose), color = Color.White
            )
        }
    })
}


