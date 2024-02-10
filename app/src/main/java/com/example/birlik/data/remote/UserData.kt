package com.example.birlik.data.remote

import android.os.Parcel
import android.os.Parcelable
import com.example.birlik.data.remote.durak.DurakSettings
import com.example.birlik.data.remote.durak.SkinSettings
import com.google.firebase.Timestamp

data class UserData(
    val userId: String? = null,
    val username: String? = null,
    val image: String? = null,
    val name: String? = null,
    val bio: String? = null,
    val country: CountryData? = null,
    val status: String? = null,
    val lastSeen: Timestamp? = null,
    val durakSettings: DurakSettings? = DurakSettings(),
    val coin: Int = 0,
    val respect: Int = 0,
    val cash: Int = 0,
    val rating: Int = 0,
    val skinSettings: SkinSettings? = SkinSettings(),
    val gameHistory: MutableList<GameHistory>? = mutableListOf(),
    val durakWinCount: Int = 0,
    val durakLoseCount: Int = 0,

    ) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(CountryData::class.java.classLoader),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),

    ) {
    }

    fun toMap() = mapOf(
        "userId" to userId,
        "username" to username,
        "image" to image,
        "country" to country,
        "name" to name,
        "bio" to bio,
        "status" to status,
        "durakSettings" to durakSettings,
        "coin" to coin,
        "cash" to cash,
        "respect" to respect,
        "rating" to rating,
        "skinSettings" to skinSettings,
        "gameHistory" to gameHistory,
        "durakWinCount" to durakWinCount,
        "durakLoseCount" to durakLoseCount
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeString(bio)
        parcel.writeParcelable(country, flags)
        parcel.writeString(status)
        parcel.writeParcelable(lastSeen, flags)
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