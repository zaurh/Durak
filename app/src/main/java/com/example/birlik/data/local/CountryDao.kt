package com.example.birlik.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CountryDao {
    @Insert
    suspend fun Insert(countryEntity: CountryEntity)

    @Delete
    suspend fun Delete(countryEntity: CountryEntity)

    @Query("SELECT * FROM countryEntity")
    suspend fun getAllCountries():List<CountryEntity>

    @Query ("SELECT * FROM countryEntity WHERE name like '%' || :searchQuery || '%'")
    suspend fun searchCountry(searchQuery:String):List<CountryEntity>
}