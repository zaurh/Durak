package com.zaurh.durak.data.remote.durak

import android.os.Parcel
import android.os.Parcelable
import com.zaurh.durak.R
import com.zaurh.durak.data.remote.UserData


data class DurakData(
    val gameId: String? = null,
    val title: String? = null,
    val kozr: CardPair? = null,
    val kozrSuit: Int? = null,
    val placeOnTable: PlaceOnTable? = null,
    val tableOwner: UserData? = null,
    val selectedCards: List<CardPair> = mutableListOf(),
    var cards: MutableList<CardPair> = mutableListOf(),
    var bita: MutableList<CardPair> = mutableListOf(),
    val playerData: MutableList<PlayerData>? = mutableListOf(),
    val startingPlayer: String? = null,
    val starterTableNumber: Int? = null,

    val started: Boolean? = null,
    val cardsOnHands: Boolean? = null,

    val loser: UserData? = null,
    val choseAttacker: Boolean? = null,
    val attacker: String? = null,
    val tableData: TableData? = TableData(),
    val rules: Rules? = null,
    val finished: Boolean = false,
    val entryPriceCash: Long = 0,
    val entryPriceCoin: Long = 0,
    val rating: Int = 0,
    val timer: Boolean = false

    ) : Parcelable {

    init {
        if (cards.isEmpty() && started == null) {
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
        "starterTableNumber" to starterTableNumber,
        "selectedCards" to selectedCards,
        "placeOnTable" to placeOnTable,
        "choseAttacker" to choseAttacker,
        "tableData" to tableData,
        "attacker" to attacker,
        "rules" to rules,
        "started" to started,
        "cardsOnHands" to cardsOnHands,
        "loser" to loser,
        "finished" to finished,
        "rating" to rating,
        "timer" to timer
    )

    private fun addCards() {
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
        cards = newCards.shuffled().toMutableList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(gameId)
//        parcel.writeValue(kozr)
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
