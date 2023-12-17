package com.example.birlik.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CountryEntity::class], version = 1)
abstract class CountryDatabase: RoomDatabase() {
    abstract fun countryDao():CountryDao
}