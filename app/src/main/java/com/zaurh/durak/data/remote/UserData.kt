package com.zaurh.durak.data.remote

import android.os.Parcel
import android.os.Parcelable
import com.zaurh.durak.data.remote.durak.DurakSettings
import com.zaurh.durak.data.remote.durak.SkinSettings

data class UserData(
    val userId: String? = null,
    val email: String? = null,
    val image: String? = null,
    val name: String? = null,
    val durakSettings: DurakSettings? = DurakSettings(),
    val coin: Int = 0,
    val respect: Int = 0,
    val cash: Int = 0,
    val rating: Int = 0,
    val skinSettings: SkinSettings? = SkinSettings(),
    val gameHistory: MutableList<GameHistory>? = mutableListOf(),
    val durakWinCount: Int = 0,
    val durakLoseCount: Int = 0,
    val promoList: List<String>? = listOf()

) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    ) {
    }

    fun toMap() = mapOf(
        "userId" to userId,
        "email" to email,
        "image" to image,
        "name" to name,
        "durakSettings" to durakSettings,
        "coin" to coin,
        "cash" to cash,
        "respect" to respect,
        "rating" to rating,
        "skinSettings" to skinSettings,
        "gameHistory" to gameHistory,
        "durakWinCount" to durakWinCount,
        "durakLoseCount" to durakLoseCount,
        "promoList" to promoList
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(email)
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeValue(coin)
        parcel.writeValue(respect)
        parcel.writeValue(cash)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }


}