package com.example.birlik.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birlik.data.local.CountryEntity
import com.example.birlik.data.repository.RoomRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepo: RoomRepo
): ViewModel() {

    val countryData = roomRepo.countryData
    val selectedCountries = mutableStateListOf<CountryEntity>()

    init {
        getCountries()
    }

    private fun getCountries() {
        viewModelScope.launch {
            roomRepo.getAllCountries()
        }
    }

    fun addCountry(countryEntity: CountryEntity) {
        viewModelScope.launch {
            roomRepo.addCountry(countryEntity)
        }
        getCountries()
    }

    fun deleteCountry(countryEntity: CountryEntity) {
        viewModelScope.launch {
            roomRepo.deleteCountry(countryEntity)
            getCountries()
        }
    }

    fun searchCountry(query: String) {
        viewModelScope.launch {
            roomRepo.searchCountry(query)
        }
    }

    fun clearSearch() {
        getCountries()
    }
}