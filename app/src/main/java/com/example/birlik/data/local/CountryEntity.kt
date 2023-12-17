package com.example.birlik.data.local

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countryEntity")
class CountryEntity(
    @ColumnInfo(name = "name") val name: String? = null,
    @ColumnInfo(name = "image") val image: Int? = null,
    @ColumnInfo(name = "users") val users: String? = null,
): Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
        id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeValue(image)
        parcel.writeString(users)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CountryEntity> {
        override fun createFromParcel(parcel: Parcel): CountryEntity {
            return CountryEntity(parcel)
        }

        override fun newArray(size: Int): Array<CountryEntity?> {
            return arrayOfNulls(size)
        }
    }
}