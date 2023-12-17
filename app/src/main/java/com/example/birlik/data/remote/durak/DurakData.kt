package com.example.birlik.data.remote.durak

import android.os.Parcel
import android.os.Parcelable
import com.example.birlik.R
import com.example.birlik.data.remote.UserData

data class DurakData(
    val gameId: String? = null,
    val kozr: CardPair? = null,
    val kozrSuit: Int? = null,
    val tableOwner: UserData? = null,
    val tableSize: Int? = 2,
    val selectedCards: List<CardPair> = mutableListOf(),
    var cards: MutableList<CardPair> = mutableListOf(),
    var bita: MutableList<CardPair> = mutableListOf(),
    val playerData: MutableList<PlayerData>? = mutableListOf(),
    val startingPlayer: String? = null,
    val starterTableNumber: Int? = null,
    val started: Boolean? = null,
    val winner: UserData? = null,
    val choseAttacker: Boolean? = null,
    val attacker: String? = null,
    val tableData: TableData? = TableData(),
    val round: Boolean? = false,

    val cheat: Boolean? = true

    ) : Parcelable {

    init {
        if (cards.isEmpty()) {
            addCards()
        }
    }


    constructor(parcel: Parcel) : this(
        parcel.readString(),
    )

    fun toMap() = mapOf(
        "playerData" to playerData,
        "cards" to cards,
        "bita" to bita,
        "kozr" to kozr,
        "kozrSuit" to kozrSuit,
        "tableOwner" to tableOwner,
        "startingPlayer" to startingPlayer,
        "tableData" to tableData,
        "attacker" to attacker,
    )

    fun addCards() {
        val newCards = cards +
                CardPair(6, R.drawable.club) +
                CardPair(6, R.drawable.heart) +
                CardPair(6, R.drawable.diamond) +
                CardPair(6, R.drawable.spade) +
                CardPair(7, R.drawable.club) +
                CardPair(7, R.drawable.heart) +
                CardPair(7, R.drawable.diamond) +
                CardPair(7, R.drawable.spade) +
                CardPair(8, R.drawable.club) +
                CardPair(8, R.drawable.heart) +
                CardPair(8, R.drawable.diamond) +
                CardPair(8, R.drawable.spade) +
                CardPair(9, R.drawable.club) +
                CardPair(9, R.drawable.heart) +
                CardPair(9, R.drawable.diamond) +
                CardPair(9, R.drawable.spade) +
                CardPair(10, R.drawable.club) +
                CardPair(10, R.drawable.heart) +
                CardPair(10, R.drawable.diamond) +
                CardPair(10, R.drawable.spade) +
                CardPair(11, R.drawable.club) +
                CardPair(11, R.drawable.heart) +
                CardPair(11, R.drawable.diamond) +
                CardPair(11, R.drawable.spade) +
                CardPair(12, R.drawable.club) +
                CardPair(12, R.drawable.heart) +
                CardPair(12, R.drawable.diamond) +
                CardPair(12, R.drawable.spade) +
                CardPair(13, R.drawable.club) +
                CardPair(13, R.drawable.heart) +
                CardPair(13, R.drawable.diamond) +
                CardPair(13, R.drawable.spade) +
                CardPair(14, R.drawable.club) +
                CardPair(14, R.drawable.heart) +
                CardPair(14, R.drawable.diamond) +
                CardPair(14, R.drawable.spade)
        cards = newCards.toMutableList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(gameId)
        parcel.writeValue(kozr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DurakData> {
        override fun createFromParcel(parcel: Parcel): DurakData {
            return DurakData(parcel)
        }

        override fun newArray(size: Int): Array<DurakData?> {
            return arrayOfNulls(size)
        }
    }

}
