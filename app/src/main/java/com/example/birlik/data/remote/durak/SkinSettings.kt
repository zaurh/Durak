package com.example.birlik.data.remote.durak

import com.example.birlik.R

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
            tablePicked = Skin(name = "Sadə", image = R.drawable.tableskin_black_simple)
            ownTableSkins?.add(Skin(name = "Sadə", image = R.drawable.tableskin_black_simple))

            saleTableSkins?.add(Skin(name = "Qırmızı", image = R.drawable.tableskin_red_simple, cash = 3000))
            saleTableSkins?.add(Skin(name = "Yaşıl", image = R.drawable.tableskin_green_simple, cash = 3000))
            saleTableSkins?.add(Skin(name = "Göy", image = R.drawable.tableskin_blue_simple, cash = 3000))
            saleTableSkins?.add(Skin(name = "Ulduz", image = R.drawable.tableskin_black_stars, coin = 10))

            trophyTableSkins?.add(Skin(name = "Çəhrayı", image = R.drawable.tableskin_pink_simple , winCount = 10))
            trophyTableSkins?.add(Skin(name = "Sarı", image = R.drawable.tableskin_yellow_simple , rating = 100))
            trophyTableSkins?.add(Skin(name = "Çiçək", image = R.drawable.tableskin_flower , winCount = 100))
            trophyTableSkins?.add(Skin(name = "Atəş", image = R.drawable.tableskin_fire , rating = 1000))
            trophyTableSkins?.add(Skin(name = "Şimşək", image = R.drawable.tableskin_lightning , winCount = 500))
            trophyTableSkins?.add(Skin(name = "Buz", image = R.drawable.tableskin_ice , rating = 5000))

        }
    }

    private fun addDefaultCardBackSkin() {
        if (ownCardBackSkins.isNullOrEmpty()) {
            cardBackPicked = Skin(name = "Sadə", image = R.drawable.cardback_black, cash = 0)
            ownCardBackSkins?.add(Skin(name = "Sadə", image = R.drawable.cardback_black, cash = 0))
            saleCardBackSkins?.add(Skin(name = "Göy", image = R.drawable.cardback_blue, cash = 3000))
            saleCardBackSkins?.add(Skin(name = "Yaşıl", image = R.drawable.cardback_green, cash = 3000))
            saleCardBackSkins?.add(Skin(name = "Qırmızı", image = R.drawable.cardback_red, coin = 10))
        }
    }

    private fun addDefaultBackgroundSkin() {
        if (ownBackgroundSkins.isNullOrEmpty()) {
            backgroundPicked = Skin(name = "Sadə", image = R.drawable.background_green, cash = 0)
            ownBackgroundSkins?.add(Skin(name = "Sadə", image = R.drawable.background_green, cash = 0))
            saleBackgroundSkins?.add(Skin(name = "Göy", image = R.drawable.background_blue, cash = 5000))
            saleBackgroundSkins?.add(Skin(name = "Qırmızı", image = R.drawable.background_red, coin = 50))
        }
    }

}
