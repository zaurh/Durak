package com.example.birlik.data

import com.example.birlik.data.local.CountryEntity
import com.example.birlik.data.remote.CountryData

fun CountryData.toCountryEntity() = CountryEntity(
    name, image, users
)

fun CountryEntity.toCountryData() = CountryData(
    name, image, users
)