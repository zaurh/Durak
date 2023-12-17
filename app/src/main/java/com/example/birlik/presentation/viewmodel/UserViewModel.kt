package com.example.birlik.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.data.remote.durak.PlayerData
import com.example.birlik.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {

    val userData = userRepo.userData
    val playerData = userRepo.playerData

    val currentUserId = userRepo.currentUserId
    val onlineUsers = userRepo.onlineUsers
    val durakTables = userRepo.durakTables
    val players = userRepo.players
    val durakData = userRepo.durak

    val remainingCards = userRepo.remainingCards


    fun updateUserData(
        userData: UserData
    ) {
        userRepo.updateUser(
            userData
        )
    }

    fun getUserData(userId: String) {
        userRepo.getUserData(userId = userId)
    }


    fun oyunuBaslat(durakData: DurakData, tableOwner: UserData) {
        userRepo.oyunuBaslat(durakData, tableOwner)
    }

    fun yereKartDus(
        durakData: DurakData,
        playerData: PlayerData,
        selectedCard: CardPair,
        rotate: Boolean = false
    ) {
        userRepo.yereKartDus(durakData, playerData, selectedCard, rotate)
    }

    fun eleYig(durakData: DurakData, playerData: PlayerData, selectedCards: List<CardPair>) {
        userRepo.eleYig(durakData, playerData, selectedCards)
    }

    fun getDurakData(gameId: String) {
        userRepo.getDurakData(gameId)
    }

    fun sitTable(durakData: DurakData, playerData: PlayerData, tableNumber: Int) {
        userRepo.stolaOtur(durakData, playerData, tableNumber)
    }

    fun updateDurakData(update: (DurakData) -> DurakData) {
        userRepo.updateDurakData(update)
    }

    fun updateDurakCards(durakData: DurakData, cards: MutableList<CardPair>) {
        userRepo.updateDurakCards(durakData, cards)
    }

    fun oyuncularaKartPayla(
        durakData: DurakData,
        players: MutableList<PlayerData>,
        originalList: List<CardPair>,
        kozr: CardPair,
        remainingCards: List<CardPair>,
        onComplete: (List<CardPair>) -> Unit,
        onSuccess: () -> Unit,
    ) {
        userRepo.oyuncularaKartPayla(
            durakData,
            players,
            originalList,
            kozr,
            remainingCards,
            onComplete,
            onSuccess
        )
    }

    fun yerdenKartGotur(
        durakData: DurakData,
        player: PlayerData,
        playerDataList: List<PlayerData>,
        card: CardPair
    ) {
        userRepo.yerdenKartGotur(durakData, player, playerDataList, card)
    }

    fun kozrGotur(durakData: DurakData) {
        userRepo.kozrGotur(durakData)
    }


    fun bitayaGetsin(
        durakData: DurakData,
        playerData: List<PlayerData>,
        cards: List<CardPair>,
    ) {
        userRepo.bitayaGetsin(durakData, playerData, cards)
    }

    fun deleteAllGames() {
        userRepo.deleteAllGames()
    }

    fun startListeningForDurakUpdates(
        durakData: DurakData
    ) {
        userRepo.startListeningForDurakUpdates(durakData)
    }

    fun updateOyuncuSirasi(durakData: DurakData, starter: String, starterTableNumber: Int) {
        userRepo.updateOyuncuSirasi(durakData, starter, starterTableNumber)
    }

    fun winGame(durakData: DurakData, userData: UserData, onSuccess: () -> Unit){
        userRepo.winGame(durakData, userData, onSuccess)
    }


    }