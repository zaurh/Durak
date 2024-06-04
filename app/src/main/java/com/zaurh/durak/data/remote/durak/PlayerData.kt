package com.zaurh.durak.data.remote.durak

import android.os.Parcel
import android.os.Parcelable
import com.zaurh.durak.data.remote.UserData

data class PlayerData(
    val playerId: String? = null,
    val userData: UserData? = null,
    val cards: List<CardPair>? = emptyList(),
    val lastDroppedCardNum: CardPair? = null,
    var selectedCard: List<CardPair>? = emptyList(),
    val tableNumber: Int? = null,
): Parcelable {

    fun toMap() = mapOf(
        "playerId" to playerId,
        "userData" to userData?.toMap(),
        "cards" to cards,
        "lastDroppedCardNum" to lastDroppedCardNum,
        "selectedCard" to selectedCard,
        "tableNumber" to tableNumber,
    )

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(UserData::class.java.classLoader),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(userData, flags)
        parcel.writeValue(selectedCard)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayerData> {
        override fun createFromParcel(parcel: Parcel): PlayerData {
            return PlayerData(parcel)
        }

        override fun newArray(size: Int): Array<PlayerData?> {
            return arrayOfNulls(size)
        }
    }
}
