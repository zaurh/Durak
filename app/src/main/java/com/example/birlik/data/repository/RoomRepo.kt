package com.example.birlik.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.birlik.data.local.CountryDao
import com.example.birlik.data.local.CountryEntity
import javax.inject.Inject

class RoomRepo @Inject constructor(
    private val dao: CountryDao
) {
    var countryData = MutableLiveData<List<CountryEntity>>()

    init {
        countryData = MutableLiveData()
    }

    suspend fun addCountry(countryEntity: CountryEntity){
        dao.Insert(countryEntity)
    }
    suspend fun deleteCountry(countryEntity: CountryEntity){
        dao.Delete(countryEntity)
    }
    suspend fun getAllCountries(){
        countryData.value = dao.getAllCountries()
    }

    suspend fun searchCountry(query: String) {
        countryData.value = dao.searchCountry(query)
    }
}