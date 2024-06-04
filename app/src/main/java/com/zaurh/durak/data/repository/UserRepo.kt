package com.zaurh.durak.data.repository

import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.zaurh.durak.R
import com.zaurh.durak.data.remote.GameHistory
import com.zaurh.durak.data.remote.PromoData
import com.zaurh.durak.data.remote.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData

    private val isUserLoading = mutableStateOf(false)


    private val promoData = mutableStateOf<PromoData?>(null)


    val usersData = mutableStateOf<List<UserData>>(emptyList())

    init {
        getAllUsers()
    }


    fun addUser(userData: UserData) {
        firestore.collection("user").document(userData.userId ?: "").set(userData)
            .addOnSuccessListener {
                isUserLoading.value = false
            }
            .addOnFailureListener {
                isUserLoading.value = false
            }
    }

    fun getUserData(userId: String) {
        firestore.collection("user").document(userId).addSnapshotListener { value, _ ->
            value?.let {
                this._userData.value = it.toObject<UserData>()
            }
        }
    }


    private fun getAllUsers() {
        firestore.collection("user")
            .addSnapshotListener { value, _ ->
                value?.let {
                    usersData.value =
                        it.toObjects()
                }
            }
    }

    fun updateUser(
        userData: UserData
    ) {
        firestore.collection("user").document(userData.userId ?: "").update(userData.toMap())
            .addOnSuccessListener {
                this._userData.value = userData
            }
    }

    fun rewardUser(
        userData: UserData,
    ) {
        val updatedUserData = userData.copy(
            cash = userData.cash + 5000,
            gameHistory = userData.gameHistory?.apply {
                add(
                    GameHistory(
                        title = "Ad watched",
                        moneyIcon = R.drawable.birlik_cash,
                        background = R.color.light_green,
                        amount = "+5000"
                    )
                )
            }
        )
        firestore.collection("user").document(userData.userId ?: "").update(updatedUserData.toMap())
            .addOnSuccessListener {
                this._userData.value = updatedUserData
            }
    }




    fun changeSkin(userData: UserData) {
        firestore.collection("user").document(userData.userId ?: "").update(userData.toMap())
            .addOnSuccessListener {
                this._userData.value = userData
            }
    }

    fun getPromo(
        code: String,
        onSuccess: (Int, Int) -> Unit,
        onFailure: (Exception) -> Unit,
        onAlreadyApplied: () -> Unit
    ) {
        firestore.collection("promos")
            .whereEqualTo("code", code)
            .addSnapshotListener { value, error ->
                val promoDocument = value?.firstOrNull()
                if (promoDocument != null) {
                    this.promoData.value = promoDocument.toObject()
                    updateUser(
                        userData = userData.value?.copy(
                            cash = userData.value?.cash?.plus(promoData.value?.cash ?: 0) ?: 0,
                            coin = userData.value?.coin?.plus(promoData.value?.coin ?: 0) ?: 0,
                            promoList = if (userData.value?.promoList?.contains(code) == true){
                                onAlreadyApplied()
                                return@addSnapshotListener
                            }else{
                                userData.value?.promoList?.plus(code)
                            }
                        )?: UserData()
                    )
                    onSuccess(
                        promoData.value?.cash ?: 0, promoData.value?.coin ?: 0
                    )
                } else {
                    onFailure(Exception(error))
                }
            }
    }
}

