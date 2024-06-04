package com.zaurh.durak.data.remote.durak

import com.zaurh.durak.R

data class SkinSettings(
    val ownTableSkins: MutableList<Skin>? = mutableListOf(),
    val saleTableSkins: MutableList<Skin>? = mutableListOf(),
    val trophyTableSkins: MutableList<Skin>? = mutableListOf(),
    var tablePicked: Skin? = null,

    val ownCardBackSkins : MutableList<Skin>? = mutableListOf(),
    val saleCardBackSkins : MutableList<Skin>? = mutableListOf(),
    var cardBackPicked: Skin? = null,

    val ownBackgroundSkins: MutableList<Skin>? = mutableListOf(),
    val saleBackgroundSkins: MutableList<Skin>? = mutableListOf(),
    var backgroundPicked: Skin? = null,

){
    init {
        addDefaultTableSkin()
        addDefaultCardBackSkin()
        addDefaultBackgroundSkin()
    }

    private fun addDefaultTableSkin() {
        if (ownTableSkins.isNullOrEmpty()) {
            tablePicked = Skin(name = "Default", image = R.drawable.tableskin_black_simple)
            ownTableSkins?.add(Skin(name = "Default", image = R.drawable.tableskin_black_simple))

            saleTableSkins?.add(Skin(name = "Red", image = R.drawable.tableskin_red_simple, cash = 30000))
            saleTableSkins?.add(Skin(name = "Green", image = R.drawable.tableskin_green_simple, cash = 30000))
            saleTableSkins?.add(Skin(name = "Blue", image = R.drawable.tableskin_blue_simple, cash = 30000))
            saleTableSkins?.add(Skin(name = "Star", image = R.drawable.tableskin_black_stars, coin = 300))

            trophyTableSkins?.add(Skin(name = "Pink", image = R.drawable.tableskin_pink_simple , winCount = 10))
            trophyTableSkins?.add(Skin(name = "Yellow", image = R.drawable.tableskin_yellow_simple , rating = 100))
            trophyTableSkins?.add(Skin(name = "Flower", image = R.drawable.tableskin_flower , winCount = 100))
            trophyTableSkins?.add(Skin(name = "Fire", image = R.drawable.tableskin_fire , rating = 1000))
            trophyTableSkins?.add(Skin(name = "Shock", image = R.drawable.tableskin_lightning , winCount = 500))
            trophyTableSkins?.add(Skin(name = "Ice", image = R.drawable.tableskin_ice , rating = 5000))

        }
    }

    private fun addDefaultCardBackSkin() {
        if (ownCardBackSkins.isNullOrEmpty()) {
            cardBackPicked = Skin(name = "Default", image = R.drawable.cardback_black, cash = 0)
            ownCardBackSkins?.add(Skin(name = "Default", image = R.drawable.cardback_black, cash = 0))
            saleCardBackSkins?.add(Skin(name = "Blue", image = R.drawable.cardback_blue, cash = 50000))
            saleCardBackSkins?.add(Skin(name = "Green", image = R.drawable.cardback_green, cash = 50000))
            saleCardBackSkins?.add(Skin(name = "Red", image = R.drawable.cardback_red, coin = 300))
        }
    }

    private fun addDefaultBackgroundSkin() {
        if (ownBackgroundSkins.isNullOrEmpty()) {
            backgroundPicked = Skin(name = "Default", image = R.drawable.background_green, cash = 0)
            ownBackgroundSkins?.add(Skin(name = "Default", image = R.drawable.background_green, cash = 0))
            saleBackgroundSkins?.add(Skin(name = "Blue", image = R.drawable.background_blue, cash = 100000))
            saleBackgroundSkins?.add(Skin(name = "Red", image = R.drawable.background_red, cash = 100000))
        }
    }

}
